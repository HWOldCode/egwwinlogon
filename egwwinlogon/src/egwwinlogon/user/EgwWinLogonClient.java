/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.jegroupware.egroupware.EgroupwareSession;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import egwwinlogon.http.LogonHttpClient;
import egwwinlogon.service.EgwWinLogonUltis;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

/**
 * EgwWinLogonClient
 * 
 * @author Stefan Werfling
 */
public class EgwWinLogonClient  {

    // BASE URL
    public static final String URL = "http://localhost:8108/";

    public static final String REQUEST_SESSION  = "session";
    public static final String REQUEST_CONFIG   = "config";
	public static final String REQUEST_COMMAND	= "command";

    /**
     * LogonHttpClient
     */
    protected LogonHttpClient _client = null;

    /**
     * constructor
     */
    public EgwWinLogonClient() {
        this._client = new LogonHttpClient();
    }

    /**
     * getEgroupwareInstance
     * @param user
     * @return 
     */
    public Egroupware getEgroupwareInstance(String user) {
        return this.getEgroupwareInstance(user, null);
    }
    
    /**
     * getMachineId
     * @param url
     * @return 
     */
    public String getMachineId(String url) {
        try {
            if( url == null ) {
                url = EgwWinLogonClient.URL;
            }
        
            String request = url + EgwWinLogonClient.REQUEST_CONFIG + "/?json=info";
            String buffer = this._client.sendGET(request);
            
            String[] params = buffer.split(";");
            
            for( String param: params ) {
                String[] values = param.split(":");

                if( values.length > 1 ) {
                    if( "machine_id".equals(values[0]) ) {
                        return values[1];
                    }
                }
            }
        }
        catch( Exception e ) {
        } 
        
        return null;
    }
    
    /**
     * getEgroupwareInstance
     * @param user
     * @param url
     * @return
     */
    public Egroupware getEgroupwareInstance(String user, String url) {
        try {
            if( url == null ) {
                url = EgwWinLogonClient.URL;
            }
            
            String request = url + EgwWinLogonClient.REQUEST_SESSION + "?user=" + user;
            String buffer = this._client.sendGET(request);
            
            if( buffer != "error" ) {
                EgroupwareConfig config = null;
                EgroupwareSession session = null;

                String[] params = buffer.split(";");

                for( String param: params ) {
                    String[] values = param.split(":");

                    if( values.length > 1 ) {
                        if( "config".equals(values[0]) ) {
                            config = EgroupwareConfig.fromSerializableString(
                                new String(Base64.decodeBase64(values[1])));
                        }
                        else if( "session".equals(values[0]) ) {
                            session = EgroupwareSession.fromSerializableString(
                                new String(Base64.decodeBase64(values[1])));
                        }
                    }
                }

                if( (config != null) && (session != null) ) {
                    Egroupware egw = Egroupware.getInstance(config);
                    egw.setSession(session);

                    return egw;
                }
            }
        }
        catch( Exception ex ) {
            System.out.println(ex.getMessage());
        }

        return null;
    }
	
	/**
	 * getCommands
	 * 
	 * @param user
	 * @param url
	 * @return 
	 */
	public LinkedList getCommands(String user, String url) {
		LinkedList list = new LinkedList();
		
		try {
            if( url == null ) {
                url = EgwWinLogonClient.URL;
            }
        
            String request = url + EgwWinLogonClient.REQUEST_COMMAND + 
				"/?contextmenu=1&username=" + EgwWinLogonUltis.encodeURIComponent(user);
			
            String buffer = this._client.sendGET(request);
			
			
			JSONParser parser = new JSONParser();
			ContainerFactory containerFactory = new ContainerFactory(){
					public List creatArrayContainer() {
						return new LinkedList();
					}

					public Map createObjectContainer() {
						return new LinkedHashMap();
					}
				};
			
			LinkedList response = (LinkedList)parser.parse(buffer.trim(), containerFactory);
			
			if( response != null ) {
				for( int i=0; i<response.size(); i++ ) {
					LinkedHashMap tmp = (LinkedHashMap) response.get(i);
					
					list.add(tmp);
				}
			}
		}
        catch( Exception e ) {
        } 
		
		return list;
	}
	
	/**
	 * 
	 * @param user
	 * @param cmdname
	 * @param url
	 * @return 
	 */
	public String execCommand(String user, String cmdname, String url) {
		try {
            if( url == null ) {
                url = EgwWinLogonClient.URL;
            }
        
            String request = url + EgwWinLogonClient.REQUEST_COMMAND + 
				"/?exec=" + EgwWinLogonUltis.encodeURIComponent(cmdname) + 
				"&username=" + EgwWinLogonUltis.encodeURIComponent(user);
			
            String buffer = this._client.sendGET(request);
			
			return buffer;
		}
        catch( Exception e ) {
        }
		
		return "";
	}
}
