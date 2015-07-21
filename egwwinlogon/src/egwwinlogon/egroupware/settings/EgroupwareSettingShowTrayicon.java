/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware.settings;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import java.util.Map;

/**
 * EgroupwareSettingShowTrayicon
 * 
 * @author Stefan Werfling
 */
public class EgroupwareSettingShowTrayicon implements IEgroupwareSetting {
    
    /**
     * name
     */
    protected String _name = "showTrayicon";
   
    /**
     * description
     */
    protected String _description = "Show all Trayicon activ in windows.";
    
    /**
     * static
     */
    static {
        EgroupwareSettingDictionary.register(new EgroupwareSettingShowTrayicon());
    }

    /**
     * getClassname
     * @return 
     */
    @Override
    public String getClassname() {
        return this.getClass().getName();
    }

    /**
     * getName
     * @return 
     */
    @Override
    public String getName() {
        return this._name;
    }

    /**
     * getDescription
     * @return 
     */
    @Override
    public String getDescription() {
        return this._description;
    }

    /**
     * getNeedSettingFields
     * @return 
     */
    @Override
    public Map<String, String> getNeedSettingFields() {
        return null;
    }

    /**
     * setSetting
     * @return 
     */
    @Override
    public Boolean setSetting() {
        // show all tray icons
        Advapi32Util.registrySetLongValue(WinReg.HKEY_LOCAL_MACHINE, 
            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer", 
            "EnableAutoTray", 
            0);
        
        return true;
    }
}