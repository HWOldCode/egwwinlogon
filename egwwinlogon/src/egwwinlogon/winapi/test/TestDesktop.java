/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi.test;

import egwwinlogon.winapi.PInvokes;

/**
 * TestDesktop
 * @author Stefan Werfling
 */
public class TestDesktop {
	
	/**
	 * main
	 * @param args String[]
	 */
	public static void main(String[] args) {
		if( PInvokes.isDesktopLocked() ) {
			System.out.println("Locked");
		}
		
		System.out.println("UnLocked");
	}
}
