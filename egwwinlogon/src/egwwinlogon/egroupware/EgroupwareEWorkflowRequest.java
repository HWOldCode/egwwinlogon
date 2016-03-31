/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import egwwinlogon.service.EgroupwarePGina;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * EgroupwareEWorkflowRequest
 * @author Stefan Werfling
 */
public class EgroupwareEWorkflowRequest extends EgroupwareJson {
	
	/**
     * menuaction
     */
    public static final String EGW_HTTP_GET_EWR_ACTION = "elogin.elogin_ui.ajax_eworkflow_request"; // &XDEBUG_SESSION_START=netbeans-xdebug
	
	/**
	 * trigger id
	 */
	protected String _triggerId = "";
	
	/**
	 * process id
	 */
	protected String _processId = null;
	
	/**
	 * json data
	 */
	protected String _jsonData = "";
	
	/**
	 * return Json Data
	 */
	protected String _returnJsonData = "{}";
	
	/**
	 * constructor
	 */
	public EgroupwareEWorkflowRequest() {
		super();
		
		this._request_url = this._createJsonMenuaction(
            EgroupwareEWorkflowRequest.EGW_HTTP_GET_EWR_ACTION);
	}
	
	/**
	 * setTriggerId
	 * @param id 
	 */
	public void setTriggerId(String id) {
		this._triggerId = id;
	}
	
	/**
	 * setProcessId
	 * @param id 
	 */
	public void setProcessId(String id) {
		this._processId = id;
	}
	
	/**
	 * setJsonData
	 * @param data 
	 */
	public void setJsonData(String data) {
		this._jsonData = data;
	}
	
	/**
	 * getReturnJsonData
	 * @return 
	 */
	public String getReturnJsonData() {
		return this._returnJsonData;
	}
	
	/**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();
		
		String parameters = "{\"uid\": \"" + 
			EgroupwarePGina.getSysFingerprint() + "\", \"trigger\": \"" + 
			this._triggerId + "\", \"data\": \"" + this._jsonData + "\"";
		
		if( this._processId != null ) {
			parameters += ", \"processid\": \"" + this._processId + "\"";
		}
		
		parameters += "}";
		
        data.put("json_data", "{\"request\":{\"parameters\":[" +
            parameters + "]}}");

        return data;
    }

    /**
     * setRawContent
     * @param content
     */
    @Override
    public void setRawContent(String content) throws EGroupwareExceptionRedirect {
        super.setRawContent(content);
    }
	
	/**
	 * _egroupwareData
	 * @param data
	 * @param responseIndex 
	 */
	@Override
	protected void _egroupwareData(LinkedHashMap data, int responseIndex) {
		this._returnJsonData = new JSONObject(data).toJSONString();
	}
}
