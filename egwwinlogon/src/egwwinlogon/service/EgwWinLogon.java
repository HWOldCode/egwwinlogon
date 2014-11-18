package egwwinlogon.service;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareBrowser;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import egwwinlogon.winapi.MoreAdvApi32;
import egwwinlogon.winapi.io.PipeProcess;
import egwwinlogon.winapi.io.WindowsNamedPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EgwWinLogon
 * @author Stefan Werfling
 */
public class EgwWinLogon {

    private static final Logger logger = LoggerFactory.getLogger(EgwWinLogon.class);

	/**
	 * Global Error
	 */
	static protected  String _error = "";

    /**
     * PipeProcess
     */
    protected PipeProcess _pipeServer = null;

    /**
     * Egroupware Config
     */
    protected EgroupwareConfig _egwConfig = null;

    /**
     * Egroupware
     */
    protected Egroupware _egw = null;

	/**
	 * main
	 * @param args String[]
	 */
	public static void main(String[] args) {
        /*Egroupware egw = Egroupware.getInstance(new EgroupwareConfig(
            "https://www.hw-softwareentwicklung.de/egroupware/",
            "default",
            "",
            ""
            ));

        try {
            System.out.println(egw.getLoginDomains());
            egw.login();
            System.out.println(egw.getSession().getLastLoginId());
            EgroupwareBrowser.open(egw);
        }
        catch( Exception e ) {
            System.out.println();
        }

        System.out.println("test");*/
	}

    /**
     * initEgroupware
     *
     * @param url
     * @param domain
     */
    public void initEgroupware(String url, String domain) {
        logger.info("initEgroupware, init egroupware by url: " + url + " domain: " + domain);

        this._egwConfig = new EgroupwareConfig();
        this._egwConfig.setUrl(url);
        this._egwConfig.setDomain(domain);

        if( this._pipeServer == null ) {
            this._pipeServer = new PipeProcess("egroupware");
        }
    }

    /**
     * egwAuthenticateUser
     *
     * @param username
     * @param password
     * @return
     */
	public int egwAuthenticateUser(String username, String password) {
        logger.info("egwAuthenticateUser, username: " + username);

        if( this._egwConfig == null ) {
            return 0;
        }

        this._egwConfig.setUser(username);
        this._egwConfig.setPassword(password);

        this._egw = Egroupware.getInstance(this._egwConfig);

        try {
            this._egw.login();

            if( this._egw.isLogin() ) {
                //EgroupwareBrowser.open(this._egw);  // test
                return 1;
            }
        }
        catch( Exception e ) {
            logger.info("egwAuthenticateUser, Exception: " + e.getMessage());
            EgwWinLogon._error = e.getMessage();
        }

        return 0;
	}

    protected void createEgroupwareUserProcess() {
        logger.info("createEgroupwareUserProcess");

        WString nullW = null;
        PROCESS_INFORMATION processInformation = new PROCESS_INFORMATION();

        STARTUPINFO startupInfo = new STARTUPINFO();
        startupInfo.lpDesktop = "winsta0\\default";

        String usernamer = this._egwConfig.getUser();
        String password = this._egwConfig.getPassword();

        logger.info("createEgroupwareUserProcess, username: " + usernamer + " password: " + password);

        //http://de.slideshare.net/dblockdotorg/waffle-windows-authentication-in-java ?
        //http://codenav.org/code.html?project=/net/java/dev/jna/jna/3.3.0&path=/Source%20Packages/test.com.sun.jna.platform.win32/Advapi32Test.java ?
        boolean result = MoreAdvApi32.INSTANCE.CreateProcessWithLogonW(
            new WString(usernamer),                         // user
            nullW,                                           // domain , null if local
            new WString(password),                           // password
            MoreAdvApi32.LOGON_WITH_PROFILE,                 // dwLogonFlags
            nullW,                                           // lpApplicationName
            new WString("c:\\windows\\system32\\cmd.exe"),   // command line
            MoreAdvApi32.CREATE_NEW_CONSOLE,                 // dwCreationFlags
            null,                                            // lpEnvironment
            new WString("c:"),                               // directory
            startupInfo,
            processInformation);

        if (!result) {
          int error = Kernel32.INSTANCE.GetLastError();

          logger.info("createEgroupwareUserProcess, OS error #" + error);
          logger.info("createEgroupwareUserProcess, " + Kernel32Util.formatMessageFromLastErrorCode(error));
          //System.out.println("OS error #" + error);
          //System.out.println(Kernel32Util.formatMessageFromLastErrorCode(error));
        }
        else {
          logger.info("createEgroupwareUserProcess, erfolgreich gestartet");
        }
    }

    /**
     * egwSessionChange
     * @param sessionChangeReason
     */
    public void egwSessionChange(int sessionChangeReason) {
        logger.info("egwSessionChange, sessionChangeReason: " + Integer.toString(sessionChangeReason));

        try {
            switch( sessionChangeReason ) {
                case 5: // SessionLogon
                    if( this._egw.isLogin() ) {
                        if( this._pipeServer != null ) {
                            logger.info("egwSessionChange, pipeServer object");

                            this._pipeServer.getOutputStream().write(
                                new String("SessionLogon").getBytes());
                        }

                        this.createEgroupwareUserProcess();
                    }
                    else {
                        EgwWinLogon._error = "please login in egroupware";
                    }
                    break;

                case 6:
                    break;

                default:
                    EgwWinLogon._error = "none egroupware change";
            }
        }
        catch( Exception e ) {
            logger.info("egwSessionChange, Exception: " + e.getMessage());
            EgwWinLogon._error = e.getMessage();
        }
    }

    /**
     * egwGetError
     * @return
     */
	public String egwGetError() {
        String terror = EgwWinLogon._error;

        EgwWinLogon._error = "";

		return terror;
	}

    /**
     * egwIsError
     * @return
     */
	public int egwIsError() {
        if( !"".equals(EgwWinLogon._error) ) {
            return 1;
        }

		return 0;
	}

	public void egwStarting() {

	}

	public void egwStopping() {

	}

	public String[] egwGetLogs() {
		return null;
	}

	/**
	 * egwGetDescription
	 * @return String
	 */
	public String egwGetDescription() {
		return "Egroupware CP Connector";
	}

	/**
	 * egwGetName
	 * @return String
	 */
	public String egwGetName() {
		return "Egroupware";
	}

	/**
	 * egwGetVersion
	 * @return String
	 */
	public String egwGetVersion() {
		return "14.1";
	}

	public int egwAuthenticatedUserGateway() {
		return 0;
	}

	public int egwSessionChange() {
		return 0;
	}
}
