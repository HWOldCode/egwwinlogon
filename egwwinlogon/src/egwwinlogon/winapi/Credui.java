/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;
import java.util.Arrays;
import java.util.List;

/**
 * Credui
 * @author Stefan Werfling
 */
public interface Credui extends Library {
	
	public static final int CREDUIWIN_GENERIC					= 0x1;
	public static final int CREDUIWIN_CHECKBOX					= 0x2;
	public static final int CREDUIWIN_AUTHPACKAGE_ONLY			= 0x10;
	public static final int CREDUIWIN_IN_CRED_ONLY				= 0x20;
	public static final int CREDUIWIN_ENUMERATE_ADMINS			= 0x100;
	public static final int CREDUIWIN_ENUMERATE_CURRENT_USER	= 0x200;
	public static final int CREDUIWIN_SECURE_PROMPT				= 0x1000;
	public static final int CREDUIWIN_PACK_32_WOW				= 0x10000000;
	
	/**
	 * INSTANCE
	 */
	Credui INSTANCE = (Credui) Native.loadLibrary("Credui", Credui.class, W32APIOptions.UNICODE_OPTIONS);
	
	/**
	 * CredUIPromptForCredentials
	 * DWORD WINAPI CredUIPromptForCredentials(
	 * _In_opt_ PCREDUI_INFO pUiInfo,
	 * _In_     PCTSTR       pszTargetName,
	 * _In_     PCtxtHandle  Reserved,
	 * _In_opt_ DWORD        dwAuthError,
	 * _Inout_  PCTSTR       pszUserName,
	 * _In_     ULONG        ulUserNameMaxChars,
	 * _Inout_  PCTSTR       pszPassword,
	 * _In_     ULONG        ulPasswordMaxChars,
	 * _Inout_  PBOOL        pfSave,
	 * _In_     DWORD        dwFlags
	 * );
	 * 
	 * @return 
	 */
	int CredUIPromptForCredentials(
			Pointer pUiInfo, 
			WString pszTargetName,
			Sspi.CtxtHandle Reserved,
			int dwAuthError,
			WString pszUserName,
			NativeLong ulUserNameMaxChars,
			WString pszPassword,
			NativeLong ulPasswordMaxChars,
			Pointer pfSave,
			int dwFlags
			);
	
	/**
	 * CredUIPromptForWindowsCredentials
	 * DWORD WINAPI CredUIPromptForWindowsCredentials(
	 * _In_opt_    PCREDUI_INFO pUiInfo,
	 * _In_        DWORD        dwAuthError,
	 * _Inout_     ULONG        *pulAuthPackage,
	 * _In_opt_    LPCVOID      pvInAuthBuffer,
	 * _In_        ULONG        ulInAuthBufferSize,
	 * _Out_       LPVOID       *ppvOutAuthBuffer,
	 * _Out_       ULONG        *pulOutAuthBufferSize,
	 * _Inout_opt_ BOOL         *pfSave,
	 * _In_        DWORD        dwFlags
	 * );
	 * 
	 * @return 
	 */
	int CredUIPromptForWindowsCredentials(
		CREDUI_INFO pUiInfo,
		int dwAuthError,
		ULONGByReference pulAuthPackage,
		Pointer pvInAuthBuffer,
		ULONG ulInAuthBufferSize,
		PointerByReference ppvOutAuthBuffer,
		ULONGByReference pulOutAuthBufferSize,
		IntByReference pfSave,
		int dwFlags
		);
	
	/**
	 * CredUnPackAuthenticationBuffer
	 * BOOL WINAPI CredUnPackAuthenticationBuffer(
	 * _In_    DWORD  dwFlags,
	 * _In_    PVOID  pAuthBuffer,
	 * _In_    DWORD  cbAuthBuffer,
	 * _Out_   LPTSTR pszUserName,
	 * _Inout_ DWORD  *pcchMaxUserName,
	 * _Out_   LPTSTR pszDomainName,
	 * _Inout_ DWORD  *pcchMaxDomainname,
	 * _Out_   LPTSTR pszPassword,
	 * _Inout_ DWORD  *pcchMaxPassword
	 * );
	 * 
	 * @return 
	 */
	boolean CredUnPackAuthenticationBuffer(
		int dwFlags,
		PVOID pAuthBuffer,
		int cbAuthBuffer,
		char[] pszUserName,
		DWORDByReference pcchMaxUserName,
		char[] pszDomainName,
		DWORDByReference pcchMaxDomainname,
		char[] pszPassword,
		DWORDByReference pcchMaxPassword
		);
	
	/**
	 * CREDUI_INFO
	 * 
	 * typedef struct _CREDUI_INFO {
	 * DWORD   cbSize;
	 * HWND    hwndParent;
	 * PCTSTR  pszMessageText;
	 * PCTSTR  pszCaptionText;
	 * HBITMAP hbmBanner;
	 * } CREDUI_INFO, *PCREDUI_INFO;
	 */
	public static class CREDUI_INFO extends Structure {

		public int cbSize;
		
		public HWND hwndParent;
		
		public WString pszMessageText;
		
		public WString pszCaptionText;
		
		public HBITMAP hbmBanner;
		
		/**
		 * getFieldOrder
		 * @return 
		 */
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[]{
				"cbSize", 
				"hwndParent",
				"pszMessageText",
				"pszCaptionText",
				"hbmBanner",
			});
		}
	}
}
