/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import egwwinlogon.service.EgroupwarePGina;
import egwwinlogon.service.EgwWinLogonConst;
import egwwinlogon.service.EgwWinLogonUltis;
import egwwinlogon.winapi.PInvokes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * EgroupwareNetShares
 * @author Stefan Werfling
 */
public class EgroupwareNetShares extends EgroupwareCacheList {
	
	/**
     * menuaction
     */
    public static final String EGW_HTTP_GET_NS_ACTION = "elogin.elogin_usershares_ui.ajax_ns_list";
	
	/**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgroupwareNetShares.class);
	
	/**
     * self instance
     */
    protected static EgroupwareNetShares _instance = null;
	
	/**
	 * getInstance
	 * @return 
	 */
	public static EgroupwareNetShares getInstance() {
		if( EgroupwareNetShares._instance == null ) {
			try {
				EgroupwareNetShares._instance = (EgroupwareNetShares) EgroupwareNetShares.loadByFile(
					EgroupwarePGina.getAppDirCache() + EgwWinLogonConst.CACHE_FILE_NETSHARE);
			}
			catch( Exception ex) {	
			}
			
			if( EgroupwareNetShares._instance == null ) {
				EgroupwareNetShares._instance = new EgroupwareNetShares();
			}
		}
		
		return EgroupwareNetShares._instance;
	}
	
	// -------------------------------------------------------------------------
	
	/**
     * net shares
     */
    protected LinkedHashMap _netShares = null;
	
	/**
     * constructor
     *
     */
    public EgroupwareNetShares() {
        super();

        this._request_url = this._createJsonMenuaction(
            EgroupwareNetShares.EGW_HTTP_GET_NS_ACTION);
    }
	
	/**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();

        data.put("json_data", "{\"request\":{\"parameters\":[" +
            "{\"uid\": \"" + EgroupwarePGina.getSysFingerprint() + "\"}" +
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
            LinkedList respsone = (LinkedList) this._json.get("response");

            if( respsone != null ) {
                for( int i=0; i<respsone.size(); i++ ) {
                    LinkedHashMap rcontent = (LinkedHashMap) respsone.get(i);
					
					String type = (String) rcontent.get("type");

                    if( type.compareTo("data") == 0 ) {
                        LinkedHashMap data = (LinkedHashMap) rcontent.get("data");
                        
                        this._netShares = (LinkedHashMap) data.get("ns");
					}
				}
			}
		}
	}
	
	/**
	 * mountAllBySession
	 * @param session 
	 */
	public void mountAllBySession(int session) {
		String username = EgroupwarePGina.getUsername(session);
		
		if( this._netShares.containsKey(username) ) {
			LinkedList mounts = (LinkedList) this._netShares.get(username);
			
			for( int i=0; i<mounts.size(); i++ ) {
				LinkedHashMap mount = (LinkedHashMap) mounts.get(i);
				
				String unc = "\\\\" + mount.get("server") + "\\" + mount.get("sharename");
				String drive = mount.get("mountname") + ":";
				String tusername = (String) mount.get("username");
				String password = (String) mount.get("password");
				
				try {
					PInvokes.mapDriveInSession(
						session,
						unc,
						drive,
						tusername,
						password
					);
				} 
				catch (Exception ex) {
					logger.error(null, ex);
				}
			}
		}
	}
	
	/**
	 * unmountAllBySession
	 * @param session 
	 */
	public void unmountAllBySession(int session) {
		String username = EgroupwarePGina.getUsername(session);
		
		if( this._netShares.containsKey(username) ) {
			LinkedList mounts = (LinkedList) this._netShares.get(username);
			
			for( int i=0; i<mounts.size(); i++ ) {
				LinkedHashMap mount = (LinkedHashMap) mounts.get(i);
				
				String drive = mount.get("mountname") + ":";
				
				try {
					PInvokes.unmapDriveInSession(
						session,
						drive
					);
				} 
				catch (Exception ex) {
					logger.error(null, ex);
				}
			}
		}
	}
	
	/**
     * getNsCount
     * @return 
     */
    public int getNsCount() {
        if( this._netShares != null ) {
            return this._netShares.size();
        }
        
        return 0;
    }
	
	/**
	 * getNsUsers
	 * @return 
	 */
	public String[] getNsUsers() {
		LinkedList<String> users = new LinkedList();
		
		Set _set = this._netShares.entrySet();
		Iterator _iterator = _set.iterator();
		
		
		while( _iterator.hasNext() ) {
            Map.Entry _me = (Map.Entry)_iterator.next();
			
			users.add((String)_me.getKey());
		}
			
		return (String[]) users.toArray(new String[users.size()]);
	}
	
	/**
     * getNs
     * @param username
     * @return 
     */
    public LinkedList getNs(String username) {
        if( this._netShares != null ) {
            return (LinkedList) this._netShares.get(username);
        }
        
        return null;
    }
	
	/**
	 * _toSerializableString
	 * @return
	 */
	@Override
	protected String _toSerializableString() {
        JSONObject serializable = new JSONObject();
        JSONObject jsonList = new JSONObject();
        
        if( this.getNsCount() == 0 ) {
            return null;
        }
        
		String[] users = this.getNsUsers();
		
        for( int i=0; i<users.length; i++ ) {
			LinkedList mounts = this.getNs(users[i]);
			
			if( mounts != null ) {
				JSONArray shares = new JSONArray();
				
				for( int e=0; e<mounts.size(); e++ ) {
					LinkedHashMap mount = (LinkedHashMap) mounts.get(e);
					JSONObject share = new JSONObject();
					
					Set<String> keys = mount.keySet();
                
					for( String k :keys ) {
						share.put(k, mount.get(k));
					}
					
					shares.add(share);
				}
				
				jsonList.put(users[i], shares);
			}
        }
            
        serializable.put("nss", jsonList);
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        serializable.put("save_time", stamp.getTime());
        
		// todo to main class
		serializable.put("classname", this.getClass().getName());
		
        return serializable.toJSONString();
    }
	
	/**
	 * _fromSerializableMap
	 * @param data
	 * @return 
	 */
	@Override
	protected boolean _fromSerializableMap(Map data) {
		LinkedHashMap nss = (LinkedHashMap) data.get("nss");
		
		if( nss != null ) {
			this._netShares = nss;
		}
		
		return true;
	}
}
