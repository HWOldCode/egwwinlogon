package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * EgroupwareSettings
 * 
 * @author Stefan Werfling
 */
public class EgroupwareSettings extends EgroupwareJson {
    
    /**
     * menuaction
     */
    public static final String EGW_HTTP_GET_SETTING_ACTION = "elogin.elogin_setting_ui.ajax_setting";
    
    /**
     * Types of Setting
     */
    public static final String EGW_SETTING_TYPE_SYSTEM  = "system";
    public static final String EGW_SETTING_TYPE_USER    = "user";
    
    /**
     * SETs
     */
    public static final String EGW_SETTING_SET_REGISTRY         = "registry";
    public static final String EGW_SETTING_SET_RUNTIME_SERVICE  = "runtime_service";
    public static final String EGW_SETTING_SET_RUNTIME_USER     = "runtime_user";
    public static final String EGW_SETTING_SET_FILE             = "file";
    
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgroupwareSettings.class);
    
    /**
     * uid machine id
     */
    protected String _uid = "";
    
    /**
     * type for settings
     */
    protected String _type = "";
    
    /**
     * constructor
     *
     * @param uid
     */
    public EgroupwareSettings(String uid) {
        super();
        this._uid   = uid;
        this._type  = EgroupwareSettings.EGW_SETTING_TYPE_SYSTEM;
        
        this._request_url = this._createJsonMenuaction(
            EgroupwareSettings.EGW_HTTP_GET_SETTING_ACTION);
    }
 
    /**
     * setSettingTypeUser
     */
    public void setSettingTypeUser() {
        this._type = EgroupwareSettings.EGW_SETTING_TYPE_USER;
    }
    
    /**
     * setSettingTypeSystem
     */
    public void setSettingTypeSystem() {
        this._type = EgroupwareSettings.EGW_SETTING_TYPE_SYSTEM;
    }
    
    /**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();

        data.put("json_data", "{\"request\":{\"parameters\":[" +
            "{\"uid\": \"" + this._uid + "\"}" +
            "]}}");

        return data;
    }
    
    /**
     * setRawContent
     * @param content
     */
    @Override
    public void setRawContent(String content) throws EGroupwareExceptionRedirect {
        super.setRawContent(content);

        if( this._json != null ) {
            
        }
    }
    
    /**
     * setSettingsToSystem
     * @return 
     */
    public Boolean setSettingsToSystem() {
        try {
            Advapi32Util.registrySetStringValue(
                WinReg.HKEY_LOCAL_MACHINE, 
                "SOFTWARE\\pGina3",
                "TileImage",
                "C:\\Program Files\\pGina\\tileimage.bmp"
                );
            
            // show all tray icons 
            Advapi32Util.registrySetLongValue(WinReg.HKEY_LOCAL_MACHINE, 
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer", 
                "EnableAutoTray", 
                0);
                    
            String value = Advapi32Util.registryGetStringValue(
                WinReg.HKEY_LOCAL_MACHINE, 
                "SOFTWARE\\pGina3",
                "TileImage");
            
            logger.info("Value: " + value);
        }
        catch( Exception e ) {
            logger.info("Error: " + e.getMessage());
        }
        
        return false;
    }
}
