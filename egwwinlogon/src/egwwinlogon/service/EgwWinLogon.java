package egwwinlogon.service;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareBrowser;
import com.jegroupware.egroupware.EgroupwareConfig;

/**
 * EgwWinLogon
 * @author Stefan Werfling
 */
public class EgwWinLogon {

	/**
	 * Global Error
	 */
	static protected  String _error = "";

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
        this._egwConfig = new EgroupwareConfig();
        this._egwConfig.setUrl(url);
        this._egwConfig.setDomain(domain);
    }

    /**
     * egwAuthenticateUser
     *
     * @param username
     * @param password
     * @return
     */
	public int egwAuthenticateUser(String username, String password) {
        if( this._egwConfig == null ) {
            return 0;
        }

        this._egwConfig.setUser(username);
        this._egwConfig.setPassword(password);

        this._egw = Egroupware.getInstance(this._egwConfig);

        try {
            this._egw.login();

            if( this._egw.isLogin() ) {
                EgroupwareBrowser.open(this._egw);  // test
                return 1;
            }
        }
        catch( Exception e ) {
            EgwWinLogon._error = e.getMessage();
        }

        return 0;
	}

    /**
     * egwSessionChange
     * @param sessionChangeReason
     */
    public void egwSessionChange(int sessionChangeReason) {
        try {
            switch( sessionChangeReason ) {
                case 5: // SessionLogon
                    if( this._egw.isLogin() ) {
                        EgroupwareBrowser.open(this._egw);
                        EgwWinLogon._error = "open browser";
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
