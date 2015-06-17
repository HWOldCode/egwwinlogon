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
     * isJavaLoggingFile
     * @return 
     */
    static public native boolean isJavaLoggingFile();
    
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
     * startUserProcessInSession
     * start a user process in a session
     * 
     * @param sessionId
     * @param cmdLine
     * @return 
     */
    static public native int startUserProcessInSession(int sessionId, String cmdLine);
    
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
     * getCredentials
     * 
     * @param title
     * @param message
     * @return 
     */
    static public native String getCredentials(String title, String message);
    
    /**
     * setSetting
     * set setting in egroupware.dll
     * 
     * @param name
     * @param value 
     */
    static public native void setSetting(String name, String value);
    
    /**
     * logoffSession
     * 
     * @param sessionId
     * @return 
     */
    static public native boolean logoffSession(int sessionId);
    
    /**
     * getUsername
     * 
     * @param sessionId
     * @return 
     */
    static public native String getUsername(int sessionId);
    
    /**
     * getDLLHash
     * 
     * @return 
     */
    static public native String getDLLHash();
    
    /**
     * getSysFingerprint
     * 
     * @return 
     */
    static public native String getSysFingerprint();
    
    /**
     * getMachineName
     * 
     * @return 
     */
    static public native String getMachineName();
    
    /**
     * setDeviceEnabled
     * 
     * @param deviceGuid
     * @param instancePath
     * @param enable 
     */
    static public native void setDeviceEnabled(String deviceGuid, String instancePath, boolean enable);
}