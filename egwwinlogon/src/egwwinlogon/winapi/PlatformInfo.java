/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PlatformInfo
 * @author Stefan Werfling
 */
public class PlatformInfo {

	/**
	 * getInfoList
	 * @return 
	 */
    static public Map<String, String> getInfoList() {
        Map<String, String> list = new HashMap<String, String>();

        list.put("availableProcessors",
            Integer.toString(Runtime.getRuntime().availableProcessors()));

        list.put("freeMemory",
            Long.toString(Runtime.getRuntime().freeMemory()));

        long maxMemory = Runtime.getRuntime().maxMemory();

        list.put("maxMemory",
            (maxMemory == Long.MAX_VALUE ? "no limit" : Long.toString(maxMemory)));

        list.put("totalMemory",
            Long.toString(Runtime.getRuntime().totalMemory()));

        try {
            list.put("biosSerialNumber", Wmic.getBiosSerialNumber());
        } catch (IOException ex) {
            Logger.getLogger(
                PlatformInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }
}
