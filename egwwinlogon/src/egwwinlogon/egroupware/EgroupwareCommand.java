/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import java.util.HashMap;
import java.util.Map;

/**
 * EgroupwareCommand
 * 
 * @author Stefan Werfling
 */
public class EgroupwareCommand extends EgroupwareJson {

    /**
     * menuaction
     */
    public static final String EGW_HTTP_GET_CMD_ACTION = "elogin.elogin_ui.ajax_cmd";

    /**
     * Types of cmd
     */
    public static final String EGW_CMD_TYPE_LOGIN    = "login";
    public static final String EGW_CMD_TYPE_PING     = "ping";
    public static final String EGW_CMD_TYPE_LOGOUT   = "logout";

    /**
     * uid of machine
     */
    protected String _uid = "";
    
    /**
     * command type
     */
    protected String _cmdtype = "";

    /**
     * constructor
     *
     * @param uid
     * @param cmdtype
     */
    public EgroupwareCommand(String uid, String cmdtype) {
        super();

        this._uid       = uid;
        this._cmdtype   = cmdtype;
        this._request_url = this._createJsonMenuaction(
            EgroupwareCommand.EGW_HTTP_GET_CMD_ACTION);
    }

    /**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();

        data.put("json_data", "{\"request\":{\"parameters\":[" +
            "{\"uid\": \"" + this._uid + "\", \"cmdtype\": \"" + this._cmdtype + "\"}" +
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
}
