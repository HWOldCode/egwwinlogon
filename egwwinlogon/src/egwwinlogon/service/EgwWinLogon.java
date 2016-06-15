package egwwinlogon.service;

import egwwinlogon.service.db.EgwWinLogonDb;
import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.jegroupware.egroupware.EgroupwareHttp;
import com.jegroupware.egroupware.events.EgroupwareEventListener;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionLoginStatus;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionUserConfig;
import egwwinlogon.egroupware.EgroupwareCommand;
import egwwinlogon.egroupware.EgroupwareELoginCache;
import egwwinlogon.egroupware.EgroupwareMachineInfo;
import egwwinlogon.egroupware.EgroupwareMachineLogging;
import egwwinlogon.http.LogonHttpServer;
import egwwinlogon.log.ZipFileAppender;
import egwwinlogon.protocol.EgwWinLogonProtocol;
import egwwinlogon.service.crypt.EgwWinLogonCryptAes;
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
	 * Static
	 */
	static {
		EgroupwareHttp.addUserAgent(
			EgwWinLogonConst.EGW_WIN_LOGON_TITLE + ";" + 
			EgwWinLogonConst.EGW_WIN_LOGON_DESCRIPTION + ";" + 
			EgwWinLogonConst.EGW_WIN_LOGON_VERSION
			);
	}
	
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgwWinLogon.class);

    /**
     * _settings
     */
    static protected LinkedHashMap _settings = new LinkedHashMap();
    
    /**
     * Global Error
     */
    static protected  String _error = "";

    /**
     * LogonHttpServer
     */
    protected LogonHttpServer _server = null;
    
    /**
     * Egroupware Configs
     */
    protected LinkedHashMap _egwConfigs = new LinkedHashMap();
    
    /**
     * EgwWinLogonDb
     */
    protected EgwWinLogonDb _db = null;
	
    /**
     * initEgroupware
     *
     */
    public void initEgroupware() {
		// add machinename to user agent
		EgroupwareHttp.addUserAgent(
			"MN:" + EgroupwarePGina.getMachineName());
		
        // init JCE
        EgwWinLogonCryptAes.initJCE();
        
        // ---------------------------------------------------------------------
        
        if( EgroupwarePGina.isJavaLoggingFile() ) {
            try {
                SimpleLayout layout = new SimpleLayout();
                ZipFileAppender fileAppender = new ZipFileAppender(layout,
                    EgroupwarePGina.getAppDir() + "log/" + EgwWinLogonConst.LOG_FILE, 
                    EgwWinLogonUltis.getPHSF(this)
                    );
                
                Logger tlogger = Logger.getRootLogger();
                tlogger.addAppender(fileAppender);
            }
            catch( Exception e ) {
                EgroupwarePGina.logError(
                    "initEgroupware-fileAppender: " + e.getMessage());
            }
        }
        
        logger.info("initEgroupware, init egroupware objects");
        
        // ---------------------------------------------------------------------

        try {
            EgroupwareELoginCache.instance = (EgroupwareELoginCache) EgroupwareELoginCache.loadByFile(
                EgroupwarePGina.getAppDirCache() + EgwWinLogonConst.CACHE_FILE_ACCOUNTS);
        }
        catch( Exception ex ) {
            EgroupwarePGina.logError(
                "initEgroupware-loadcache: " + ex.getMessage());
        }

        if( EgroupwareELoginCache.instance == null ) {
            EgroupwareELoginCache.instance = new EgroupwareELoginCache();
        }
        
        // ---------------------------------------------------------------------
        
        try {
            EgroupwareCommand.instance = (EgroupwareCommand) EgroupwareCommand.loadByFile(
                EgroupwarePGina.getAppDirCache() + EgwWinLogonConst.CACHE_FILE_COMMANDS);
        }
        catch( Exception ex ) {
            EgroupwarePGina.logError(
                "initEgroupware-loadcommand: " + ex.getMessage());
        }
        
        if( EgroupwareCommand.instance == null ) {
            EgroupwareCommand.instance = new EgroupwareCommand();
        }
        
        // ---------------------------------------------------------------------
        
        if( this._server == null ) {
            String tport = (String) EgwWinLogon._settings.get("httpserverport");
            
            if( tport == null ) {
                this._server = new LogonHttpServer();
            }
            else {
                this._server = new LogonHttpServer(Integer.parseInt(tport));
            }

            try {
                this._server.init();
            } 
			catch( IOException ex ) {
                java.util.logging.Logger.getLogger(
                    EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            }

            EgwWinLogonHttpHandlerConfig config = new EgwWinLogonHttpHandlerConfig(
                (String) EgwWinLogon._settings.get("sysfingerprint"));
            config.register(this._server);

            EgwWinLogonHttpHandlerLogger httplogger = new EgwWinLogonHttpHandlerLogger();
            httplogger.register(this._server);

            EgwWinLogonHttpHandlerSession session = new EgwWinLogonHttpHandlerSession();
            session.register(this._server);
            //logger.get

            EgwWinLogonHttpHandlerFirstInstall finstall = new EgwWinLogonHttpHandlerFirstInstall();
            finstall.register(this._server);
            
			EgwWinLogonHttpHandlerCommand httpcmd = new EgwWinLogonHttpHandlerCommand();
			httpcmd.register(this._server);
			
			EgwWinLogonHttpHandlerEWorkflow httpwork = new EgwWinLogonHttpHandlerEWorkflow();
			httpwork.register(this._server);
			
            try {
                this._server.start();
            } catch( IOException ex ) {
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

		EgwWinLogonThread _wlt = null;
        EgroupwareConfig config = null;

		// ---------------------------------------------------------------------
		
        if( this._egwConfigs.containsKey(username) ) {
            config = (EgroupwareConfig) _egwConfigs.get(username);

            this._egwConfigs.remove(username);
        }
        else {
            config = new EgroupwareConfig();
        }

        config.setUrl((String) EgwWinLogon._settings.get("url"));
        config.setDomain((String) EgwWinLogon._settings.get("domain"));
        config.setUser(username);
        config.setPassword(password);
        config.setSocketTimeout(3000);
        
		// disable
		config.setUseEgwThread(false);
		
        this._egwConfigs.put(username, config);

        // ---------------------------------------------------------------------

        Egroupware _egw = Egroupware.getInstance(config);

        if( egwListener != null ) {
            _egw.addListener(egwListener);
        }
		
		// ---------------------------------------------------------------------
        
        try {
            try {
				_wlt  = EgwWinLogonThread.getInstance(username);
				
				if( _wlt == null ) {
					try {
						_egw.login();
					}
					catch( EGroupwareExceptionUserConfig e ) {
						EgwWinLogon._error = "Please check your username or password!";

						return 0;
					}
					catch( EGroupwareExceptionLoginStatus e ) {
						EgwWinLogon._error = "EGroupware response status: " + 
							EGroupwareExceptionLoginStatus.getStatusMessage(e.getStatus());

						return 0;
					}
					catch( Exception te ) {
						throw new EgwWinLogonException(
							EgwWinLogonException.EC_SERVER_CONNECTION);
					}
				}
				else {
					// check user
					if( EgroupwareELoginCache.instance.isStatusA(username) && 
						EgroupwareELoginCache.instance.isAccountExpires(username) ) 
					{
                        // check password
                        if( EgroupwareELoginCache.instance.compareUsernamePassword(username, password) ) {
							_egw = _wlt.getEgroupware();	// current egw instance use
						}
					}
				}

				// is login
                if( _egw.isLogin() ) {
                    // only by first login
                    if( _wlt == null ) {
                        try {
                            // ---------------------------------------------------------
                            logger.info("Send MachineInfo ...");

                            // machine info send
                            EgroupwareMachineInfo mi = new EgroupwareMachineInfo(
                                (String) EgwWinLogon._settings.get("sysfingerprint"));

                            mi.setMachineName((String) EgwWinLogon._settings.get("machinename"));
                            _egw.request(mi);
                            // ---------------------------------------------------------

                            logger.info("Set Machine Logging ...");

                            EgroupwareMachineLogging egwlog = new EgroupwareMachineLogging(config);

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
                        }
                        catch( Exception ec ) {
                            // nothing
                            logger.error("EgroupwareMachineInfo: " + ec.getMessage() + 
                                " <> " + ec.getLocalizedMessage());
                        }

                        logger.info("Load new Cachelist by user: " + username);

                        // request login cache list
                        _egw.request(EgroupwareELoginCache.instance);

                        if( EgroupwareELoginCache.instance.countAccounts() > 0 ) {
                            logger.info("Save new Cachelist by user: " + username);

                            EgroupwareELoginCache.saveToFile(
                                EgroupwareELoginCache.instance, 
                                EgroupwarePGina.getAppDirCache() + EgwWinLogonConst.CACHE_FILE_ACCOUNTS);
                        }

                        // request command cache list
                        _egw.request(EgroupwareCommand.instance);

                        if( EgroupwareCommand.instance.getCmdCount() > 0 ) {
                            logger.info("Save new Commandlist by user: " + username);

                            EgroupwareCommand.saveToFile(
                                EgroupwareCommand.instance, 
                                EgroupwarePGina.getAppDirCache() + EgwWinLogonConst.CACHE_FILE_COMMANDS);
                        }
                    }

                    try {
                        if( _wlt == null ) {
                            
                            // no instance found, first login
                            // execute event login pre
                            EgroupwareCommand.instance.executeEvent(
                                0, 
                                EgroupwareCommand.TYPE_SERVICE, 
                                EgroupwareCommand.EVENT_LOGIN_PRE
                                );
                            
                            // create thread
                            _wlt = new EgwWinLogonThread(_egw);
                        }

                        // ---------------------------------------------------------
						
						if( EgwWinLogonUltis.checkWindowsProfile(username) ) {
							logger.info("Profile is fixed.");
						}
                    }
                    catch( Exception ec ) {
                        // nothing
                        logger.error("EgroupwareMachineInfo Create Thread: " + ec.getMessage() + 
                            " <> " + ec.getLocalizedMessage());
						
						throw ec;
                    }

                    logger.info("egwAuthenticateUser return true by user: " + username);
                    return 1;
                }
                else {
                    // login false
                    throw new EgwWinLogonException(
                        EgwWinLogonException.EC_LOGIN_FALSE);
                }
            }
            catch( 
                java.net.SocketTimeoutException | 
                java.net.UnknownHostException | 
                java.net.NoRouteToHostException |
                EgwWinLogonException e )
            {
                if( e instanceof EgwWinLogonException ) {
                    if( ((EgwWinLogonException)e).getErrorCode() != 
                        EgwWinLogonException.EC_SERVER_CONNECTION ) {
                        throw e; // move up
                    }
                }

                // login by offline mode, username + password check by cachelist
                if( EgroupwareELoginCache.instance.countAccounts() > 0 ) {
                    // is activ and expries
                    if( EgroupwareELoginCache.instance.isStatusA(username) && EgroupwareELoginCache.instance.isAccountExpires(username) ) {
                        // check password
                        if( EgroupwareELoginCache.instance.compareUsernamePassword(username, password) ) {

                            // final init wlt
							_wlt = EgwWinLogonThread.getInstance(username);
							
                            if( _wlt == null ) {
                                _wlt = new EgwWinLogonThread(_egw);
                            }

                            EgroupwareCommand.instance.executeEvent(
                                0, 
                                EgroupwareCommand.TYPE_SERVICE, 
                                EgroupwareCommand.EVENT_LOGIN_PRE
                                );

                            return 1;
                        }
                    }
                }
            }
            catch( Exception e ) {
                throw e;    // move up
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
     * egwAuthenticatedUserGateway
     * 
     * @param username
     * @param password
     * @param domain
     * @return 
     */
    public boolean egwAuthenticatedUserGateway(String username, String password, String domain) {
        // moment ever true
        if( true ) {
            return true;
        }
        
        return false;
    }
    
    /**
     * egwAuthorizeUser
     * 
     * @param username
     * @param password
     * @param domain
     * @return 
     */
    public boolean egwAuthorizeUser(String username, String password, String domain) {
        // moment ever true
        if( true ) {
            return true;
        }
        
        return false;
    }
    
    /**
     * egwSessionChange
     * 
	 * @param sessionChangeReasonStr
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

        // main objects
        Egroupware _egw         = Egroupware.findInstance(username);
        EgwWinLogonThread _wlt  = EgwWinLogonThread.getInstance(sessionid);
        
        if( _wlt == null ) {
            _wlt = EgwWinLogonThread.getInstance(username);
            
            if( _wlt != null ) {
                _wlt.setSessionId(sessionid);
            }
        }
        
        try {
            // set session status
            if( _wlt != null ) {
                _wlt.setSessionStatus(sessionChangeReason);
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
        // setup 
        EgwWinLogonProtocol.setup();
        // ---------------------------------
        
        if( this._server != null ) {
			/*this._winfs = new EgwWinFS();
			this._winfs.setVolume(new EgwWinFSVolumeMultiResource());
			
			try {
				this._winfs.start();
			}
			catch( Exception ex ) {
				logger.error("egwStarting: Error load winfs, " + ex.getMessage());
			}
			*/
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
		return EgwWinLogonConst.EGW_WIN_LOGON_DESCRIPTION;
	}

	/**
	 * egwGetName
	 * @return String
	 */
	public String egwGetName() {
		return EgwWinLogonConst.EGW_WIN_LOGON_TITLE;
	}

	/**
	 * egwGetVersion
	 * @return String
	 */
	public String egwGetVersion() {
		return EgwWinLogonConst.EGW_WIN_LOGON_VERSION;
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
        if( EgwWinLogon._settings.containsKey(key) ) {
            EgwWinLogon._settings.remove(key);
        }

        EgwWinLogon._settings.put(key, value);
    }
    
    /**
     * getSetting
     * @param key
     * @return 
     */
    static public String getSetting(String key) {
        if( EgwWinLogon._settings.containsKey(key) ) {
            return (String) EgwWinLogon._settings.get(key);
        }
        
        return "";
    }
}