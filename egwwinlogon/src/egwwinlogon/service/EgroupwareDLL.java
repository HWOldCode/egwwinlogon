/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

/**
 * EgroupwareDLL
 * jni4net nativ register methods by my pgina plugin dll
 * 
 * @author Stefan Werfling
 */
public class EgroupwareDLL {
    
    /**
     * isRunAsService
     * is egroupware.dll run as service
     * 
     * @return 
     */
    static public native boolean isRunAsService();
    
    /**
     * getAppDir
     * get pGina App dir of Egroupware.dll
     * 
     * @return 
     */
    static public native String getAppDir();
    
    /**
     * startProcessInSession
     * start a process in a session
     * 
     * @param sessionId
     * @param cmdLine
     * @return 
     */
    static public native int startProcessInSession(int sessionId, String cmdLine);
    
    /**
     * logInfo
     * log a message as info
     * 
     * @param msg 
     */
    static public native void logInfo(String msg);
    
    /**
     * logError
     * log a message as error
     * 
     * @param msg 
     */
    static public native void logError(String msg);
    
    /**
     * validateCredentials
     * 
     * @param username
     * @param domain
     * @param password
     * @return 
     */
    static public native boolean validateCredentials(String username, String domain, String password);
    
    /**
     * setSetting
     * set setting in egroupware.dll
     * 
     * @param name
     * @param value 
     */
    static public native void setSetting(String name, String value);
}