/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EgroupwareDLLEmulator
 * for Debuging 
 * @author Stefan Werfling
 */
public class EgroupwareDLLEmulator {
    
    /**
     * is run as service
     */
    static protected Boolean _isRunAsService = false;
    
    /**
     * app dir of pgina
     */
    static protected String _appDir = "";
    
    /**
     * dll hash
     */
    static protected String _dllHash = "DEBUG-DLL";
    
    /**
     * system fingerprint
     */
    static protected String _sysFingerprint = "DEBUG-V3";
    
    /**
     * machine name
     */
    static protected String _machineName = "Debug-PC-HW";
    
    /**
     * system string
     */
    static protected String _systemStr = "CPU:124;BIOS:234;BASE:345;MAC:4353;";
    
    /**
     * username by session
     */
    static protected String _username = "stefan.werfling";
    
	/** 
     * _settings 
     */ 
    static protected LinkedHashMap _settings = new LinkedHashMap();
	
    /**
     * isRunAsService
     * is egroupware.dll run as service
     * 
     * @return 
     */
    static public boolean isRunAsService() {
        return EgroupwareDLLEmulator._isRunAsService;
    }
    
    /**
     * isJavaLoggingFile
     * @return 
     */
    static public boolean isJavaLoggingFile() {
        return true;
    }
    
    /**
     * getAppDir
     * get pGina App dir of Egroupware.dll
     * @return 
     */
    static public String getAppDir() {
        if( !"".equals(EgroupwareDLLEmulator._appDir) ) {
            return EgroupwareDLLEmulator._appDir;
        }
        
        try {
            return EgwWinLogonUltis.getCurrentJarPath();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(
                EgroupwareDLLEmulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "C:/";
    }
    
    /**
     * getDLLHash
     * @return 
     */
    static public String getDLLHash() {
        return EgroupwareDLLEmulator._dllHash;
    }
    
    /**
     * getSysFingerprint
     * @return 
     */
    static public String getSysFingerprint() {
        return EgroupwareDLLEmulator._sysFingerprint;
    }
   
    /**
     * getSystemStr
     * @return 
     */
    static public String getSystemStr() {
        return EgroupwareDLLEmulator._systemStr;
    }
    
    /**
     * getMachineName
     * @return 
     */
    static public String getMachineName() {
        return EgroupwareDLLEmulator._machineName;
    }
    
    /**
     * logInfo
     * log a message as info
     * @param msg 
     */
    static public void logInfo(String msg) {
        Logger.getLogger(
            EgroupwareDLLEmulator.class.getName()).log(Level.INFO, msg);
    }
    
    /**
     * logError
     * log a message as error
     * @param msg 
     */
    static public void logError(String msg) {
        Logger.getLogger(
            EgroupwareDLLEmulator.class.getName()).log(Level.SEVERE, msg);
    }
    
    /**
     * getUsername
     * @param sessionId
     * @return 
     */
    static public String getUsername(int sessionId) {
        return EgroupwareDLLEmulator._username;
    }
    
    /**
     * logoffSession
     * @param sessionId
     * @return 
     */
    static public boolean logoffSession(int sessionId) {
        return true;
    }
	
	/**
	 * getSetting
	 * @param name
	 * @return 
	 */
	static public String getSetting(String name) {
		if( EgroupwareDLLEmulator._settings.containsKey(name) ) { 
            return (String) EgroupwareDLLEmulator._settings.get(name); 
        }
		
		return "";
	}
	
	/**
	 * setSetting
	 * @param name
	 * @param value 
	 */
	static public void setSetting(String name, String value) {
		if( EgroupwareDLLEmulator._settings.containsKey(name) ) {
            EgroupwareDLLEmulator._settings.remove(name);
        }
 
        EgroupwareDLLEmulator._settings.put(name, value);
	}
}