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
 * @author Stefan Werfling
 */
public class EgroupwareCommand extends EgroupwareJson {

    /**
     * menuaction
     */
    public static final String EGW_HTTP_GET_CMD_ACTION = "elogin.elogin_ui.ajax_cmd";

    public static final String EGW_CMD_LOGIN    = "login";
    public static final String EGW_CMD_PING     = "ping";
    public static final String EGW_CMD_LOGOUT   = "logout";

    /**
     * command
     */
    protected String _cmd = "";

    /**
     * Message
     */
    protected String _msg = "";

    /**
     * constructor
     *
     * @param cmd
     * @param msg
     */
    public EgroupwareCommand(String cmd, String msg) {
        super();

        this._cmd = cmd;
        this._msg = msg;

        this._request_url = this._createJsonMenuaction(
            EgroupwareCommand.EGW_HTTP_GET_CMD_ACTION);
    }

    /**
     * EgroupwareCommand
     */
    public EgroupwareCommand() {
        super();

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
            "{\"cmd\": \"" + this._cmd + "\", \"msg\": \"" + this._msg + "\"}" +
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
