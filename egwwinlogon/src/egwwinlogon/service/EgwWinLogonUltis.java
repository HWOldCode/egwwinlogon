/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import egwwinlogon.service.crypt.EgwWinLogonCryptAes;
import egwwinlogon.updater.WinLogonUpdater;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;

/**
 * EgwWinLogonUltis
 * @author Stefan Werfling
 */
public class EgwWinLogonUltis {
    
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgwWinLogonUltis.class);
    
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
            appDir = EgroupwarePGina.getAppDir();
        }
        catch( Exception ex ) {
            logger.error("Error getUserAppCmd: " + ex.getMessage());
        }
        
        String appCmd = "\"" + EgwWinLogonUltis.getJavaInstallationPath() + 
            "\\bin\\javaw.exe\" -jar \"" + appDir + 
            "egwwinlogon.jar\" " + params;
        
        return appCmd;
    }
    
    /**
     * getUpdaterAppCmd
     * @return 
     */
    static public String getUpdaterAppCmd() {
        String appDir = "";
        
        try {
            appDir = EgroupwarePGina.getAppDir();
        }
        catch( Exception ex ) {
            logger.error("Error getUserAppCmd: " + ex.getMessage());
        }
        
        String appCmd = "\"" + EgwWinLogonUltis.getJavaInstallationPath() + 
            "\\bin\\javaw.exe\" -cp \"" + appDir + 
            "egwwinlogon.jar\" egwwinlogon.updater.WinLogonUpdater ";
        
        return appCmd;
    }
    
    /**
     * getProtocolAppCmd
     * @return 
     */
    static public String getProtocolAppCmd() {
        String appDir = "";
        
        try {
            appDir = EgroupwarePGina.getAppDir();
        }
        catch( Exception ex ) {
            logger.error("Error getUserAppCmd: " + ex.getMessage());
        }
        
        String appCmd = "\"" + EgwWinLogonUltis.getJavaInstallationPath() + 
            "\\bin\\javaw.exe\" -cp \"" + appDir + 
            "egwwinlogon.jar\" egwwinlogon.protocol.EgwWinLogonProtocol ";
        
        return appCmd;
    }
    
    /**
     * pingUrl
     * 
     * @param url
     * @return 
     */
    static public boolean pingUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();

            if( responseCode != 200 ) {
                return false;
            }

            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }
    
    /**
     * getCurrentJarPath
     * @return
     * @throws UnsupportedEncodingException 
     */
    static public String getCurrentJarPath() throws UnsupportedEncodingException {
        String path = WinLogonUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        decodedPath = decodedPath.replace("egwwinlogon.jar", "");
        decodedPath = decodedPath.substring(1);
        
        return decodedPath;
    }
    
    /**
     * getStrEncode
     * 
     * @param content
     * @param k
     * @return
     * @throws Exception 
     */
    static public String getStrEncode(String content, String k) throws Exception {
        return EgwWinLogonCryptAes.encode(content, k);
    }
    
    /**
     * getStrDecode
     * @param content
     * @param k
     * @return
     * @throws Exception 
     */
    static public String getStrDecode(String content, String k) throws Exception {
        return EgwWinLogonCryptAes.decode(content, k);
    }
}