using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace EGroupware
{
    class WTS {
        [DllImport("wtsapi32.dll")]
        static extern IntPtr WTSOpenServer([MarshalAs(UnmanagedType.LPStr)] String pServerName);

        [DllImport("wtsapi32.dll")]
        static extern void WTSCloseServer(IntPtr hServer);

        [DllImport("wtsapi32.dll")]
        static extern Int32 WTSEnumerateSessions(
            IntPtr hServer,
            [MarshalAs(UnmanagedType.U4)] Int32 Reserved,
            [MarshalAs(UnmanagedType.U4)] Int32 Version,
            ref IntPtr ppSessionInfo,
            [MarshalAs(UnmanagedType.U4)] ref Int32 pCount);

        [DllImport("wtsapi32.dll")]
        static extern void WTSFreeMemory(IntPtr pMemory);

        [DllImport("Wtsapi32.dll")]
        static extern bool WTSQuerySessionInformation(
            System.IntPtr hServer, int sessionId, WTS_INFO_CLASS wtsInfoClass, out System.IntPtr ppBuffer, out uint pBytesReturned);

        [DllImport("wtsapi32.dll", SetLastError = true)]
        static extern bool WTSDisconnectSession(IntPtr hServer, int sessionId, bool bWait);

        [StructLayout(LayoutKind.Sequential)]
        private struct WTS_SESSION_INFO {
          public Int32 SessionID;

          [MarshalAs(UnmanagedType.LPStr)]
          public String pWinStationName;

          public WTS_CONNECTSTATE_CLASS State;
        }

        public enum WTS_INFO_CLASS {
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

        public enum WTS_CONNECTSTATE_CLASS {
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

        public static IntPtr OpenServer(String Name)
        {
          IntPtr server = WTSOpenServer(Name);
          return server;
        }

        public static void CloseServer(IntPtr ServerHandle)
        {
          WTSCloseServer(ServerHandle);
        }

        public static void ListUsers(String ServerName)
        {
          IntPtr serverHandle = IntPtr.Zero;
          List<String> resultList = new List<string>();
          serverHandle = OpenServer(ServerName);

          try
          {
            IntPtr SessionInfoPtr = IntPtr.Zero;
            IntPtr userPtr = IntPtr.Zero;
            IntPtr domainPtr = IntPtr.Zero;
            Int32 sessionCount = 0;
            Int32 retVal = WTSEnumerateSessions(serverHandle, 0, 1, ref SessionInfoPtr, ref sessionCount);
            Int32 dataSize = Marshal.SizeOf(typeof(WTS_SESSION_INFO));
            Int32 currentSession = (int)SessionInfoPtr;
            uint bytes = 0;

            if (retVal != 0)
            {
              for (int i = 0; i < sessionCount; i++)
              {
                WTS_SESSION_INFO si = (WTS_SESSION_INFO)Marshal.PtrToStructure((System.IntPtr)currentSession, typeof(WTS_SESSION_INFO));
                currentSession += dataSize;

                WTSQuerySessionInformation(serverHandle, si.SessionID, WTS_INFO_CLASS.WTSUserName, out userPtr, out bytes);
                WTSQuerySessionInformation(serverHandle, si.SessionID, WTS_INFO_CLASS.WTSDomainName, out domainPtr, out bytes);

                Console.WriteLine("Domain and User: " + Marshal.PtrToStringAnsi(domainPtr) + "\\" + Marshal.PtrToStringAnsi(userPtr));

                WTSFreeMemory(userPtr); 
                WTSFreeMemory(domainPtr);
              }

              WTSFreeMemory(SessionInfoPtr);
            }
          }
          finally
          {
            CloseServer(serverHandle);
          }
        }

        public static void closeLocalUserSession(String username) {
            IntPtr serverHandle = IntPtr.Zero;
            List<String> resultList = new List<string>();
            serverHandle = OpenServer("localhost");

            try
            {
                IntPtr SessionInfoPtr = IntPtr.Zero;
                IntPtr userPtr = IntPtr.Zero;
                IntPtr domainPtr = IntPtr.Zero;
                Int32 sessionCount = 0;
                Int32 retVal = WTSEnumerateSessions(serverHandle, 0, 1, ref SessionInfoPtr, ref sessionCount);
                Int32 dataSize = Marshal.SizeOf(typeof(WTS_SESSION_INFO));
                Int32 currentSession = (int)SessionInfoPtr;
                uint bytes = 0;

                if( retVal != 0 )
                {
                    for( int i = 0; i < sessionCount; i++ ) {
                        WTS_SESSION_INFO si = (WTS_SESSION_INFO)Marshal.PtrToStructure((System.IntPtr)currentSession, typeof(WTS_SESSION_INFO));
                        currentSession += dataSize;

                        WTSQuerySessionInformation(serverHandle, si.SessionID, WTS_INFO_CLASS.WTSUserName, out userPtr, out bytes);
                        WTSQuerySessionInformation(serverHandle, si.SessionID, WTS_INFO_CLASS.WTSDomainName, out domainPtr, out bytes);

                        if( username == Marshal.PtrToStringAnsi(userPtr) ) {
                            //Console.WriteLine("Domain and User: " + Marshal.PtrToStringAnsi(domainPtr) + "\\" + Marshal.PtrToStringAnsi(userPtr));
                            WTSDisconnectSession(serverHandle, si.SessionID, false);

                            WTSFreeMemory(userPtr); 
                            WTSFreeMemory(domainPtr);
                            break;
                        }

                        WTSFreeMemory(userPtr); 
                        WTSFreeMemory(domainPtr);
                    }

                    WTSFreeMemory(SessionInfoPtr);
                }
            }
            finally   {
                CloseServer(serverHandle);
            }
        }
    }
}
