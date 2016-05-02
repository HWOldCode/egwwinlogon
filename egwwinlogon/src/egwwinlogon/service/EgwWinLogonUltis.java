/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;


import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.service.crypt.EgwWinLogonCryptAes;
import egwwinlogon.updater.WinLogonUpdater;
import egwwinlogon.winapi.AdvApi32;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import javax.mail.*;
import javax.mail.internet.*;

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
            "\\bin\\javaw.exe\" -cp \"" + appDir + 
            "egwwinlogon.jar\" egwwinlogon.user.EgwWinTrayer " + params;
        
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
            logger.error("Error getUpdaterAppCmd: " + ex.getMessage());
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
            logger.error("Error getProtocolAppCmd: " + ex.getMessage());
        }
        
        String appCmd = "\"" + EgwWinLogonUltis.getJavaInstallationPath() + 
            "\\bin\\javaw.exe\" -cp \"" + appDir + 
            "egwwinlogon.jar\" egwwinlogon.protocol.EgwWinLogonProtocol ";
        
        return appCmd;
    }
    
	/**
	 * getReLoginAppCmd
	 * @return 
	 */
	static public String getReLoginAppCmd() {
		String appDir = "";
        
        try {
            appDir = EgroupwarePGina.getAppDir();
        }
        catch( Exception ex ) {
            logger.error("Error getReLoginAppCmd: " + ex.getMessage());
        }
        
        String appCmd = "\"" + EgwWinLogonUltis.getJavaInstallationPath() + 
            "\\bin\\javaw.exe\" -cp \"" + appDir + 
            "egwwinlogon.jar\" egwwinlogon.user.EgwWinPromptCredentials ";
        
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

            // OK or FOUND
            if( (responseCode != 200) && (responseCode != 302) ) {
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
	 * checkWindowsProfile
	 * @param username
	 * @return 
	 */
	static public Boolean checkWindowsProfile(String username) {
		try {
			Account taccount = Advapi32Util.getAccountByName(username);
			String sid = taccount.sidString;
			
			String profileList = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\ProfileList";
			
			String[] keylist = Advapi32Util.registryGetKeys(
				WinReg.HKEY_LOCAL_MACHINE,
				profileList
				);
			
			LinkedList<String> mkeylist = new LinkedList<String>(Arrays.asList(keylist));
			
			if( mkeylist.contains(sid) ) {
				if( mkeylist.contains(sid + ".bak") ) {
					Advapi32Util.registryDeleteKey(
						WinReg.HKEY_LOCAL_MACHINE, 
						profileList, 
						sid);
					
					int treturn = AdvApi32.INSTANCE.RegRenameKey(
						WinReg.HKEY_LOCAL_MACHINE, 
						profileList + "\\" + sid + ".bak",
						profileList + "\\" + sid
						);
					
					logger.info("Reg-Profile-Fix: " + sid);
					
					return true;
				}
			}
		}
		catch( Exception ex ) {
			logger.error("Error checkWindowsProfile: " + ex.getMessage());
		}
		
		return false;
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
    
    /**
     * pathUriValid
     * 
     * @param path
     * @return 
     */
    static public String pathUriValid(String path) {
        path = path.replace("\\", "/");
        String lastchar = path.substring(path.length() - 1);
        
        if( lastchar != "/" ) {
            path += "/";
        }
        
        return path;
    }
    
    /**
     * sendEmail
     * 
     * @param host
     * @param user
     * @param password
     * @param to
     * @param subject
     * @param message
     * @return 
     */
    static public Boolean sendEmail(String host, String user, String password, String to, String subject, String message) {
        final Properties props = new Properties();
        
        props.setProperty( "mail.smtp.host", "smtp.gmail.com");
        props.setProperty( "mail.smtp.auth", "true");
        props.setProperty( "mail.smtp.port", "465");
        props.setProperty( "mail.smtp.user", user);
        props.setProperty( "mail.smtp.password", password);
        props.setProperty( "mail.smtp.socketFactory.port", "465" );
        props.setProperty( "mail.smtp.socketFactory.class",
                           "javax.net.ssl.SSLSocketFactory" );
        props.setProperty( "mail.smtp.socketFactory.fallback", "false" );
        
        Session tsession = Session.getInstance( props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( 
                    props.getProperty( "mail.smtp.user" ),
                    props.getProperty( "mail.smtp.password" ));
            }
          });
        
        Message msg = new MimeMessage(tsession);

        try {
            InternetAddress addressTo = new InternetAddress( to );
            msg.setRecipient(Message.RecipientType.TO, addressTo);

            msg.setSubject( subject );
            msg.setContent( message, "text/plain" );
            Transport.send( msg );
        }
        catch( Exception ex ) {
            return false;
        }
        
        return true;
    }
	
	/**
	 * encodeURIComponent
	 * @param s
	 * @return 
	 */
	public static String encodeURIComponent(String s) {
		String result;

		try {
			result = URLEncoder.encode(s, "UTF-8")
				.replaceAll("\\+", "%20")
				.replaceAll("\\%21", "!")
				.replaceAll("\\%27", "'")
				.replaceAll("\\%28", "(")
				.replaceAll("\\%29", ")")
				.replaceAll("\\%7E", "~");
		} 
		catch( UnsupportedEncodingException e ) {
			result = s;
		}

		return result;
	}
	
	/**
	 * getCurrentSystemUser
	 * @return 
	 */
	public static String getCurrentSystemUser() {
		return Advapi32Util.getUserName();
	}
}