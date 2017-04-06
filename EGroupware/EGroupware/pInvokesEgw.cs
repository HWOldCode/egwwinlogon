using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Reflection;
using System.Net;
using System.Net.Security;
using System.Security.Principal;
using System.ComponentModel;
using Microsoft.Win32;
using System.Security.AccessControl;

namespace pGina.Plugin.EGroupware {
    public static class pInvokesEgw {

        internal class SafeNativeMethods {

            #region Structs/Enums
            [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
            public struct CREDUI_INFO {
                public int cbSize;
                public IntPtr hwndParent;
                public string pszMessageText;
                public string pszCaptionText;
                public IntPtr hbmBanner;
            }

            public enum PromptForWindowsCredentialsFlags {
                /// <summary>
                /// The caller is requesting that the credential provider return the user name and password in plain text.
                /// This value cannot be combined with SECURE_PROMPT.
                /// </summary>
                CREDUIWIN_GENERIC = 0x1,
                /// <summary>
                /// The Save check box is displayed in the dialog box.
                /// </summary>
                CREDUIWIN_CHECKBOX = 0x2,
                /// <summary>
                /// Only credential providers that support the authentication package specified by the authPackage parameter should be enumerated.
                /// This value cannot be combined with CREDUIWIN_IN_CRED_ONLY.
                /// </summary>
                CREDUIWIN_AUTHPACKAGE_ONLY = 0x10,
                /// <summary>
                /// Only the credentials specified by the InAuthBuffer parameter for the authentication package specified by the authPackage parameter should be enumerated.
                /// If this flag is set, and the InAuthBuffer parameter is NULL, the function fails.
                /// This value cannot be combined with CREDUIWIN_AUTHPACKAGE_ONLY.
                /// </summary>
                CREDUIWIN_IN_CRED_ONLY = 0x20,
                /// <summary>
                /// Credential providers should enumerate only administrators. This value is intended for User Account Control (UAC) purposes only. We recommend that external callers not set this flag.
                /// </summary>
                CREDUIWIN_ENUMERATE_ADMINS = 0x100,
                /// <summary>
                /// Only the incoming credentials for the authentication package specified by the authPackage parameter should be enumerated.
                /// </summary>
                CREDUIWIN_ENUMERATE_CURRENT_USER = 0x200,
                /// <summary>
                /// The credential dialog box should be displayed on the secure desktop. This value cannot be combined with CREDUIWIN_GENERIC.
                /// Windows Vista: This value is not supported until Windows Vista with SP1.
                /// </summary>
                CREDUIWIN_SECURE_PROMPT = 0x1000,
                /// <summary>
                /// The credential provider should align the credential BLOB pointed to by the refOutAuthBuffer parameter to a 32-bit boundary, even if the provider is running on a 64-bit system.
                /// </summary>
                CREDUIWIN_PACK_32_WOW = 0x10000000,
            }

            public enum ResourceScope
            {
                RESOURCE_CONNECTED = 1,
                RESOURCE_GLOBALNET,
                RESOURCE_REMEMBERED,
                RESOURCE_RECENT,
                RESOURCE_CONTEXT
            }

            public enum ResourceType
            {
                RESOURCETYPE_ANY,
                RESOURCETYPE_DISK,
                RESOURCETYPE_PRINT,
                RESOURCETYPE_RESERVED
            }

            public enum ResourceUsage
            {
                RESOURCEUSAGE_CONNECTABLE = 0x00000001,
                RESOURCEUSAGE_CONTAINER = 0x00000002,
                RESOURCEUSAGE_NOLOCALDEVICE = 0x00000004,
                RESOURCEUSAGE_SIBLING = 0x00000008,
                RESOURCEUSAGE_ATTACHED = 0x00000010,
                RESOURCEUSAGE_ALL = (RESOURCEUSAGE_CONNECTABLE | RESOURCEUSAGE_CONTAINER | RESOURCEUSAGE_ATTACHED),
            }

            public enum ResourceDisplayType
            {
                RESOURCEDISPLAYTYPE_GENERIC,
                RESOURCEDISPLAYTYPE_DOMAIN,
                RESOURCEDISPLAYTYPE_SERVER,
                RESOURCEDISPLAYTYPE_SHARE,
                RESOURCEDISPLAYTYPE_FILE,
                RESOURCEDISPLAYTYPE_GROUP,
                RESOURCEDISPLAYTYPE_NETWORK,
                RESOURCEDISPLAYTYPE_ROOT,
                RESOURCEDISPLAYTYPE_SHAREADMIN,
                RESOURCEDISPLAYTYPE_DIRECTORY,
                RESOURCEDISPLAYTYPE_TREE,
                RESOURCEDISPLAYTYPE_NDSCONTAINER
            }

            [StructLayout(LayoutKind.Sequential)]
            internal class NETRESOURCE
            {
                public ResourceScope dwScope = 0; // Ignored by WNetAddConnection2
                public ResourceType dwType = ResourceType.RESOURCETYPE_DISK;
                public ResourceDisplayType dwDisplayType = 0; // Ignored by WNetAddConnection2
                public ResourceUsage dwUsage = 0;  // Ignored by WNetAddConnection2
                public string lpLocalName = null;
                public string lpRemoteName = null;
                public string lpComment = "";  // Ignored by WNetAddConnection2
                public string lpProvider = null;
            }

            [StructLayout(LayoutKind.Sequential)]
            public struct SECURITY_ATTRIBUTES
            {
                public int nLength;
                public IntPtr lpSecurityDescriptor;
                public int bInheritHandle;
            }

            public enum SECURITY_IMPERSONATION_LEVEL
            {
                SecurityAnonymous,
                SecurityIdentification,
                SecurityImpersonation,
                SecurityDelegation
            }

            [Flags]
            public enum ACCESS_MASK : uint
            {
                DELETE = 0x00010000,
                READ_CONTROL = 0x00020000,
                WRITE_DAC = 0x00040000,
                WRITE_OWNER = 0x00080000,
                SYNCHRONIZE = 0x00100000,
                STANDARD_RIGHTS_REQUIRED = 0x000f0000,
                STANDARD_RIGHTS_READ = 0x00020000,
                STANDARD_RIGHTS_WRITE = 0x00020000,
                STANDARD_RIGHTS_EXECUTE = 0x00020000,
                STANDARD_RIGHTS_ALL = 0x001f0000,
                SPECIFIC_RIGHTS_ALL = 0x0000ffff,
                ACCESS_SYSTEM_SECURITY = 0x01000000,
                MAXIMUM_ALLOWED = 0x02000000,
                GENERIC_READ = 0x80000000,
                GENERIC_WRITE = 0x40000000,
                GENERIC_EXECUTE = 0x20000000,
                GENERIC_ALL = 0x10000000,
                DESKTOP_READOBJECTS = 0x00000001,
                DESKTOP_CREATEWINDOW = 0x00000002,
                DESKTOP_CREATEMENU = 0x00000004,
                DESKTOP_HOOKCONTROL = 0x00000008,
                DESKTOP_JOURNALRECORD = 0x00000010,
                DESKTOP_JOURNALPLAYBACK = 0x00000020,
                DESKTOP_ENUMERATE = 0x00000040,
                DESKTOP_WRITEOBJECTS = 0x00000080,
                DESKTOP_SWITCHDESKTOP = 0x00000100,
                WINSTA_ENUMDESKTOPS = 0x00000001,
                WINSTA_READATTRIBUTES = 0x00000002,
                WINSTA_ACCESSCLIPBOARD = 0x00000004,
                WINSTA_CREATEDESKTOP = 0x00000008,
                WINSTA_WRITEATTRIBUTES = 0x00000010,
                WINSTA_ACCESSGLOBALATOMS = 0x00000020,
                WINSTA_EXITWINDOWS = 0x00000040,
                WINSTA_ENUMERATE = 0x00000100,
                WINSTA_READSCREEN = 0x00000200,
                WINSTA_ALL_ACCESS = 0x0000037f
            }

            public enum TOKEN_TYPE
            {
                TokenPrimary = 1,
                TokenImpersonation
            }

            public enum LogonFlags
            {
                LOGON_WITH_PROFILE = 1,
                LOGON_NETCREDENTIALS_ONLY = 2,
            }

            public enum TOKEN_INFORMATION_CLASS : int
            {
                TokenUser = 1,
                TokenGroups,
                TokenPrivileges,
                TokenOwner,
                TokenPrimaryGroup,
                TokenDefaultDacl,
                TokenSource,
                TokenType,
                TokenImpersonationLevel,
                TokenStatistics,
                TokenRestrictedSids,
                TokenSessionId,
                TokenGroupsAndPrivileges,
                TokenSessionReference,
                TokenSandBoxInert,
                TokenAuditPolicy,
                TokenOrigin,
                MaxTokenInfoClass
            };

            [StructLayout(LayoutKind.Sequential)]
            public struct STARTUPINFO
            {
                public Int32 cb;
                public String lpReserved;
                public String lpDesktop;
                public String lpTitle;
                public UInt32 dwX;
                public UInt32 dwY;
                public UInt32 dwXSize;
                public UInt32 dwYSize;
                public UInt32 dwXCountChars;
                public UInt32 dwYCountChars;
                public UInt32 dwFillAttribute;
                public UInt32 dwFlags;
                public short wShowWindow;
                public short cbReserved2;
                public IntPtr lpReserved2;
                public IntPtr hStdInput;
                public IntPtr hStdOutput;
                public IntPtr hStdError;
            };

            [StructLayout(LayoutKind.Sequential)]
            public struct PROCESS_INFORMATION
            {
                public IntPtr hProcess;
                public IntPtr hThread;
                public UInt32 dwProcessId;
                public UInt32 dwThreadId;
            };

            public const int CREATE_UNICODE_ENVIRONMENT = 0x00000400;

            [StructLayout(LayoutKind.Sequential)]
            public struct WTS_SESSION_INFO
            {
                public Int32 SessionID;

                [MarshalAs(UnmanagedType.LPStr)]
                public String pWinStationName;

                public WTS_CONNECTSTATE_CLASS State;
            }

            public enum WTS_INFO_CLASS
            {
                WTSInitialProgram,
                WTSApplicationName,
                WTSWorkingDirectory,
                WTSOEMId,
                WTSSessionId,
                WTSUserName,
                WTSWinStationName,
                WTSDomainName,
                WTSConnectState,
                WTSClientBuildNumber,
                WTSClientName,
                WTSClientDirectory,
                WTSClientProductId,
                WTSClientHardwareId,
                WTSClientAddress,
                WTSClientDisplay,
                WTSClientProtocolType
            }
            public enum WTS_CONNECTSTATE_CLASS
            {
                WTSActive,
                WTSConnected,
                WTSConnectQuery,
                WTSShadow,
                WTSDisconnected,
                WTSIdle,
                WTSListen,
                WTSReset,
                WTSDown,
                WTSInit
            }

            public static IntPtr WTS_CURRENT_SERVER_HANDLE = IntPtr.Zero;

            /// <summary>
            /// Used with LogonUser
            /// </summary>
            public enum LogonType
            {
                /// <summary>
                /// This logon type is intended for users who will be interactively using the computer, such as a user being logged on  
                /// by a terminal server, remote shell, or similar process.
                /// This logon type has the additional expense of caching logon information for disconnected operations;
                /// therefore, it is inappropriate for some client/server applications,
                /// such as a mail server.
                /// </summary>
                LOGON32_LOGON_INTERACTIVE = 2,

                /// <summary>
                /// This logon type is intended for high performance servers to authenticate plaintext passwords.
                /// The LogonUser function does not cache credentials for this logon type.
                /// </summary>
                LOGON32_LOGON_NETWORK = 3,

                /// <summary>
                /// This logon type is intended for batch servers, where processes may be executing on behalf of a user without 
                /// their direct intervention. This type is also for higher performance servers that process many plaintext
                /// authentication attempts at a time, such as mail or Web servers. 
                /// The LogonUser function does not cache credentials for this logon type.
                /// </summary>
                LOGON32_LOGON_BATCH = 4,

                /// <summary>
                /// Indicates a service-type logon. The account provided must have the service privilege enabled. 
                /// </summary>
                LOGON32_LOGON_SERVICE = 5,

                /// <summary>
                /// This logon type is for GINA DLLs that log on users who will be interactively using the computer. 
                /// This logon type can generate a unique audit record that shows when the workstation was unlocked. 
                /// </summary>
                LOGON32_LOGON_UNLOCK = 7,

                /// <summary>
                /// This logon type preserves the name and password in the authentication package, which allows the server to make 
                /// connections to other network servers while impersonating the client. A server can accept plaintext credentials 
                /// from a client, call LogonUser, verify that the user can access the system across the network, and still 
                /// communicate with other servers.
                /// NOTE: Windows NT:  This value is not supported. 
                /// </summary>
                LOGON32_LOGON_NETWORK_CLEARTEXT = 8,

                /// <summary>
                /// This logon type allows the caller to clone its current token and specify new credentials for outbound connections.
                /// The new logon session has the same local identifier but uses different credentials for other network connections. 
                /// NOTE: This logon type is supported only by the LOGON32_PROVIDER_WINNT50 logon provider.
                /// NOTE: Windows NT:  This value is not supported. 
                /// </summary>
                LOGON32_LOGON_NEW_CREDENTIALS = 9,
            }

            /// <summary>
            /// Used with LogonUser
            /// </summary>
            public enum LogonProvider
            {
                /// <summary>
                /// Use the standard logon provider for the system. 
                /// The default security provider is negotiate, unless you pass NULL for the domain name and the user name 
                /// is not in UPN format. In this case, the default provider is NTLM. 
                /// NOTE: Windows 2000/NT:   The default security provider is NTLM.
                /// </summary>
                LOGON32_PROVIDER_DEFAULT = 0,
            }
            #endregion

            #region credui.dll
            [DllImport("credui.dll", CharSet = CharSet.Auto)]
            public static extern int CredUIPromptForWindowsCredentials(ref CREDUI_INFO uiInfo, int authError, ref uint authPackage,
                                                                         IntPtr InAuthBuffer, uint InAuthBufferSize,
                                                                         out IntPtr refOutAuthBuffer, out uint refOutAuthBufferSize,
                                                                         ref bool fSave, PromptForWindowsCredentialsFlags flags);

            [DllImport("credui.dll", CharSet = CharSet.Auto)]
            public static extern bool CredUnPackAuthenticationBuffer(int dwFlags, IntPtr pAuthBuffer, uint cbAuthBuffer,
                                                                       StringBuilder pszUserName, ref int pcchMaxUserName,
                                                                       StringBuilder pszDomainName, ref int pcchMaxDomainname,
                                                                       StringBuilder pszPassword, ref int pcchMaxPassword);
            #endregion

            #region ole32.dll
            [DllImport("ole32.dll")]
            public static extern void CoTaskMemFree(IntPtr ptr);
            #endregion

            #region mpr.dll
            [DllImport("mpr.dll")]
            public static extern int WNetAddConnection2(NETRESOURCE netResource,
                            string password, string username, int flags);

            [DllImport("mpr.dll")]
            public static extern int WNetCancelConnection2(string name, int flags,
                            bool force);
            #endregion

            #region wtsapi32.dll
            [DllImport("wtsapi32.dll", SetLastError = true)]
            public static extern bool WTSQueryUserToken(int sessionId, out IntPtr Token);

            [DllImport("wtsapi32.dll")]
            public static extern IntPtr WTSOpenServer([MarshalAs(UnmanagedType.LPStr)] String pServerName);

            [DllImport("wtsapi32.dll")]
            public static extern void WTSCloseServer(IntPtr hServer);

            [DllImport("wtsapi32.dll")]
            public static extern int WTSEnumerateSessions(IntPtr hServer,
                [MarshalAs(UnmanagedType.U4)] int Reserved,
                [MarshalAs(UnmanagedType.U4)] int Version,
                ref IntPtr ppSessionInfo,
                [MarshalAs(UnmanagedType.U4)] ref int pCount);

            [DllImport("wtsapi32.dll")]
            public static extern void WTSFreeMemory(IntPtr pMemory);

            [DllImport("Wtsapi32.dll")]
            public static extern bool WTSQuerySessionInformation(System.IntPtr hServer, int sessionId, WTS_INFO_CLASS wtsInfoClass, out System.IntPtr ppBuffer, out uint pBytesReturned);

            [DllImport("wtsapi32.dll")]
            public static extern bool WTSLogoffSession(IntPtr hServer, int sessionId, bool bWait);
            #endregion

            #region userenv.dll
            [DllImport("userenv.dll")]
            public static extern bool DeleteProfile(string sidString, string path, string machine);

            #endregion

            #region kernel32.dll
            [DllImport("kernel32.dll", SetLastError = true)]
            [return: MarshalAs(UnmanagedType.Bool)]
            public static extern bool CloseHandle(IntPtr hObject);
            #endregion

            #region advapi32.dll
            [DllImport("advapi32.dll", SetLastError = true)]
            public extern static bool DuplicateTokenEx(IntPtr hExistingToken, uint dwDesiredAccess, IntPtr tokenAttr,
                SECURITY_IMPERSONATION_LEVEL ImpersonationLevel, TOKEN_TYPE TokenType, out IntPtr phNewToken);

            [DllImport("advapi32.dll", SetLastError = true)]
            [return: MarshalAs(UnmanagedType.Bool)]
            public static extern bool OpenProcessToken(IntPtr ProcessHandle,
                                                        UInt32 DesiredAccess, out IntPtr TokenHandle);

            // TokenInformation is really an IntPtr, but we only ever call this with SessionId, so we ref the int directly
            [DllImport("advapi32.dll", SetLastError = true)]
            public static extern bool SetTokenInformation(IntPtr TokenHandle, TOKEN_INFORMATION_CLASS TokenInformationClass,
                                                           ref int TokenInformation, int TokenInformationLength);

            [DllImport("userenv.dll", SetLastError = true)]
            public static extern Boolean CreateEnvironmentBlock(ref IntPtr lpEnvironment, IntPtr hToken, Boolean bInherit);

            [DllImport("advapi32.dll", SetLastError = true)]
            public static extern bool CreateProcessAsUser(IntPtr hToken, string lpApplicationName, string lpCommandLine, ref SECURITY_ATTRIBUTES lpProcessAttributes,
                                                           ref SECURITY_ATTRIBUTES lpThreadAttributes, bool bInheritHandles, uint dwCreationFlags, IntPtr lpEnvironment,
                                                           string lpCurrentDirectory, ref STARTUPINFO lpStartupInfo, out PROCESS_INFORMATION lpProcessInformation);

            [DllImport("advapi32.dll", SetLastError = true)]
            public static extern bool LogonUser(
                string lpszUsername, string domain, string password,
                int dwLogonType, int dwLogonProvider,
                out IntPtr phToken);

            [DllImport("advapi32.dll", SetLastError = true)]
            public static extern bool ImpersonateLoggedOnUser(IntPtr hToken);

            [DllImport("advapi32.dll", SetLastError = true)]
            public static extern bool RevertToSelf();
            #endregion
        }

