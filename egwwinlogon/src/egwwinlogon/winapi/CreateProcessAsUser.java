/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;

/**
 * CreateProcessAsUser
 * 
 * @author Stefan Werfling
 */
public class CreateProcessAsUser {
    
    public static final String CMD = "c:\\windows\\system32\\cmd.exe";
    
    /**
     * createProcess
     * 
     * @param cmdline
     * @param directoryLocation
     * @param username
     * @param password
     * @return PROCESS_INFORMATION
     * @throws Exception 
     */
    static public PROCESS_INFORMATION createProcess(String cmdline, 
        String directoryLocation, String username, String password) throws Exception 
    {
        WString nullW                           = null;
        PROCESS_INFORMATION processInformation  = new PROCESS_INFORMATION();
        STARTUPINFO startupInfo                 = new STARTUPINFO();
        
        boolean result = AdvApi32.INSTANCE.CreateProcessWithLogonW
            (new WString(username),                             // user
            nullW,                                              // domain , null if local
            new WString(password),                              // password
            AdvApi32.LOGON_WITH_PROFILE,                    // dwLogonFlags
            nullW,                                              // lpApplicationName
            new WString(cmdline),                               // command line
            AdvApi32.CREATE_NO_WINDOW,                      // dwCreationFlags
            null,                                               // lpEnvironment
            new WString(directoryLocation),                     // directory
            startupInfo,
            processInformation);

        if( !result ) {
          int error = Kernel32.INSTANCE.GetLastError();
          
          throw new Exception(Kernel32Util.formatMessageFromLastErrorCode(error));
        }
        
        return processInformation;
    }
}
