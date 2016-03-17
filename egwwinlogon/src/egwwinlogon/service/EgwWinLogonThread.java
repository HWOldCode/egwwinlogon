package egwwinlogon.service;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.events.EgroupwareAuthentifiactionEvent;
import com.jegroupware.egroupware.events.EgroupwareEvent;
import com.jegroupware.egroupware.events.EgroupwareEventListener;
import com.jegroupware.egroupware.events.EgroupwareEventRequest;
import com.jegroupware.egroupware.events.EgroupwareLogoutEvent;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import egwwinlogon.dokan.volume.multiresource.EgwWinFSMultiResourceSMBMount;
import egwwinlogon.dokan.volume.multiresource.EgwWinFSMultiResourceWebDavMount;
import egwwinlogon.egroupware.EgroupwareCommand;
import egwwinlogon.egroupware.EgroupwareNetShares;
import egwwinlogon.winapi.ProcessList;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * EgwWinLogonThread
 * 
 * @author Stefan Werfling
 */
public class EgwWinLogonThread implements Runnable, EgroupwareEventListener {

    /**
     * list of instance of EgwWinLogonThread
     */
    static protected List<EgwWinLogonThread> _instances = new ArrayList<>();
    
    /**
     * getInstance
     *
     * @param username
     * @return Egroupware
     */
    static public EgwWinLogonThread getInstance(String username) {
        for( EgwWinLogonThread _tinstance : EgwWinLogonThread._instances ) {
            Egroupware egw = _tinstance.getEgroupware();
            
            if( egw != null ) {
                if( egw.getConfig().getUser().equals(username) ) {
                    return _tinstance;
                }
            }
        }
        
        return null;
    }
    
