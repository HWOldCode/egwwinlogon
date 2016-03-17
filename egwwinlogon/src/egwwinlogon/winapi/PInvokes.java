/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.PointerByReference;
import egwwinlogon.winapi.mpr.Mpr;
import egwwinlogon.winapi.mpr.NETRESOURCEW;

/**
 * PInvokes
 * @author Stefan Werfling
 */
public class PInvokes {
	
	/**
	 * mapNetworkDrive
	 * @param unc
	 * @param drive
	 * @param user
	 * @param password
	 * @throws Exception 
	 */
	static public void mapNetworkDrive(String unc, String drive, String user, String password) throws Exception {
		NETRESOURCEW lpNetResource = new NETRESOURCEW();
		
		lpNetResource.lpLocalName = new WString(drive);
		lpNetResource.lpRemoteName = new WString(unc);
		
		int result = Mpr.INSTANCE.WNetAddConnection2W(
			lpNetResource, 
			new WString(password), 
			new WString(user),
			0
			);
		
		if( result != 0 ) {
			String message = String.format("Failed to map drive {0}", unc);
			 
			if( result == 86 ||  // ERROR_INVALID_PASSWORD
				result == 2202 ||  // ERROR_BAD_USERNAME
				result == 1326 )  // ERROR_LOGON_FAILURE
			{
				message += ": invalid username or password.";
			}
			else {
				message += String.format(": native error code = {0}", result);
			}
			
			throw new Exception(message);
		}
	}
	
	/**
	 * unmapNetworkDrive
	 * @param drive
	 * @throws Exception 
	 */
	static public void unmapNetworkDrive(String drive) throws Exception {
		int result = Mpr.INSTANCE.WNetCancelConnection2W(new WString(drive), 0, 0);
		
		if( result != 0 ) {
			String message = String.format("Failed to unmap drive {0}", drive);
			message += String.format(": native error code = {0}", result);
			
			throw new Exception(message);
		}
	}
	
	/**
	 * mapDriveInSession
	 * @param session
	 * @param unc
	 * @param drive
	 * @param user
	 * @param password 
	 */
	static public void mapDriveInSession(int session, String unc, String drive, String user, String password) throws Exception {
		PointerByReference userToken = new PointerByReference();
		
		if( !Wtsapi32.INSTANCE.WTSQueryUserToken(session, userToken) ) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		
		HANDLE _handle = new WinNT.HANDLE(userToken.getValue());
		
		try {
			if( !AdvApi32.INSTANCE.ImpersonateLoggedOnUser(_handle) ) {
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
			}
			
			PInvokes.mapNetworkDrive(unc, drive, user, password);
		}
		finally {
			AdvApi32.INSTANCE.RevertToSelf();
			
			if( _handle != null ) {
				Kernel32.INSTANCE.CloseHandle(_handle);
			}
		}
	}
	
	/**
	 * unmapDriveInSession
	 * @param session
	 * @param drive
	 * @throws Exception 
	 */
	static public void unmapDriveInSession(int session, String drive) throws Exception {
		PointerByReference userToken = new PointerByReference();
		
		if( !Wtsapi32.INSTANCE.WTSQueryUserToken(session, userToken) ) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		
		HANDLE _handle = new WinNT.HANDLE(userToken.getValue());
		
		try {
			if( !AdvApi32.INSTANCE.ImpersonateLoggedOnUser(_handle) ) {
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
			}
			
			PInvokes.unmapNetworkDrive(drive);
		}
		finally {
			AdvApi32.INSTANCE.RevertToSelf();
			
			if( _handle != null ) {
				Kernel32.INSTANCE.CloseHandle(_handle);
			}
		}
	}
	
	/**
	 * test
	 */
	static void test() {
		Kernel32.INSTANCE.ProcessIdToSessionId(null, null);
	}
}