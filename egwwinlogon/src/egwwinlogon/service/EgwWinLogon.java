package egwwinlogon.service;

import egwwinlogon.service.db.EgwWinLogonDb;
import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareBrowser;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import egwwinlogon.egroupware.EgroupwareELoginCache;
import egwwinlogon.http.LogonHttpServer;
import egwwinlogon.service.db.EgwWinLogonDbConnection;
import egwwinlogon.user.EgwWinLogonClient;
import java.io.IOException;
import java.util.logging.Level;
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
     * LogonHttpServer
     */
    protected LogonHttpServer _server = null;

    /**
     * Egroupware Config
     */
    protected EgroupwareConfig _egwConfig = null;

    /**
     * Egroupware
     */
    protected Egroupware _egw = null;

    /**
     * EgroupwareELoginCache
     */
    protected EgroupwareELoginCache _eLoginCache = null;

    /**
     * EgwWinLogonDb
     */
    protected EgwWinLogonDb _db = null;

	/**
	 * main
	 * @param args String[]
	 */
	public static void main(String[] args) {
        try {
            /*Egroupware tegw = Egroupware.getInstance(new EgroupwareConfig(
                "http://dev.hw-softwareentwicklung.de/egroupware/",
                "default",
                "admin2",
                "test"
                ));

            tegw.login();

            EgroupwareELoginCache mycache = new EgroupwareELoginCache();

            tegw.request(mycache);

            if( mycache.existUsername("admin2") ) {
                System.out.println("Existiert!");
            }
            else {
                System.out.println("Nicht :O");
            }

            if( mycache.compareUsernamePassword("admin2", "test") ) {
                System.out.println("Passwort richtig");
            }
            else {
                System.out.println("Passwort falsch");
            }*/
            EgwWinLogon egw = new EgwWinLogon();
            egw.initEgroupware("http://dev.hw-softwareentwicklung.de/egroupware/", "default");
            egw.egwStarting();
            egw.egwAuthenticateUser("admin2", "test");
            //EgwWinLogonClient tclient = new EgwWinLogonClient();
            //tclient.getEgroupwareInstance("admin2");

            Thread.sleep(10000);
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
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

    /**
     * initEgroupware
     *
     * @param url
     * @param domain
     */
    public void initEgroupware(String url, String domain) {
        logger.info("initEgroupware, init egroupware by url: " + url + " domain: " + domain);

        this._db = new EgwWinLogonDb();
        this._db.start();

        this._egwConfig = new EgroupwareConfig();
        this._egwConfig.setUrl(url);
        this._egwConfig.setDomain(domain);

        //this._eLoginCache = EgroupwareELoginCache.loadByFile("elogin.cache");

        if( this._eLoginCache == null ) {
            this._eLoginCache = new EgroupwareELoginCache();
        }

        if( this._server == null ) {
            this._server = new LogonHttpServer();

            try {
                this._server.init();
            } catch( IOException ex ) {
                java.util.logging.Logger.getLogger(
                    EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            }

            EgwWinLogonHttpHandlerConfig config = new EgwWinLogonHttpHandlerConfig();
            config.register(this._server);

            EgwWinLogonHttpHandlerLogger httplogger = new EgwWinLogonHttpHandlerLogger();
            httplogger.register(this._server);

            EgwWinLogonHttpHandlerSession session = new EgwWinLogonHttpHandlerSession();
            session.register(this._server);
            //logger.get

            try {
                this._server.start();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(
                    EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                this._egw.request(this._eLoginCache);

                if( this._eLoginCache.countAccounts() > 0 ) {
                    EgwWinLogonDbConnection _con = this._db.getNewConnection();

                    //_con.query("drop table barcodes if exists;");
                    _con.query("create table barcodes (id integer, barcode varchar(20) not null);");
                    _con.query("insert into barcodes (id, barcode) values (1, '12345566');");
                    //EgroupwareELoginCache.saveToFile(this._eLoginCache, "elogin.cache");
                }

                return 1;
            }
        }
        catch( java.net.SocketTimeoutException e ) {
            // login by offline mode, username + password check by cachelist
            if( this._eLoginCache.countAccounts() > 0 ) {
                if( this._eLoginCache.compareUsernamePassword(username, password) ) {
                    return 1;
                }
            }
        }
        catch( Exception e ) {
            logger.info("egwAuthenticateUser, Exception: " + e.getMessage());
            EgwWinLogon._error = e.getMessage();
        }

        return 0;
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
                        if( this._server != null ) {

                        }

                        //this.createEgroupwareUserProcess();
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
        if( this._server != null ) {

        }
	}

	public void egwStopping() {
        if( this._server != null ) {
            this._server.stop();
        }
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