        // ---------------------------------------------------------------------------------------------

        /**
         * StartUserProcessInSessionEgw
         */
        static public System.Diagnostics.Process StartUserProcessInSessionEgw(int sessionId, string cmdLine) {
            IntPtr processToken = IntPtr.Zero;

            try {
                if( !SafeNativeMethods.WTSQueryUserToken(sessionId, out processToken) ) {
                    throw new Win32Exception(Marshal.GetLastWin32Error(), "WTSQueryUserToken");
                }

                return StartProcessWithToken(processToken, cmdLine);
            }
            finally {
                SafeNativeMethods.CloseHandle(processToken);
            }
        }

        /**
         * StartProcessWithToken
         */
        public static System.Diagnostics.Process StartProcessWithToken(IntPtr token, string cmdLine) {
            IntPtr environmentBlock = IntPtr.Zero;

            try {
                // Default nil security attribute
                SafeNativeMethods.SECURITY_ATTRIBUTES defSec = new SafeNativeMethods.SECURITY_ATTRIBUTES();
                defSec.nLength = Marshal.SizeOf(defSec);
                defSec.lpSecurityDescriptor = IntPtr.Zero;
                defSec.bInheritHandle = 0;

                // Create an environment block
                if (!SafeNativeMethods.CreateEnvironmentBlock(ref environmentBlock, token, false)) {
                    throw new Win32Exception(Marshal.GetLastWin32Error(), "CreateEnvironmentBlock");
                }

                // Now we can finally get into the business at hand and setup our process info
                SafeNativeMethods.STARTUPINFO startInfo = new SafeNativeMethods.STARTUPINFO();
                startInfo.cb = Marshal.SizeOf(startInfo);
                startInfo.wShowWindow = 0;
                startInfo.lpDesktop = "Winsta0\\Default";   // TBD: Support other desktops?

                SafeNativeMethods.PROCESS_INFORMATION procInfo = new SafeNativeMethods.PROCESS_INFORMATION();
                if (!SafeNativeMethods.CreateProcessAsUser(token, null, cmdLine,
                                    ref defSec, ref defSec, false, SafeNativeMethods.CREATE_UNICODE_ENVIRONMENT,
                                    environmentBlock, null, ref startInfo, out procInfo))
                {
                    int lastError = Marshal.GetLastWin32Error();
                    throw new Win32Exception(lastError, "CreateProcessAsUser");
                }

                // We made it, process is running! Closing our handles to it ensures it doesn't orphan,
                //  then we just use its pid to return a process object
                SafeNativeMethods.CloseHandle(procInfo.hProcess);
                SafeNativeMethods.CloseHandle(procInfo.hThread);

                return System.Diagnostics.Process.GetProcessById((int)procInfo.dwProcessId);
            }
            finally
            {
                SafeNativeMethods.CloseHandle(environmentBlock);
            }
        }
    }
}
