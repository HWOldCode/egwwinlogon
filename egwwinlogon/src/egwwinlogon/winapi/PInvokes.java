/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import egwwinlogon.winapi.mpr.Mpr;
import egwwinlogon.winapi.mpr.NETRESOURCEW;

/**
 * PInvokes
 * @author Stefan Werfling
 */
public class PInvokes {
	
	static public boolean isDesktopLocked() {
		WinDef.HWND _hwnd = User32Ex.INSTANCE.OpenDesktop(
			"Default".toCharArray(), 0, false, 0x0100);
		
		if( _hwnd != null ) {
			boolean rtn = User32Ex.INSTANCE.SwitchDesktop(_hwnd);
			
			User32Ex.INSTANCE.CloseDesktop(_hwnd);
			
			if( rtn == false ) {	
				return true;
			}
		}
		
		return false;
	}
	
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
	
	static public void getCredentialsInSession(int session, String caption, String message) {
		PointerByReference userToken = new PointerByReference();
		
		if( !Wtsapi32.INSTANCE.WTSQueryUserToken(session, userToken) ) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		
		HANDLE _handle = new WinNT.HANDLE(userToken.getValue());
		
		try {
			if( !AdvApi32.INSTANCE.ImpersonateLoggedOnUser(_handle) ) {
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
			}
			
			PInvokes.getCredentials(caption, message);
		}
		finally {
			AdvApi32.INSTANCE.RevertToSelf();
			
			if( _handle != null ) {
				Kernel32.INSTANCE.CloseHandle(_handle);
			}
		}
	}
	
	/**
	 * getCredentials
	 * @param caption
	 * @param message
	 * @return 
	 */
	static public CredentialData getCredentials(String caption, String message) {
		Credui.CREDUI_INFO info = new Credui.CREDUI_INFO();
		
		info.pszCaptionText = new WString(caption);
		info.pszMessageText = new WString(message);
		info.hwndParent		= User32Ex.INSTANCE.GetDesktopWindow();
		info.cbSize			= info.size();
		
		WinDef.ULONGByReference authPackage = new WinDef.ULONGByReference();
		PointerByReference outCredBuffer = new PointerByReference();
		WinDef.ULONGByReference outCredSize = new WinDef.ULONGByReference();
		IntByReference save = new IntByReference(0);
		WinDef.ULONG ulInAuthBufferSize = new WinDef.ULONG(0);
		
		int result = Credui.INSTANCE.CredUIPromptForWindowsCredentials(
			info,
			0,
			authPackage,
			null,
			ulInAuthBufferSize,
			outCredBuffer,
			outCredSize,
			save,
			Credui.CREDUIWIN_GENERIC
			);
		
		if( result == 0 ) {
			char[] usernameBuf	= new char[100];
			char[] passwordBuf	= new char[100];
			char[] domainBuf	= new char[100];
			
			DWORDByReference maxUserName	= new DWORDByReference(new DWORD(100));
			DWORDByReference maxDomain		= new DWORDByReference(new DWORD(100));
			DWORDByReference maxPassword	= new DWORDByReference(new DWORD(100));
			
			PVOID toutCredBuffer = new WinDef.PVOID(outCredBuffer.getValue());
			int toutCredSize = outCredSize.getValue().intValue();
			
			boolean presult = Credui.INSTANCE.CredUnPackAuthenticationBuffer(
					0, toutCredBuffer, 
					toutCredSize, 
					usernameBuf, maxUserName, 
					domainBuf, maxDomain, 
					passwordBuf, maxPassword);
			
			if( presult )  {
				return new CredentialData(usernameBuf, passwordBuf, domainBuf);
			}
		}
		
		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
	}
	
	/**
	 * CredentialData
	 */
	public static class CredentialData {
		
		public String username	= null;
		public String password	= null;
		public String domain	= null;
		
		/**
		 * CredentialData
		 */
		public CredentialData() {}
		
		/**
		 * CredentialData
		 * @param cusername
		 * @param cpassword
		 * @param cdomain 
		 */
		public CredentialData(char[] cusername, char[] cpassword, char[] cdomain) {
			this.username = new String(cusername);
			this.password = new String(cpassword);
			this.domain = new String(cdomain);
		}
	}
}