/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.sun.net.httpserver.HttpExchange;
import egwwinlogon.egroupware.EgroupwareCommand;
import egwwinlogon.http.LogonHttpServerHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONValue;

/**
 * EgwWinLogonHttpHandlerCommand
 * @author Stefan Werfling
 */
public class EgwWinLogonHttpHandlerCommand extends LogonHttpServerHandler {
	
	/**
     * _getUrl
     * @return
     */
    protected String _getUrl() {
        return "/command";
    }
	
	/**
	 * handle
	 * 
	 * @param t
	 * @throws IOException 
	 */
	@Override
    public void handle(HttpExchange t) throws IOException {
		String msg = "error";
		
		try {
            Map<String, String> params = LogonHttpServerHandler.queryToMap(
                    t.getRequestURI().getQuery());

            String path = t.getRequestURI().getPath();

            if( "/command/".equals(path) ) {
				int sessionId = 0;
				
				if( params.containsKey("username") ) {
					String username = params.get("username");
					
					EgwWinLogonThread wlt = EgwWinLogonThread.getInstance(username);
					
					if( wlt != null ) {
						sessionId = wlt.getSessionId();
					}
				}
				
				if( sessionId != 0 ) {
					if( params.containsKey("contextmenu") ) {
						LinkedList contextCmds = new LinkedList();
						List<String> cmdnames = new ArrayList<String>();
						
						for( int n=0; n<EgroupwareCommand.instance.getCmdCount(); n++ ) {
							LinkedHashMap tcmd = EgroupwareCommand.instance.getCmd(n);
							
							String cmdname = (String) tcmd.get("name");
							
							if( cmdnames.contains(cmdname) ) {
								continue;
							}
							else {
								cmdnames.add(cmdname);
							}
							
							if( !tcmd.containsKey("options") ) {
								continue;
							}
							
							Object toptions = tcmd.get("options");
							
							if( !(toptions instanceof LinkedHashMap) ) {
								continue;
							}
							
							LinkedHashMap options = (LinkedHashMap) toptions;
							
							if( options != null ) {
								if( options.containsKey("trayer_show_contextmenu") ) {
									String tsc = (String) options.get("trayer_show_contextmenu");
									
									if( "1".equals(tsc) ) {
										LinkedHashMap newcmd = new LinkedHashMap();
							
										newcmd.put("name", cmdname);
										newcmd.put("catname", (String) tcmd.get("catname"));

										contextCmds.add(newcmd);
									}
								}
							}
						}
						
						msg = JSONValue.toJSONString(contextCmds);
					}
					else if( params.containsKey("exec") ) {
						String execname = params.get("exec");

						EgroupwareCommand.instance.executeName(
							sessionId, 
							execname);
						
						msg = "exec: " + execname;
					}
					else {
						msg = "error: unknow http parameter";
					}
				}
				else {
					msg = "error: unknow sessionid by username";
				}
            }
        }
        catch( Exception ex ) {
            Logger.getLogger(
                EgwWinLogonHttpHandlerLogger.class.getName()
                ).log(Level.SEVERE, null, ex);
			
			msg = msg + ": " + ex.getMessage();
        }
		
		t.sendResponseHeaders(200, msg.length());
		OutputStream os = t.getResponseBody();
		os.write(msg.getBytes());
		os.close();
	}
}
