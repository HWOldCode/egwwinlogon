/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareBrowser;
import java.io.File;

/**
 * EgroupwareELoginBrowser
 * @author Stefan Werfling
 */
public class EgroupwareELoginBrowser extends EgroupwareBrowser {
    
    /**
     * open
     *
     * @param egw
     * @throws Exception
     */
    static public void open(Egroupware egw) throws Exception {
        EgroupwareELoginBrowser.open(egw, "");
    }

    /**
     * open
     * 
     * @param egw
     * @param browser
     * @throws Exception 
     */
    static public void open(Egroupware egw, String browser) throws Exception {
        EgroupwareELoginBrowser.open(egw, null, "");
    }

    /**
     * open
     * 
     * @param egw
     * @param menuaction
     * @param browser
     * @throws Exception 
     */
    static public void open(Egroupware egw, String menuaction, String browser) throws Exception {
        try {
            if( egw.isLogin() ) {
                String url = egw.createBrowserLink(menuaction);
                
                String[] _browsers = {
                    "C:\\Program Files (x86)\\Chromium\\Chrome\\chrome.exe",
                    "C:\\Program Files (x86)\\Comodo\\Dragon\\dragon.exe",
                    "C:\\Program Files (x86)\\Comodo\\Chromodo\\chromodo.exe",
                    "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"
                    };
                
				for (String browserApp : _browsers) {
					File f = new File(browserApp);
					
					if( f.exists() ) {
						Process proc = Runtime.getRuntime().exec(
								"\"" + browserApp + "\" --app=" + url);
						return;
					}
				}
                
                EgroupwareBrowser.open(egw, menuaction, browser);
            }
            else {
                throw new Exception("please login in egroupware");
            }
        }
        catch( Exception exp ) {
            throw new Exception("can not open system default browser");
        }
    }
}