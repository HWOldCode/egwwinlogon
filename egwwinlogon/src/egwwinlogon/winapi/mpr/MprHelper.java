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
    
    public static void mountA(String remotepath, String username, String password) {
        NETRESOURCEW lpNetResource;
        
        int dwFlags;
        int errorCode;

        lpNetResource = new NETRESOURCEW();
        lpNetResource.dwScope		= 0;
        lpNetResource.dwType		= NETRESOURCE.RESOURCETYPE_DISK;
        lpNetResource.dwDisplayType = NETRESOURCE.RESOURCEDISPLAYTYPE_SHARE;
        lpNetResource.dwUsage		= NETRESOURCE.RESOURCEUSAGE_CONNECTABLE;
        lpNetResource.lpLocalName	= new WString("G:");
        lpNetResource.lpRemoteName	= new WString(remotepath);
        lpNetResource.lpComment		= null;
        lpNetResource.lpProvider	= null;
        
        String lpPassword = password;
        String lpUserName = username;
        
        dwFlags = Mpr.CONNECT_TEMPORARY;
        
        System.out.println("Mounting Windows Share [" + lpNetResource.lpRemoteName + "] [" + lpUserName + "] ...");

        errorCode = Mpr.INSTANCE.WNetAddConnection2W(
            lpNetResource, 
            new WString(lpPassword), 
            new WString(lpUserName), 
            dwFlags
            );

        System.out.println("Mounting Windows Share: " + errorCode);
    }
    
    
    /**
     * mountW
     * 
     * @param remotepath
     * @param localname
     * @param username
     * @param password
     * @return 
     */
    public static int mountW(String remotepath, String localname, String username, String password) {
        NETRESOURCEW lpNetResource;
        
        int dwFlags;

        lpNetResource               = new NETRESOURCEW();
        lpNetResource.dwScope       = 0;
        lpNetResource.dwType        = NETRESOURCE.RESOURCETYPE_ANY;
        lpNetResource.dwDisplayType = NETRESOURCE.RESOURCEDISPLAYTYPE_SHARE;
        lpNetResource.dwUsage       = NETRESOURCE.RESOURCEUSAGE_CONNECTABLE;
        lpNetResource.lpLocalName   = new WString(localname);
        lpNetResource.lpRemoteName  = new WString(remotepath);
        lpNetResource.lpComment     = null;
        lpNetResource.lpProvider    = null;//new WString("Microsoft Windows-Netzwerk");
        
        WString lpPassword = new WString(password);
        WString lpUserName = new WString(username);
        
        dwFlags = Mpr.CONNECT_INTERACTIVE;
        
        //System.out.println("Mounting Windows Share [" + lpNetResource.lpRemoteName + "] [" + lpUserName + "] ...");

        return Mpr.INSTANCE.WNetAddConnection2W(
            lpNetResource, 
            lpPassword, 
            lpUserName, 
            dwFlags
            );

        //System.out.println("Mounting Windows Share: " + errorCode);
    }
}
