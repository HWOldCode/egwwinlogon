/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

/**
 * User32Ex
 * @author Stefan Werfling
 */
public interface User32Ex extends User32 {
	
	User32Ex INSTANCE =
        (User32Ex) Native.loadLibrary("User32", User32Ex.class, W32APIOptions.DEFAULT_OPTIONS);
	
	/**
	 * GetDesktopWindow
	 * HWND WINAPI GetDesktopWindow(void);
	 * @return 
	 */
	HWND GetDesktopWindow();
	
	/**
	 * OpenDesktop
	 * 
	 * @param lpszDesktop
	 * @param dwFlags
	 * @param fInherit
	 * @param dwDesiredAccess
	 * @return 
	 */
	HWND OpenDesktop(
		char[] lpszDesktop,
		int dwFlags,
		boolean fInherit,
		long dwDesiredAccess);
	
	/**
	 * SwitchDesktop
	 * @param hDesktop
	 * @return 
	 */
	boolean SwitchDesktop(HWND hDesktop);
	
	/**
	 * CloseDesktop
	 * @param hDesktop
	 * @return 
	 */
	boolean CloseDesktop(HWND hDesktop);
}
