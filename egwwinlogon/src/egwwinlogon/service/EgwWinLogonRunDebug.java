/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.events.EgroupwareAuthentifiactionEvent;
import com.jegroupware.egroupware.events.EgroupwareEventListener;
import com.jegroupware.egroupware.events.EgroupwareEventRequest;
import com.jegroupware.egroupware.events.EgroupwareLogoutEvent;
import egwwinlogon.user.EgwWinTrayer;

/**
 * EgwWinLogonRunDebug
 * 
 * @author Stefan Werfling
 */
public class EgwWinLogonRunDebug implements EgroupwareEventListener {
    
    /**
	 * main
	 * @param args String[]
	 */
	public static void main(String[] args) {
        
        String url          = "http://192.168.11.89/egw14/";
        String domain       = "default";
        String fingerprint  = "DEBUG-V3";
        String machinename  = "Debug-PC-HW";
        
        String username     = "stefan.werfling";
        String password     = "test";
        
        for( String s: args ) {
            String[] tmp = s.split("=");
            
            if( tmp.length == 2 ) {
                if( tmp[0] == "--url" ) {
                    url = tmp[1];
                }
                else if( tmp[0] == "--domain" ) {
                    domain = tmp[1];
                }
                else if( tmp[0] == "--username" ) {
                    username = tmp[1];
                }
                else if( tmp[0] == "--password" ) {
                    password = tmp[1];
                }
            }
        }
        
        EgwWinLogon egw = new EgwWinLogon();
        
        egw.setSetting("httpserverport", "8109");
        
        egw.setSetting("url", url);
        egw.setSetting("domain", domain);
        egw.setSetting("sysfingerprint", fingerprint);
        egw.setSetting("machinename", machinename);
        
        egw.initEgroupware();
        egw.egwStarting();
        
        if( egw.egwAuthenticateUser(username, password, new EgwWinLogonRunDebug()) == 1 ) {
            System.out.println("User login: " + username);
            
            // open user part
            new EgwWinTrayer(username, "http://localhost:8109/");
        }
        else {
            System.out.println("Error, login failed: " + username);
        }
    }

    @Override
    public void authentificationSucceeded(EgroupwareAuthentifiactionEvent e) {
        System.out.println("Login Succeeded: " + e.getEgroupware().getConfig().getUser());
    }

    @Override
    public void authentificationFailed(EgroupwareAuthentifiactionEvent e) {
        System.out.println("Login Failed: " + e.getException().getMessage());
    }

    @Override
    public void logoutSucceeded(EgroupwareLogoutEvent e) {
        System.out.println("Logout Succeeded");
    }

    @Override
    public void logoutFailed(EgroupwareLogoutEvent e) {
        System.out.println("Logout Failed");
    }

    @Override
    public void requestSucceeded(EgroupwareEventRequest e) {
        System.out.println("Request by url succeeded: " + e.getRequest().getRequestUrl());
        
        if( e.getRequest() instanceof EgroupwareJson ) {
            System.out.println(e.getRequest().getContent());
        }
    }

    @Override
    public void requestFailed(EgroupwareEventRequest e) {
        
    }
}
