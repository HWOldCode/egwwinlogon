package egwwinlogon.service;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.events.EgroupwareAuthentifiactionEvent;
import com.jegroupware.egroupware.events.EgroupwareEvent;
import com.jegroupware.egroupware.events.EgroupwareEventListener;
import com.jegroupware.egroupware.events.EgroupwareEventRequest;
import com.jegroupware.egroupware.events.EgroupwareLogoutEvent;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.win32.W32APIOptions;
import egwwinlogon.egroupware.EgroupwareCommand;
import egwwinlogon.winapi.ProcessUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

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
    protected Egroupware _egw   = null;
    
    /**
     * session id
     */
    protected int _sessionId    = -1;
    
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
     * getJavaInstallationPath
     * @return 
     */
    static public String getJavaInstallationPath() {
        String javaHome = "";
        
        try {
            String javaEnv = "SOFTWARE\\JavaSoft\\Java Runtime Environment";

            String currentVersion = Advapi32Util.registryGetStringValue(
                    WinReg.HKEY_LOCAL_MACHINE, 
                    javaEnv,
                    "CurrentVersion");

            String javaCV = javaEnv + "\\" + currentVersion;

            javaHome = Advapi32Util.registryGetStringValue(
                    WinReg.HKEY_LOCAL_MACHINE, 
                    javaCV,
                    "JavaHome");

            logger.info("JavaHome: " + javaHome);
        }
        catch( Exception e) {
            //logger.log(Priority.ERROR, null, e);
            logger.error("Error getJavaInstallationPath: " + e.getMessage());
        }
        
        return javaHome;
    }
    
    /**
     * getUserAppCmd
     * @param params
     * @return 
     */
    static public String getUserAppCmd(String params) {
        String appDir = "";
        
        try {
            appDir = EgroupwareDLL.getAppDir();
        }
        catch( Exception ex ) {
            logger.error("Error getUserAppCmd: " + ex.getMessage());
        }
        
        String appCmd = "\"" + EgwWinLogonThread.getJavaInstallationPath() + 
            "\\bin\\javaw.exe\" -jar \"" + appDir + 
            "egwwinlogon.jar\" " + params;
        
        return appCmd;
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
        if( this._sessionId != -1 ) {
            if( this._userappProcessId == -1 ) {
                logger.info("Start Userapp...");
                
                String username = EgroupwareDLL.getUsername(this._sessionId);
                logger.info("Userapp for username: " + username);
                
                String cmdApp = EgwWinLogonThread.getUserAppCmd(username);
                
                logger.info("Userapp cmd: " + cmdApp);
                
                this._userappProcessId = EgroupwareDLL.startUserProcessInSession(
                    this._sessionId, cmdApp);
                
                logger.info("Userapp processid: " + 
                    String.valueOf(this._userappProcessId));
            }
            else {
                /*try {
                    if( !ProcessUtils.existProcessById(this._userappProcessId) ) {

                        String username = EgroupwareDLL.getUsername(this._sessionId);
                        logger.info("Userapp for username: " + username);

                        String cmdApp = EgwWinLogonThread.getUserAppCmd(username);

                        logger.info("Userapp cmd: " + cmdApp);

                        this._userappProcessId = EgroupwareDLL.startUserProcessInSession(
                            this._sessionId, cmdApp);

                        logger.info("Userapp processid: " + 
                            String.valueOf(this._userappProcessId));
                    }
                }
                catch( Exception ex ) {
                    logger.error(ex.getMessage());
                }
                */
            }
            
            // check can later login
            // TODO
        }
    }
    
    /**
     * _runSessionLogon
     */
    protected void _runSessionLogon() {
        
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
        
    }

    /**
     * _changeSessionLogon
     */
    private void _changeSessionLogon() {
        // ---------------------------------------------------------------------
        // call cmd by server
        if( this._egw.isLogin() ) {
            EgroupwareCommand egwcmd = new EgroupwareCommand(
                EgwWinLogon.getSetting("sysfingerprint"), EgroupwareCommand.EGW_CMD_TYPE_SERVICE);
            
            try {
                this._egw.request(egwcmd);
                egwcmd.execute(this._sessionId);
            }
            catch( Exception ex ) {
                logger.error(ex.getMessage());
            }
        }
    }

    /**
     * _changeSessionLogoff
     */
    private void _changeSessionLogoff() {
        
    }

    /**
     * _changeSessionLock
     */
    private void _changeSessionLock() {
        
    }

    /**
     * _changeSessionUnlock
     */
    private void _changeSessionUnlock() {
        
    }

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
        EgroupwareDLL.logoffSession(this._sessionId);
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