    /**
     * getInstance
     * @param sessionId
     * @return 
     */
    static public EgwWinLogonThread getInstance(int sessionId) {
        for( EgwWinLogonThread _tinstance : EgwWinLogonThread._instances ) {
            if( _tinstance._sessionId == sessionId ) {
                return _tinstance;
            }
        }
        
        return null;
    }
    
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgwWinLogonThread.class);
    
    /**
     * Thread of EgwWinLogonThread
     */
    protected Thread _cthread = null;
    
    /**
     * egw instance
     */
    protected Egroupware _egw = null;
	
    /**
     * session id
     */
    protected int _sessionId = -1;
    
    /**
     * session status
     */
    protected int _sessionStatus = -1;
    
    /**
     * runnable
     */
    protected boolean _runnable = false;
    
    /**
     * userapp process id
     */
    protected int _userappProcessId = -1;
    
    /**
     * EgwWinLogonThread
     * @param egw 
	 * @param volume 
     */
    public EgwWinLogonThread(Egroupware egw) {
        this._egw = egw;
        this._egw.addListener(this);
        
        this._runnable = true;
        
        this._cthread = new Thread(this);
        this._cthread.setDaemon(true);
        this._cthread.setPriority(Thread.MIN_PRIORITY);
        this._cthread.start();
        
        // add self
        EgwWinLogonThread._instances.add(this);
    }
    
    /**
     * getEgroupware
     * @return 
     */
    public Egroupware getEgroupware() {
        return this._egw;
    }
    
    /**
     * setSession
     * @param sessionid 
     */
    public void setSessionId(int sessionid) {
        this._sessionId = sessionid;
    }
    
    /**
     * getSessionId
     * @return 
     */
    public int getSessionId() {
        return this._sessionId;
    }
    
    /**
     * setSessionStatus
     * @param status 
     */
    public void setSessionStatus(int status) {
        this._sessionStatus = status;
        
        logger.info("Session Status cahnge: " + String.valueOf(status));
        
        switch( this._sessionStatus ) {
            case 5:
                this._changeSessionLogon();
                break;

            case 6:
                this._changeSessionLogoff();
                break;

            case 7:
                this._changeSessionLock();
                break;

            case 8:
                this._changeSessionUnlock();
                break;
        }
    }
    
    /**
     * run
     */
    @Override
    public void run() {
        logger.info("Thread run");
        
        while( this._runnable ) {
            try {
                Thread.sleep(100);
                
                this._run();
                
                if( this._sessionId == -1 ) {
                    continue;
                }
                
                switch( this._sessionStatus ) {
                    case 5:
                        this._runSessionLogon();
                        break;
                        
                    case 6:
                        this._runSessionLogoff();
                        break;
                        
                    case 7:
                        this._runSessionLock();
                        break;
                        
                    case 8:
                        this._runSessionUnlock();
                        break;
                }
            }
            catch( Exception ex ) {
                // TODO
                logger.error(ex.getMessage());
            }
        }
        
        logger.info("Thread close");
    }
    
    /**
     * _run 
     * ever
     */
    protected void _run() {
        // ---------------------------------------------------------------------
        // offline, check have connection
        
		// none
        
        // ---------------------------------------------------------------------
        // userapp
        if( this._sessionId != -1 ) {
            if( this._userappProcessId == -1 ) {
                logger.info("Start Updaterapp...");
                
                String cmdUApp = EgwWinLogonUltis.getUpdaterAppCmd();
                
                EgroupwarePGina.startProcessInSession(this._sessionId, cmdUApp);
                
                // -------------------------------------------------------------
                
                logger.info("Firststart Userapp...");
                
                String username = EgroupwarePGina.getUsername(this._sessionId);
                logger.info("Userapp for username: " + username);
                
                String cmdApp = EgwWinLogonUltis.getUserAppCmd(username);
                
                logger.info("Userapp cmd: " + cmdApp);
                
				try{
					Thread.sleep(20000);
				}
				catch( Exception ex ) {
				}
				
                this._userappProcessId = EgroupwarePGina.startUserProcessInSession(
                    this._sessionId, cmdApp);
                
                logger.info("userapp processid: " + 
                    String.valueOf(this._userappProcessId));
            }
            else {
                try {
                    if( !ProcessList.existProcessById(this._userappProcessId) ) {
                        String username = EgroupwarePGina.getUsername(this._sessionId);
                        logger.info("Userapp for username: " + username);

                        String cmdApp = EgwWinLogonUltis.getUserAppCmd(username);

                        logger.info("Userapp cmd: " + cmdApp);

                        this._userappProcessId = EgroupwarePGina.startUserProcessInSession(
                            this._sessionId, cmdApp);

                        logger.info("Userapp processid: " + 
                            String.valueOf(this._userappProcessId));
                    }
                }
                catch( Exception ex ) {
                    logger.error(ex.getMessage());
                }
            }
            
            // check can later login
            // TODO
        }
    }
    
    /**
     * _runSessionLogon
     */
    protected void _runSessionLogon() {
        
        this._reInitEgroupware();
        
        // ---------------------------------------------------------------------
        // is egroupware logout
        if( !this._egw.isLogin() ) {
            
            return;
        }
    }
    
    /**
     * _runSessionLogoff
     */
    protected void _runSessionLogoff() {
        
        // end thread (new login for create by login check)
        this._runnable = false;
        EgwWinLogonThread._instances.remove(this);
    }
    
    /**
     * _runSessionLock
     */
    protected void _runSessionLock() {
        
    }
    
    /**
     * _runSessionUnlock
     */
    protected void _runSessionUnlock() {
        this._reInitEgroupware();
    }

    /**
     * _reInitEgroupware
     */
    protected void _reInitEgroupware() {
        if( !this._egw.isLogin() ) {
            if( EgwWinLogonUltis.pingUrl(this._egw.getConfig().getUrl()) ) {
                try {
                    this._egw.login();
                }
                catch( Exception e ) {
                    logger.error(e.getMessage());
                }
            }
        }
    }
    
    /**
     * _changeSessionLogon
     */
    private void _changeSessionLogon() {
		// ---------------------------------------------------------------------
		
		//this._setVolumeMounts();
		
        // ---------------------------------------------------------------------
		
        if( this._sessionId != -1 ) {
			try {
				try {
					this._egw.request(EgroupwareNetShares.getInstance());

					EgroupwareNetShares.saveToFile(
						EgroupwareNetShares.getInstance(), 
						EgroupwarePGina.getAppDirCache() + "ns.cache"
						);
				}
				catch( Exception e ) {
					// offline
					logger.error(e.getMessage());
				}
				
				// all mount
				EgroupwareNetShares.getInstance().mountAllBySession(this._sessionId);
			}
			catch( Exception e ) {
				logger.error(e.getMessage());
			}
			
			try {
				EgroupwareCommand.instance.executeEvent(
					this._sessionId, 
					EgroupwareCommand.TYPE_BYCMD, 
					EgroupwareCommand.EVENT_LOGIN_AFTER
					);
			}
			catch( Exception e ) {
				logger.error(e.getMessage());
			}
        }
        
		// ---------------------------------------------------------------------
		
        // call cmd by server
        if( this._egw.isLogin() ) {
            /*EgroupwareCommand egwcmd = new EgroupwareCommand(
                EgwWinLogon.getSetting("sysfingerprint"), 
                EgroupwareCommand.EGW_CMD_TYPE_SERVICE);
            
            try {
                this._egw.request(egwcmd);
                egwcmd.execute(this._sessionId);
            }
            catch( Exception ex ) {
                logger.error(ex.getMessage());
            }*/
        }
    }

    /**
     * _changeSessionLogoff
     */
    private void _changeSessionLogoff() {
		// ---------------------------------------------------------------------
		
		//this._removeVolumeMounts();
		try {
			EgroupwareNetShares.getInstance().unmountAllBySession(this._sessionId);
		}
		catch( Exception e ) {
			logger.error(e.getMessage());
		}
		
        // ---------------------------------------------------------------------
		
		try {
			EgroupwareCommand.instance.executeEvent(
				this._sessionId, 
				EgroupwareCommand.TYPE_SERVICE, 
				EgroupwareCommand.EVENT_LOGOFF
				);
		}
		catch( Exception e ) {
			logger.error(e.getMessage());
		}		
    }

    /**
     * _changeSessionLock
     */
    private void _changeSessionLock() {
        if( this._sessionId != -1 ) {
			try {
				EgroupwareCommand.instance.executeEvent(
					this._sessionId, 
					EgroupwareCommand.TYPE_BYCMD, 
					EgroupwareCommand.EVENT_LOCK
					);
			}
			catch( Exception e ) {
				logger.error(e.getMessage());
			}
        }
    }

    /**
     * _changeSessionUnlock
     */
    private void _changeSessionUnlock() {
        if( this._sessionId != -1 ) {
			try {
				EgroupwareCommand.instance.executeEvent(
					this._sessionId, 
					EgroupwareCommand.TYPE_BYCMD, 
					EgroupwareCommand.EVENT_UNLOCK
					);
			}
			catch( Exception e ) {
				logger.error(e.getMessage());
			}
        }
    }

	/**
	 * _setVolumeMounts
	 */
	/*protected void _setVolumeMounts() {
		logger.info("_setVolumeMounts start");
		
		if( this._volume != null ) {
			logger.info("start volume: " + this._volume.getLabel());
			
			String username = EgroupwarePGina.getUsername(this._sessionId);
			
			logger.info("Username convert to sid: " + username + " sessionid: " + String.valueOf(this._sessionId));
			
			Account _account = Advapi32Util.getAccountByName(username);
			
			if( _account != null ) {
				//String sid = Advapi32Util.convertSidToStringSid(new WinNT.PSID(_account.sid));
				String sid = _account.sidString;
				
				logger.info("Check and Add Sid Owner: " + sid);
				
				if( !"".equals(sid) ) {
					this._volume.addOwnerSid(sid);
					
					logger.info("Add Mounts");
					
					//TODO Mounts
					this._volume.addMount(sid, new EgwWinFSMultiResourceSMBMount("public", "192.168.11.4", "admin", "1234"));
					this._volume.addMount(sid, new EgwWinFSMultiResourceSMBMount("win.logon", "192.168.11.4", "admin", "1234"));
					this._volume.addMount(sid, new EgwWinFSMultiResourceWebDavMount("home", "", "", ""));
				}
				else {
					logger.error("Sid by Username is empty: " + username);
				}
			}
			else {
				logger.error("Account by Username is empty: " + username);
			}
		}
		else {
			logger.error("Volume for mount not found!");
		}
	}*/
	
	/**
	 * _removeVolumeMounts
	 */
	/*protected void _removeVolumeMounts() {
		
	}*/
	
    /**
     * authentificationSucceeded
     * @param e 
     */
    @Override
    public void authentificationSucceeded(EgroupwareAuthentifiactionEvent e) {
        
    }

    /**
     * authentificationFailed
     * @param e 
     */
    @Override
    public void authentificationFailed(EgroupwareAuthentifiactionEvent e) {
        
    }

    /**
     * logoutSucceeded
     * @param e 
     */
    @Override
    public void logoutSucceeded(EgroupwareLogoutEvent e) {
        EgroupwarePGina.logoffSession(this._sessionId);
    }

    /**
     * logoutFailed
     * @param e 
     */
    @Override
    public void logoutFailed(EgroupwareLogoutEvent e) {
        
    }

    /**
     * requestSucceeded
     * @param e 
     */
    @Override
    public void requestSucceeded(EgroupwareEventRequest e) {
        
    }

    /**
     * requestFailed
     * @param e 
     */
    @Override
    public void requestFailed(EgroupwareEventRequest e) {
        
    }

    /**
     * threadAction
     * @param e 
     */
    @Override
    public void threadAction(EgroupwareEvent e) {
        
    }
}