/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.jegroupware.egroupware.Egroupware;
import com.sun.net.httpserver.HttpExchange;
import egwwinlogon.egroupware.EgroupwareEWorkflowRequest;
import egwwinlogon.http.LogonHttpServerHandler;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EgwWinLogonHttpHandlerEWorkflow
 * @author Stefan Werfling
 */
public class EgwWinLogonHttpHandlerEWorkflow extends LogonHttpServerHandler {
    
    /**
     * constructor
     */
    public EgwWinLogonHttpHandlerEWorkflow() {
        super();
    }
	
	/**
     * _getUrl
     * @return
     */
	@Override
    protected String _getUrl() {
        return "/eworkflow";
    }
	
	/**
	 * handle
	 * @param t 
	 */
	@Override
    public void handle(HttpExchange t) {
		String mreturn = "{}";
		
        try {
            Map<String, String> params = LogonHttpServerHandler.queryToMap(
                    t.getRequestURI().getQuery());

            String path = t.getRequestURI().getPath();

            if( "/eworkflow/".equals(path) ) {
				String triggerId	= null;
				String username		= null;
				String processId	= null;
				
				if( params.containsKey("trigger") ) {
					triggerId = params.get("trigger");
				}
				
				if( params.containsKey("username") ) {
					username = params.get("username");
				}
				
				if( params.containsKey("processid") ) {
					processId = params.get("processid");
				}
				
				if( (triggerId != null) && (username != null) ) {
					EgwWinLogonThread thread = EgwWinLogonThread.getInstance(username);
					
					if( thread != null ) {
						if( thread.getSessionStatus() == 5 ) {
							Egroupware _egw = thread.getEgroupware();
							if( _egw.isLogin() ) {
								try {
									EgroupwareEWorkflowRequest _request = new EgroupwareEWorkflowRequest();
									_request.setTriggerId(triggerId);
									
									if( processId != null ) {
										_request.setProcessId(processId);
									}									
									
									if( t.getRequestMethod().equals("POST") ) {
										Map<String, String> posts = LogonHttpServerHandler.postToMap(t);
										
										if( posts.containsKey("data") ) {
											_request.setJsonData(posts.get("data"));
										}
									}
									
									_egw.request(_request);
									
									mreturn = _request.getReturnJsonData();
								}
								catch( Exception ex ) {
									mreturn = "{\"status\": \"error\", \"msg\": \"" + ex.getMessage() + "\"}";
								}
							}
							else {
								mreturn = "{\"status\": \"error\", \"msg\": \"elogin offline mod, please check connection\"}";
							}
						}
						else {
							//
							mreturn = "{\"status\": \"error\", \"msg\": \"user issnt logged in activ\"}";
						}
					}
					else {
						//
						mreturn = "{\"status\": \"error\", \"msg\": \"user thread not found\"}";
					}
				}
				else {
					//
					mreturn = "{\"status\": \"error\", \"msg\": \"triggerid or username set please\"}";
				}
			}
			
			// -----------------------------------------------------------------
			
			t.sendResponseHeaders(200, mreturn.length());
			OutputStream os = t.getResponseBody();
			os.write(mreturn.getBytes());
			os.close();
			
		} catch( Exception ex ) {
            Logger.getLogger(EgwWinLogonHttpHandlerConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
}