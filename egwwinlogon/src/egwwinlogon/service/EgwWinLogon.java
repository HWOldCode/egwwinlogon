package egwwinlogon.service;

import egwwinlogon.service.db.EgwWinLogonDb;
import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareConfig;
import egwwinlogon.egroupware.EgroupwareCommand;
import egwwinlogon.egroupware.EgroupwareELoginCache;
import egwwinlogon.http.LogonHttpServer;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EgwWinLogon
 * @author Stefan Werfling
 */
public class EgwWinLogon {

    /**
     * logger
     */
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
     * _settings
     */
    protected LinkedHashMap _settings = new LinkedHashMap();
    
    /**
     * Egroupware Configs
     */
    protected LinkedHashMap _egwConfigs = new LinkedHashMap();

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
            //EgwWinLogon egw = new EgwWinLogon();
            //egw.setSetting("url", "http://dev.hw-softwareentwicklung.de/egroupware/");
            //egw.setSetting("domain", "default");
            //egw.initEgroupware();
            //egw.egwStarting();
            //egw.egwAuthenticateUser("admin2", "test");
            
            //egw.initEgroupware("http://dev.hw-softwareentwicklung.de/egroupware/", "default", "test");
            //egw.egwStarting();
            //egw.egwAuthenticateUser("admin2", "test", "99");
            //EgwWinLogonClient tclient = new EgwWinLogonClient();
            //tclient.getEgroupwareInstance("admin2");

            /*EgroupwareELoginCache test = EgroupwareELoginCache.loadByFile("elogin.cache");
            String username = "artur.skuratowicz";
            String password = "";
            
            if( test.countAccounts() > 0 ) {
                // is activ and expries
                if( test.isStatusA(username) && test.isAccountExpires(username) ) {
                    // check password
                    if( test.compareUsernamePassword(username, password) ) {
                        System.out.println("Drin");
                    }
                }
            }
            
            Thread.sleep(10000);*/
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
     */
    public void initEgroupware() {
        logger.info("initEgroupware, init egroupware objects");

        // ---------------------------------------------------------------------
        
        this._eLoginCache = EgroupwareELoginCache.loadByFile("elogin.cache");

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

        EgroupwareConfig config = null;
        
        if( this._egwConfigs.containsKey(username) ) {
            config = (EgroupwareConfig) _egwConfigs.get(username);
            
            this._egwConfigs.remove(username);
        }
        else {
            config = new EgroupwareConfig();
        }

        config.setUrl((String) this._settings.get("url"));
        config.setDomain((String) this._settings.get("domain"));
        config.setUser(username);
        config.setPassword(password);

        this._egwConfigs.put(username, config);
        
        // ---------------------------------------------------------------------
        
        Egroupware _egw = Egroupware.getInstance(config);

        try {
            _egw.login();

            if( _egw.isLogin() ) {
                
                // send login command
                _egw.request(new EgroupwareCommand(
                    EgroupwareCommand.EGW_CMD_LOGIN, 
                    this._settings.get("machinename") + ";" + 
                        this._settings.get("sysfingerprint")
                    ));
                
                // request login cache list
                _egw.request(this._eLoginCache);

                if( this._eLoginCache.countAccounts() > 0 ) {
                    //EgwWinLogonDbConnection _con = this._db.getNewConnection();

                    //_con.query("drop table barcodes if exists;");
                    //_con.query("create table barcodes (id integer, barcode varchar(20) not null);");
                    //_con.query("insert into barcodes (id, barcode) values (1, '12345566');");
                    EgroupwareELoginCache.saveToFile(this._eLoginCache, "elogin.cache");
                }

                return 1;
            }
        }
        catch( java.net.SocketTimeoutException | java.net.UnknownHostException  e ) {
            // login by offline mode, username + password check by cachelist
            if( this._eLoginCache.countAccounts() > 0 ) {
                // is activ and expries
                if( this._eLoginCache.isStatusA(username) && this._eLoginCache.isAccountExpires(username) ) {
                    // check password
                    if( this._eLoginCache.compareUsernamePassword(username, password) ) {
                        return 1;
                    }
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
     * @param username
     */
    public void egwSessionChange(int sessionChangeReason, String username) {
        logger.info("egwSessionChange, sessionChangeReason: " + Integer.toString(sessionChangeReason));

        Egroupware _egw = Egroupware.findInstance(username);
        
        try {
            switch( sessionChangeReason ) {
                case 5: // SessionLogon
                    if( (_egw != null) && _egw.isLogin() ) {
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
		return "14.2";
	}

	public int egwAuthenticatedUserGateway() {
		return 0;
	}
    
    /**
     * isEgwLogin
     * 
     * @param username
     * @return 
     */
    public int isEgwLogin(String username) {
        int lreturn = 0;
        
        Egroupware _egw = Egroupware.findInstance(username);
        
        if( (_egw != null) && _egw.isLogin() ) {
            lreturn = 1;
        }
        
        return lreturn;
    }
    
    /**
     * logoutEgw
     * 
     * @param username
     * @return
     * @throws Exception 
     */
    public int logoutEgw(String username) throws Exception {
        int lreturn = 0;
        Egroupware _egw = Egroupware.findInstance(username);
        
        if( (_egw != null) && _egw.isLogin() ) {
            try {
                _egw.logout();
                lreturn = 1;
            }
            catch( Exception e ) {
                
            }
        }
        
        return lreturn;
    }
    
    /**
     * setSetting
     * 
     * @param key
     * @param value 
     */
    public void setSetting(String key, String value) {
        if( this._settings.containsKey(key) ) {
            this._settings.remove(key);
        }
        
        this._settings.put(key, value);
    }
}
