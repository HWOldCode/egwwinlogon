/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.events.EgroupwareAuthentifiactionEvent;
import com.jegroupware.egroupware.events.EgroupwareEvent;
import com.jegroupware.egroupware.events.EgroupwareEventListener;
import com.jegroupware.egroupware.events.EgroupwareEventRequest;
import com.jegroupware.egroupware.events.EgroupwareLogoutEvent;
import egwwinlogon.user.EgwWinTrayer;

/**
 * EgwWinLogonRunDebug
 * @author Stefan Werfling
 */
public class EgwWinLogonRunDebug implements EgroupwareEventListener {
    
    /**
     * main
     * @param args String[]
     */
    public static void main(String[] args) {
        
        // emulator
        EgroupwarePGina.setUseEmulator(true);
        
        // default vars
        //String url          = "http://192.168.0.37/egroupware/";	// 192.168.11.51
        String url          = "http://192.168.11.55/egroupware/";	// 192.168.11.51
        //String url          = "https://www.hw-softwareentwicklung.de/egroupware/";
        String domain       = "default";
        String fingerprint  = "DEBUG-V3";
        String machinename  = "Debug-PC-HW";
        
        String username     = "win.logon";
        String password     = "123456";
        
        for( String s: args ) {
            System.out.println("Argument: " + s);
            
            String[] tmp = s.split("=");
            
            System.out.println("Argument-Split: " + Integer.toString(tmp.length));
            
            if( tmp.length == 2 ) {
                if( tmp[0].contains("--url") ) {
                    url = tmp[1];
                }
                else if( tmp[0].contains("--domain") ) {
                    domain = tmp[1];
                }
                else if( tmp[0].contains("--username") ) {
                    username = tmp[1];
                }
                else if( tmp[0].contains("--password") ) {
                    password = tmp[1];
                }
            }
        }
        
        EgwWinLogon egw = new EgwWinLogon();
        
        EgroupwareDLLEmulator.setSetting("httpserverport", "8109");
        
        EgroupwareDLLEmulator.setSetting("url", url);
        EgroupwareDLLEmulator.setSetting("domain", domain);
        EgroupwareDLLEmulator.setSetting("sysfingerprint", fingerprint);
        EgroupwareDLLEmulator.setSetting("machinename", machinename);
        
        egw.initEgroupware();
        egw.egwStarting();
        
        System.out.println("Url: " + url);
        
        if( egw.egwAuthenticateUser(username, password, domain, 
            0, new EgwWinLogonRunDebug()) == 1 ) 
        {
			// simulate logon
			egw.egwSessionChange("5", username, 1);
			
            System.out.println("User login: " + username);
            
            // open user part
            new EgwWinTrayer(username, "http://localhost:8109/");
        }
        else {
            System.out.println("Error, login failed: " + username);
        }
    }

	/**
	 * authentificationSucceeded
	 * @param e 
	 */
    @Override
    public void authentificationSucceeded(EgroupwareAuthentifiactionEvent e) {
        System.out.println("Login Succeeded: " + e.getEgroupware().getConfig().getUser());
    }

	/**
	 * authentificationFailed
	 * @param e 
	 */
    @Override
    public void authentificationFailed(EgroupwareAuthentifiactionEvent e) {
        System.out.println("Login Failed: " + e.getException().getMessage());
    }

	/**
	 * logoutSucceeded
	 * @param e 
	 */
    @Override
    public void logoutSucceeded(EgroupwareLogoutEvent e) {
        System.out.println("Logout Succeeded");
    }

	/**
	 * logoutFailed
	 * @param e 
	 */
    @Override
    public void logoutFailed(EgroupwareLogoutEvent e) {
        System.out.println("Logout Failed");
    }

	/**
	 * requestSucceeded
	 * @param e 
	 */
    @Override
    public void requestSucceeded(EgroupwareEventRequest e) {
        System.out.println("Request by url succeeded: " + e.getRequest().getRequestUrl());
        
        if( e.getRequest() instanceof EgroupwareJson ) {
            System.out.println(e.getRequest().getContent());
        }
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
