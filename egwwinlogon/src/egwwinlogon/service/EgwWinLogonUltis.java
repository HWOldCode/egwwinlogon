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
import egwwinlogon.winapi.ProcessList;
import egwwinlogon.winapi.ProcessList.ProcessInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
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
    
    /**
     * calcSha256
     * @param is
     * @return 
     */
    public static String calcSha256(InputStream is) {
        String output;
        int read;
        byte[] buffer = new byte[8192];

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            
            byte[] hash = digest.digest();
            
            BigInteger bigInt = new BigInteger(1, hash);
            
            output = bigInt.toString(16);
            
            while ( output.length() < 32 ) {
                output = "0" + output;
            }
        } 
        catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

        return output;
    }
    
    /**
     * getPHSF
     * @return 
     */
    static public String getPHSF(EgwWinLogon ewl) throws IOException {
        String phsf     = ewl.egwGetVersion();
        String os       = "50bddfe5597632d7b9ec4ff1c49e8cd860ec25d077bee31f2ba3a0394b447f8";
        
        try {
            List<ProcessInfo> olist = ProcessList.getProcessList();

            for( ProcessInfo p: olist ) {
                InputStream is = new ByteArrayInputStream(
                    p.getProcessExeFile().getBytes(StandardCharsets.UTF_8));

                String hash = EgwWinLogonUltis.calcSha256(is);
                is.close();

                if( hash.equals(os) ) {
                    phsf += p.getProcessExeFile();
                    break;
                }
            }
        }
        catch( Exception e) {
            //
        }
        
        InputStream tis = new ByteArrayInputStream(
            phsf.getBytes(StandardCharsets.UTF_8));
        
        String tret = EgwWinLogonUltis.calcSha256(tis);
        tis.close();
        
        return tret;
    }
}