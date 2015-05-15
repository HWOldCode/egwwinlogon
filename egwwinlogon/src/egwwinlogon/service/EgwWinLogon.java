package egwwinlogon.service;

import egwwinlogon.service.db.EgwWinLogonDb;
import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.jegroupware.egroupware.events.EgroupwareEventListener;
import egwwinlogon.egroupware.EgroupwareCommand;
import egwwinlogon.egroupware.EgroupwareELoginCache;
import egwwinlogon.egroupware.EgroupwareMachineInfo;
import egwwinlogon.egroupware.EgroupwareMachineLogging;
import egwwinlogon.egroupware.EgroupwareSettings;
import egwwinlogon.http.LogonHttpServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import org.apache.log4j.*;

/**
 * EgwWinLogon
 * @author Stefan Werfling
 */
public class EgwWinLogon {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgwWinLogon.class);

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

    public native void callStartApplication(String cmd);
    public native void callLogApp(String log);

    /**
     * initEgroupware
     *
     */
    public void initEgroupware() {
        if( true ) {
            try {
                SimpleLayout layout = new SimpleLayout();
                FileAppender fileAppender = new FileAppender(layout,
                    "C:/MeineLogDatei.log", 
                    false
                    );
                
                Logger tlogger = Logger.getRootLogger();
                tlogger.addAppender(fileAppender);
            }
            catch( Exception e ) {
                
            }
        }
        
        logger.info("initEgroupware, init egroupware objects");
        
        // ---------------------------------------------------------------------

        this._eLoginCache = EgroupwareELoginCache.loadByFile("elogin.cache");

        if( this._eLoginCache == null ) {
            this._eLoginCache = new EgroupwareELoginCache();
        }
        
        if( this._server == null ) {
            String tport = (String) this._settings.get("httpserverport");
            
            if( tport == null ) {
                this._server = new LogonHttpServer();
            }
            else {
                this._server = new LogonHttpServer(Integer.parseInt(tport));
            }

            try {
                this._server.init();
            } catch( IOException ex ) {
                java.util.logging.Logger.getLogger(
                    EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            }

            EgwWinLogonHttpHandlerConfig config = new EgwWinLogonHttpHandlerConfig(
                (String) this._settings.get("sysfingerprint"));
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
    public int egwAuthenticateUser(String username, String password, String domain, int sessionid) {
        return this.egwAuthenticateUser(username, password, domain, sessionid, null);
    }
    
    /**
     * egwAuthenticateUser
     *
     * @param username
     * @param password
     * @param egwListener
     * @return
     */
	public int egwAuthenticateUser(String username, String password, 
        String domain, int sessionid, EgroupwareEventListener egwListener) 
    {
        logger.info("egwAuthenticateUser, username: " + 
            username + " Domain: " + domain + 
            " Windows-SessionID: " + String.valueOf(sessionid));

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

        if( egwListener != null ) {
            _egw.addListener(egwListener);
        }
        
        try {
            _egw.login();

            if( _egw.isLogin() ) {
                try {
                    // ---------------------------------------------------------
                    logger.info("Send MachineInfo ...");
                    
                    // machine info send
                    EgroupwareMachineInfo mi = new EgroupwareMachineInfo(
                        (String) this._settings.get("sysfingerprint"));

                    mi.setMachineName((String) this._settings.get("machinename"));
                    _egw.request(mi);
                    // ---------------------------------------------------------
                    
                    logger.info("Set Machine Logging ...");
                    
                    EgroupwareMachineLogging egwlog = new EgroupwareMachineLogging(
                        (String) this._settings.get("sysfingerprint"),
                        config
                        );
                    
                    // set logger
                    Logger tlogger = Logger.getRootLogger();
                    tlogger.addAppender(egwlog);

                    // ---------------------------------------------------------
                    
                    // register machine logger to http logger
                    EgwWinLogonHttpHandlerLogger httpLogger = 
                        (EgwWinLogonHttpHandlerLogger) this._server.getHandler(
                            EgwWinLogonHttpHandlerLogger.class.getName());
                    
                    if( httpLogger != null ) {
                        httpLogger.setMachineLogger(egwlog);
                    }
                    
                    // ---------------------------------------------------------
                    
                    logger.info("Login by user: " + username + "@" + domain);
                    // ---------------------------------------------------------

                    /*EgroupwareCommand cmds = new EgroupwareCommand(
                        (String) this._settings.get("sysfingerprint"), 
                        EgroupwareCommand.EGW_CMD_TYPE_USER);

                    try {
                        _egw.request(cmds);
                        cmds.execute();
                    }
                    catch( Exception e ) {
                    }*/
                }
                catch( Exception ec ) {
                    // nothing
                    logger.error("EgroupwareMachineInfo: " + ec.getMessage() + 
                        " <> " + ec.getLocalizedMessage());
                }

                logger.info("Load new Cachelist by user: " + username);
                
                // request login cache list
                _egw.request(this._eLoginCache);

                if( this._eLoginCache.countAccounts() > 0 ) {
                    logger.info("Save new Cachelist by user: " + username);
                    //EgwWinLogonDbConnection _con = this._db.getNewConnection();

                    //_con.query("drop table barcodes if exists;");
                    //_con.query("create table barcodes (id integer, barcode varchar(20) not null);");
                    //_con.query("insert into barcodes (id, barcode) values (1, '12345566');");
                    EgroupwareELoginCache.saveToFile(this._eLoginCache, "elogin.cache");
                }
                
                logger.info("egwAuthenticateUser return true by user: " + username);
                return 1;
            }
        }
        catch( 
            java.net.SocketTimeoutException | 
            java.net.UnknownHostException | 
            java.net.NoRouteToHostException e ) 
        {
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            
            logger.info("egwAuthenticateUser, Exception: " + sw.toString());
            
            EgwWinLogon._error = "Error by Login with User: " + username + "\n" +
                "Exception: " + sw.toString();
        }

        logger.info("egwAuthenticateUser return false by user: " + username);
        
        return 0;
	}

    /**
     * egwSessionChange
     * 
     * @param sessionChangeReason
     * @param username
     * @param sessionid
     */
    public void egwSessionChange(String sessionChangeReasonStr, String username, int sessionid) {
        Integer sessionChangeReason = Integer.parseInt(sessionChangeReasonStr);
        
        logger.info("egwSessionChange, sessionChangeReason: " + 
            String.valueOf(sessionChangeReason) + 
            " Username: " + username + 
            " Sessionid: " + String.valueOf(sessionid)
            );

        Egroupware _egw = Egroupware.findInstance(username);
        
        try {
            switch( sessionChangeReason ) {
                case 5: // SessionLogon
                    logger.info("form process");
                    EgroupwareDLL.logInfo("process exec: ");
                    logger.info("nachem process");
                    Integer t = EgroupwareDLL.startProcessInSession(sessionid, "c:\\windows\\system32\\cmd.exe /c net use U: \"\\\\192.168.0.252\\video\" /user:megasave 1234");
                    EgroupwareDLL.logInfo("process id: ");
                        
                    if( (_egw != null) && _egw.isLogin() ) {
                        if( this._server != null ) {
                            EgroupwareSettings tmp = new EgroupwareSettings("");
                            tmp.setSettingsToSystem();
                            // test
                            /*CreateProcessAsUser.createProcess(
                                CreateProcessAsUser.CMD, "C:\\", username, 
                                _egw.getConfig().getPassword()
                                );*/
                            // ---
                        }
                        
                        
                        
                        
                        //this.createEgroupwareUserProcess();
                    }
                    else {
                        EgwWinLogon._error = "please login in egroupware";
                    }
                    break;

                case 6:
                    if( (_egw != null) && _egw.isLogin() ) {
                        this.logoutEgw(username);
                    }
                    else {
                        EgwWinLogon._error = "egroupware instance empty";
                    }
                    break;
            }
        }
        catch( Exception e ) {
            String msg = "egwSessionChange, Exception: " + e.getMessage();
            logger.info(msg);
            EgwWinLogon._error = msg;
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

    /**
     * egwStarting
     */
	public void egwStarting() {
        if( this._server != null ) {

        }
	}

    /**
     * egwStopping
     */
	public void egwStopping() {
        if( this._server != null ) {
            this._server.stop();
        }
	}

    /**
     * egwGetLogs
     * @return 
     */
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

    /**
     * egwAuthenticatedUserGateway
     * @return 
     */
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
                String msg = "Logout by user: " + username;
                
                Logger tlogger = Logger.getRootLogger();
                EgroupwareMachineLogging melog = 
                    (EgroupwareMachineLogging) tlogger.getAppender("EgroupwareMachineLogging");
                
                if( melog != null ) {
                    melog.log(msg, "logout", "INFO");
                }
                else {
                    logger.info(msg);
                }
                
                _egw.logout();
                lreturn = 1;
            }
            catch( Exception e ) {
                logger.error("Logout by user: " + username + ", " + e.getMessage());
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
