/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

/**
 * EgroupwarePGina
 * @author Stefan Werfling
 */
public class EgroupwarePGina {
    
    /**
     * use Emulator DLL for Debuging
     */
    static protected Boolean _useEmulator = false;
    
    /**
     * setUseEmulator
     * @param use 
     */
    static public void setUseEmulator(Boolean use) {
        EgroupwarePGina._useEmulator = use;
    }
    
    /**
     * isRunAsService
     * is run as service
     * 
     * @return 
     */
    static public boolean isRunAsService() {
        if( EgroupwarePGina._useEmulator ) {
            return EgroupwareDLLEmulator.isRunAsService();
        }
        
        return EgroupwareDLL.isRunAsService();
    }
    
    /**
     * isJavaLoggingFile
     * @return 
     */
    static public boolean isJavaLoggingFile() {
        if( EgroupwarePGina._useEmulator ) {
            return EgroupwareDLLEmulator.isJavaLoggingFile();
        }
        
        return EgroupwareDLL.isJavaLoggingFile();
    }
    
    /**
     * getAppDir
     * get pGina App dir
     * 
     * @return 
     */
    static public String getAppDir() {
        if( EgroupwarePGina._useEmulator ) {
            return EgwWinLogonUltis.pathUriValid(
                EgroupwareDLLEmulator.getAppDir());
        }
        
        return EgwWinLogonUltis.pathUriValid(
            EgroupwareDLL.getAppDir());
    }
    
    /**
     * startProcessInSession
     * start a process in a session
     * 
     * @param sessionId
     * @param cmdLine
     * @return 
     */
    static public int startProcessInSession(int sessionId, String cmdLine) {
        if( EgroupwarePGina._useEmulator ) {
            // TODO
            return -1;
        }
        
        return EgroupwareDLL.startProcessInSession(sessionId, cmdLine);
    }
    
    /**
     * startUserProcessInSession
     * start a user process in a session
     * 
     * @param sessionId
     * @param cmdLine
     * @return 
     */
    static public int startUserProcessInSession(int sessionId, String cmdLine) {
        if( EgroupwarePGina._useEmulator ) {
            // TODO
            return -1;
        }
        
        return EgroupwareDLL.startUserProcessInSession(sessionId, cmdLine);
    }
    
    /**
     * startProcessInWinsta0Default
     * @param cmdLine
     * @return 
     */
    static public int startProcessInWinsta0Default(String cmdLine) {
        if( EgroupwarePGina._useEmulator ) {
            // TODO
            return -1;
        }
        
        return EgroupwareDLL.startProcessInWinsta0Default(cmdLine);
    }
    
    /**
     * startProcessInWinsta0Winlogon
     * @param cmdLine
     * @return 
     */
    static public int startProcessInWinsta0Winlogon(String cmdLine) {
        if( EgroupwarePGina._useEmulator ) {
            // TODO
            return -1;
        }
        
        return EgroupwareDLL.startProcessInWinsta0Winlogon(cmdLine);
    }
    
    /**
     * getDLLHash
     * 
     * @return 
     */
    static public String getDLLHash() {
        if( EgroupwarePGina._useEmulator ) {
            return EgroupwareDLLEmulator.getDLLHash();
        }
        
        return EgroupwareDLL.getDLLHash();
    }
    
    /**
     * getSysFingerprint
     * 
     * @return 
     */
    static public String getSysFingerprint() {
        if( EgroupwarePGina._useEmulator ) {
            return EgroupwareDLLEmulator.getSysFingerprint();
        }
        
        return EgroupwareDLL.getSysFingerprint();
    }
    
    /**
     * getSystemStr
     * 
     * @return 
     */
    static public String getSystemStr() {
        if( EgroupwarePGina._useEmulator ) {
            return EgroupwareDLLEmulator.getSystemStr();
        }
        
        return EgroupwareDLL.getSystemStr();
    }
    
    /**
     * getMachineName
     * 
     * @return 
     */
    static public String getMachineName() {
        if( EgroupwarePGina._useEmulator ) {
            return EgroupwareDLLEmulator.getMachineName();
        }
        
        return EgroupwareDLL.getMachineName();
    }
    
    /**
     * logInfo
     * log a message as info
     * 
     * @param msg 
     */
    static public void logInfo(String msg) {
        if( EgroupwarePGina._useEmulator ) {
            EgroupwareDLLEmulator.logInfo(msg);
        }
        else {
            EgroupwareDLL.logInfo(msg);
        }
    }
    
    /**
     * logError
     * log a message as error
     * 
     * @param msg 
     */
    static public void logError(String msg) {
        if( EgroupwarePGina._useEmulator ) {
            EgroupwareDLLEmulator.logError(msg);
        }
        else {
            EgroupwareDLL.logError(msg);
        }
    }
    
    /**
     * getUsername
     * 
     * @param sessionId
     * @return 
     */
    static public String getUsername(int sessionId) {
        if( EgroupwarePGina._useEmulator ) {
            return EgroupwareDLLEmulator.getUsername(sessionId);
        }
        
        return EgroupwareDLL.getUsername(sessionId);
    }
    
    /**
     * logoffSession
     * 
     * @param sessionId
     * @return 
     */
    static public boolean logoffSession(int sessionId) {
        if( EgroupwarePGina._useEmulator ) {
            return EgroupwareDLLEmulator.logoffSession(sessionId);
        }
        
        return EgroupwareDLL.logoffSession(sessionId);
    }
    
    /**
     * getCredentials
     * 
     * @param title
     * @param message
     * @return 
     */
    static public String getCredentials(String title, String message) {
        if( EgroupwarePGina._useEmulator ) {
            return "";
        }
        
        return EgroupwareDLL.getCredentials(title, message);
    }
}