/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Userenv
 * 
 * @see https://msdn.microsoft.com/de-de/library/windows/desktop/bb776901(v=vs.85).aspx
 * @author Stefan Werfling
 */
public interface Userenv extends Library {
	
	Userenv INSTANCE = (Userenv) Native.loadLibrary("Userenv", Userenv.class);
	
	/**
	 * GetUserProfileDirectoryW
	 * 
	 * BOOL WINAPI GetUserProfileDirectory(
	 * _In_      HANDLE  hToken,
	 * _Out_opt_ LPTSTR  lpProfileDir,
	 * _Inout_   LPDWORD lpcchSize
	 * );
	 * 
	 * @param hToken
	 * @param lpProfileDir
	 * @param lpcchSize
	 * @return 
	 */
	boolean GetUserProfileDirectoryW(HANDLE hToken , char[] lpProfileDir , IntByReference lpcchSize);
	
	/**
	 * DeleteProfile
	 * 
	 * BOOL WINAPI DeleteProfile(
	 *	_In_     LPCTSTR lpSidString,
	 * _In_opt_ LPCTSTR lpProfilePath,
	 * _In_opt_ LPCTSTR lpComputerName
	 * );
	 * 
	 * @param lpSidString
	 * @param lpProfilePath
	 * @param lpComputerName
	 * @return 
	 */
	boolean DeleteProfile(WString lpSidString, WString lpProfilePath, WString lpComputerName);
}
