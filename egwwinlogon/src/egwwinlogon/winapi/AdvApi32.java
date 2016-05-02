/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.ptr.PointerByReference;

/**
 * MoreAdvApi32
 * @author Stefan Werfling
 */
public interface AdvApi32 extends Advapi32 {
    
	public static final int LOGON_WITH_PROFILE          = 0x00000001;
    public static final int LOGON_NETCREDENTIALS_ONLY   = 0x00000002;


    public static final int CREATE_NO_WINDOW            = 0x08000000;
    public static final int CREATE_UNICODE_ENVIRONMENT  = 0x00000400;
    public static final int CREATE_NEW_CONSOLE          = 0x00000010;
    public static final int DETACHED_PROCESS            = 0x00000008;
	
	public static final int SE_UNKNOWN_OBJECT_TYPE		= 0x00000000;
	public static final int SE_FILE_OBJECT				= 0x00000001;
	public static final int SE_SERVICE					= 0x00000002;
	public static final int SE_PRINTER					= 0x00000003;
	public static final int SE_REGISTRY_KEY				= 0x00000004;
	public static final int SE_LMSHARE					= 0x00000005;
	public static final int SE_KERNEL_OBJECT			= 0x00000006;
	public static final int SE_WINDOW_OBJECT			= 0x00000007;
	public static final int SE_DS_OBJECT				= 0x00000008;
	public static final int SE_DS_OBJECT_ALL			= 0x00000009;
	public static final int SE_PROVIDER_DEFINED_OBJECT	= 0x00000010;
	public static final int SE_WMIGUID_OBJECT			= 0x00000011;
	public static final int SE_REGISTRY_WOW64_32KEY		= 0x00000012;
	
	
    AdvApi32 INSTANCE =
        (AdvApi32) Native.loadLibrary("AdvApi32", AdvApi32.class);
    
    /*
   * BOOL WINAPI CreateProcessWithLogonW( __in LPCWSTR lpUsername,
   * __in_opt LPCWSTR lpDomain, __in LPCWSTR lpPassword, __in DWORD
   * dwLogonFlags, __in_opt LPCWSTR lpApplicationName, __inout_opt LPWSTR
   * lpCommandLine, __in DWORD dwCreationFlags, __in_opt LPVOID
   * lpEnvironment, __in_opt LPCWSTR lpCurrentDirectory, __in
   * LPSTARTUPINFOW lpStartupInfo, __out LPPROCESS_INFORMATION
   * lpProcessInfo );
  */

	// http://msdn.microsoft.com/en-us/library/windows/desktop/ms682431%28v=vs.85%29.aspx
	boolean CreateProcessWithLogonW
            (WString lpUsername,
             WString lpDomain,
             WString lpPassword,
             int dwLogonFlags,
             WString lpApplicationName,
             WString lpCommandLine,
             int dwCreationFlags,
             Pointer lpEnvironment,
             WString lpCurrentDirectory,
             STARTUPINFO  lpStartupInfo,
             PROCESS_INFORMATION lpProcessInfo);
	
	/**
	 * RegRenameKey
	 * @param hKey
	 * @param oldName
	 * @param newName
	 * @return 
	 */
	int RegRenameKey(HKEY hKey, String oldName, String newName);
	
	/**
	 * GetSecurityInfo
	 * DWORD WINAPI GetSecurityInfo(
	 * _In_      HANDLE               handle,
	 * _In_      SE_OBJECT_TYPE       ObjectType,
	 * _In_      SECURITY_INFORMATION SecurityInfo,
	 * _Out_opt_ PSID                 *ppsidOwner,
	 * _Out_opt_ PSID                 *ppsidGroup,
	 * _Out_opt_ PACL                 *ppDacl,
	 * _Out_opt_ PACL                 *ppSacl,
	 * _Out_opt_ PSECURITY_DESCRIPTOR *ppSecurityDescriptor
	 * );
	 * 
	 * @param handle
	 * @param ObjectType
	 * @param SecurityInfo
	 * @param ppsidOwner
	 * @param ppsidGroup
	 * @param ppDacl
	 * @param ppSacl
	 * @param ppSecurityDescriptor
	 * @return 
	 */
	int GetSecurityInfo(
		HANDLE handle, 
		int ObjectType, 
		int SecurityInfo, 
		PointerByReference ppsidOwner,
		PointerByReference ppsidGroup,
		PointerByReference ppDacl,
		PointerByReference ppSacl,
		PointerByReference ppSecurityDescriptor);
}
