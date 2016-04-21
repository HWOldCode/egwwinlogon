/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;

/**
 * User32Ex
 * @author Stefan Werfling
 */
public interface User32Ex extends User32 {
	
	User32Ex INSTANCE =
        (User32Ex) Native.loadLibrary("User32", User32Ex.class);
	
	/**
	 * GetDesktopWindow
	 * HWND WINAPI GetDesktopWindow(void);
	 * @return 
	 */
	HWND GetDesktopWindow();
}
