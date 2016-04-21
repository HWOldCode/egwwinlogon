/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.sun.jna.WString;
import egwwinlogon.winapi.PInvokes;
import egwwinlogon.winapi.mpr.Mpr;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NetShare
 * @author Stefan Werfling
 */
public class NetShare {
	
	/**
	 * main
	 * @param args 
	 */
	public static void main(String[] args) {
		PInvokes.getCredentials("Test", "Hallo?");
		
		/*int session = 2;
		
		try {
			//PInvokes.mapDriveInSession(session, "\\\\192.168.11.4\\public", "G:", "admin", "1234");
			PInvokes.mapNetworkDrive("\\\\192.168.11.4\\public", "G:", "admin", "1234");
		} catch (Exception ex) {
			Logger.getLogger(NetShare.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		Mpr.INSTANCE.WNetCancelConnection2W(new WString("G:"), 0, 0);
		System.out.println("Test");*/
	}
}
