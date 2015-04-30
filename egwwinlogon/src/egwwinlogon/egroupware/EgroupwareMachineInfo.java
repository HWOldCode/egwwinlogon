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
 * EgroupwareMachineInfo
 * @author Stefan Werfling
 */
public class EgroupwareMachineInfo extends EgroupwareJson {

    /**
     * menuaction
     */
    public static final String EGW_HTTP_GET_MI_ACTION = "elogin.elogin_machine_ui.ajax_machine_info";

    /**
     * UID
     */
    protected String _uid = "";

    /**
     * Name
     */
    protected String _name = "";

    /**
     * constructor
     * @param uid
     */
    public EgroupwareMachineInfo(String uid) {
        super();

        this._uid = uid;

        this._request_url = this._createJsonMenuaction(
            EgroupwareMachineInfo.EGW_HTTP_GET_MI_ACTION);
    }

    /**
     * setMachineName
     * @param name
     */
    public void setMachineName(String name) {
        this._name = name;
    }

    /**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();

        data.put("json_data", "{\"request\":{\"parameters\":[" +
            "{\"uid\": \"" + this._uid + "\", \"name\": \"" + this._name + "\"}" +
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
