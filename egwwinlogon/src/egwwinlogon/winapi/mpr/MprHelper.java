/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi.mpr;

import com.sun.jna.WString;

/**
 * MprHelper
 * 
 * @author Stefan Werfling
 */
public class MprHelper {
    
    public static void mountW(String remotepath, String username, String password) {
        NETRESOURCEW lpNetResource;
        
        int dwFlags;
        int errorCode;

        lpNetResource = new NETRESOURCEW();
        lpNetResource.dwScope = 0;
        lpNetResource.dwType = NETRESOURCE.RESOURCETYPE_DISK;
        lpNetResource.dwDisplayType = NETRESOURCE.RESOURCEDISPLAYTYPE_SHARE;
        lpNetResource.dwUsage = NETRESOURCE.RESOURCEUSAGE_CONNECTABLE;
        lpNetResource.lpLocalName = null;
        lpNetResource.lpRemoteName = new WString(remotepath);
        lpNetResource.lpComment = null;
        lpNetResource.lpProvider = null;
        
        WString lpPassword = new WString(password);
        WString lpUserName = new WString(username);
        
        dwFlags = Mpr.CONNECT_TEMPORARY;
        
        System.out.println("Mounting Windows Share [" + lpNetResource.lpRemoteName + "] [" + lpUserName + "] ...");

        errorCode = Mpr.INSTANCE.WNetAddConnection2W(
            lpNetResource, 
            lpPassword, 
            lpUserName, 
            dwFlags
            );

        System.out.println("Mounting Windows Share: " + errorCode);
    }
}
