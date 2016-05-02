/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.jegroupware.egroupware.Egroupware;
import com.sun.net.httpserver.HttpExchange;
import egwwinlogon.http.LogonHttpServerHandler;
import egwwinlogon.winapi.PlatformInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EgwWinLogonHttpHandlerConfig
 * @author Stefan Werfling
 */
public class EgwWinLogonHttpHandlerConfig extends LogonHttpServerHandler {

    /**
     * machine id
     */
    protected String _machine_id = "";
    
    /**
     * constructor
     * @param machineid 
     */
    public EgwWinLogonHttpHandlerConfig(String machineid) {
        super();
        this._machine_id = machineid;
    }
    
    /**
     * _getUrl
     * @return
     */
    protected String _getUrl() {
        return "/config";
    }

    @Override
    public void handle(HttpExchange t) {
        try {
            Map<String, String> params = LogonHttpServerHandler.queryToMap(
                    t.getRequestURI().getQuery());

            String path = t.getRequestURI().getPath();

            if( "/config/".equals(path) ) {
                if( params.containsKey("json") ) {
                    String value = params.get("json");

                    if( "info".equals(value) ) {
                        String response = "";

                        Map<String, String> info = PlatformInfo.getInfoList();

                        for( Map.Entry<String, String> entry : info.entrySet()) {
                            response += entry.getKey() + ":" + entry.getValue() + ";";
                        }

                        // add machine id
                        response += "machine_id:" + this._machine_id + ";";
                        
                        t.sendResponseHeaders(200, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();

                        return;
                    }
                }
				else if( params.containsKey("relogin") ) {
					String value = params.get("relogin");
					
					if( "1".equals(value) ) {
						if( params.containsKey("username") && params.containsKey("password") ) {
							String username = params.get("username");
							String password = params.get("password");
							
							EgwWinLogonThread _thread = EgwWinLogonThread.getInstance(username);
							
							if( (_thread.getSessionStatus() == 5) || (_thread.getSessionStatus() == 8) ) {
								Egroupware _egw = _thread.getEgroupware();
								
								if( _egw != null ) {
									if( !_egw.isLogin() ) {
										if( _egw.getConfig().getUser().equals(username) ) {
											_egw.getConfig().setPassword(password);
											
											String rMsg = "OK";
											
											try {
												_egw.login();
											}
											catch( Exception e ){
												rMsg = e.getMessage();
											}
											
											t.sendResponseHeaders(200, rMsg.length());
											OutputStream os = t.getResponseBody();
											os.write(rMsg.getBytes());
											os.close();

											return;
										}
									}
								}
							}
						}
					}
				}
            }

            InputStream file = null;

            try {
                file = LogonHttpServerHandler.getFile("egwwinlogon/service/webdir" + path);
            }
            catch( Exception ex ) {
            }

            if( file != null ) {
                t.sendResponseHeaders(200, file.available());
                OutputStream os = t.getResponseBody();

                byte[] buffer = new byte[1024]; // Adjust if you want
                int bytesRead;

                while ((bytesRead = file.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                os.close();
            }
            else {

                String response = "This is the response, config";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(EgwWinLogonHttpHandlerConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
