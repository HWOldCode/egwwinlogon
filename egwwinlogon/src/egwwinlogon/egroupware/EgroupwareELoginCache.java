/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.core.EgroupwareAuth;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import egwwinlogon.service.EgroupwarePGina;
import egwwinlogon.service.EgwWinLogon;
import egwwinlogon.service.EgwWinLogonUltis;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * EgroupwareELogin
 * @author Stefan Werfling
 */
public class EgroupwareELoginCache extends EgroupwareCacheList {

    /**
	 * serialVersionUID
	 */
	private final static long serialVersionUID = 1;

    /**
     * menuaction
     */
    public static final String EGW_HTTP_GET_CACHE_ACTION = "elogin.elogin_ui.ajax_cache";

    /**
     * encryption type
     */
    protected String _encryption_type = "";

    /**
     * EGW Accounts
     */
    protected LinkedList<LinkedHashMap> _accounts = null;

    /**
     * constructor
     */
    public EgroupwareELoginCache() {
        super();

        this._request_url = this._createJsonMenuaction(
            EgroupwareELoginCache.EGW_HTTP_GET_CACHE_ACTION);
    }

    /**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();

        data.put("json_data", "{\"request\":{\"parameters\":[null]}}");
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

                        this._encryption_type = (String) data.get("encryption_type");
                        this._accounts = (LinkedList) data.get("egw_accounts");
                    }
                }
            }
        }
    }

    /**
     * countAccounts
     * @return
     */
    public int countAccounts() {
        if( this._accounts != null ) {
            return this._accounts.size();
        }

        return 0;
    }

    /**
     * getAccount
     * @param i
     * @return
     */
    public LinkedHashMap getAccount(int i) {
        if( this._accounts != null ) {
            if( i < this._accounts.size() ) {
                return this._accounts.get(i);
            }
        }

        return null;
    }

    /**
     * setAccounts
     * @param accounts
     */
    public void setAccounts(LinkedList<LinkedHashMap> accounts) {
        this._accounts = accounts;
    }

    /**
     * getEncryptionType
     * @return
     */
    public String getEncryptionType() {
        return this._encryption_type;
    }

    /**
     * setEncryptionType
     * @param type
     * @return
     */
    public void setEncryptionType(String type) {
        this._encryption_type = type;
    }

    /**
     * existUsername
     * @param username
     * @return
     */
    public Boolean existUsername(String username) {
        if( this._accounts == null ) {
            return false;
        }

        for( int i=0; i<this._accounts.size(); i++ ) {
            LinkedHashMap account = (LinkedHashMap) this._accounts.get(i);
            String account_lid = (String) account.get("account_lid");

            if( account_lid.compareTo(username) == 0 ) {
                return true;
            }
        }

        return false;
    }

    /**
     * isStatusA
     *
     * @param username
     * @return
     */
    public Boolean isStatusA(String username) {
        if( this._accounts == null ) {
            return false;
        }

        for( int i=0; i<this._accounts.size(); i++ ) {
            LinkedHashMap account = (LinkedHashMap) this._accounts.get(i);
            String account_lid = (String) account.get("account_lid");

            if( account_lid.compareTo(username) == 0 ) {
                String account_status = (String) account.get("account_status");

                if( account_status.compareTo("A") == 0 ) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * isAccountExpires
     *
     * @param username
     * @return
     */
    public Boolean isAccountExpires(String username) {
        if( this._accounts == null ) {
            return false;
        }

        for( int i=0; i<this._accounts.size(); i++ ) {
            LinkedHashMap account = (LinkedHashMap) this._accounts.get(i);
            String account_lid = (String) account.get("account_lid");

            if( account_lid.compareTo(username) == 0 ) {
                Integer account_expires = Integer.parseInt((String) account.get("account_expires"));
                Timestamp stamp = new Timestamp(System.currentTimeMillis());

                if( account_expires == -1 ) {
                    return true;
                }
                else if( account_expires >= stamp.getTime() ) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * compareUsernamePassword
     *
     * @param username
     * @param blankpassword
     * @return
     */
    public Boolean compareUsernamePassword(String username, String blankpassword) {
        if( this._accounts == null ) {
            return false;
        }

        for( int i=0; i<this._accounts.size(); i++ ) {
            LinkedHashMap account = (LinkedHashMap) this._accounts.get(i);
            String account_lid = (String) account.get("account_lid");

            if( account_lid.compareTo(username) == 0 ) {

                String password = (String) account.get("account_pwd");

                Boolean treturn = EgroupwareAuth.comparePassword(
                    blankpassword,
                    password,
                    this._encryption_type,
                    account_lid);

                if( treturn ) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return false;
    }

    /**
	 * _toSerializableString
	 * @return
	 */
	@Override
	protected String _toSerializableString() {
		JSONObject serializable = new JSONObject();
        JSONArray jsonList = new JSONArray();

        if( this.countAccounts() == 0 ) {
            return null;
        }

        for( int i=0; i<this.countAccounts(); i++ ) {
            LinkedHashMap account = this.getAccount(i);
            JSONArray jsonAccountDataList = new JSONArray();

            Set<String> keys = account.keySet();

            for( String k :keys ) {
                JSONObject data = new JSONObject();
                data.put(k, account.get(k));
                jsonAccountDataList.add(data);
            }

            jsonList.add(jsonAccountDataList);
        }

        serializable.put("accounts", jsonList);
        serializable.put("encryption_type", this.getEncryptionType());

        // save time for later (check max cache access)
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
        LinkedList<LinkedHashMap> accounts = new LinkedList();
        LinkedList taccounts = (LinkedList) data.get("accounts");

        for( int i=0; i<taccounts.size(); i++ ) {
            LinkedList taccount = (LinkedList) taccounts.get(i);
            LinkedHashMap naccount = new LinkedHashMap();

            for( int e=0; e<taccount.size(); e++ ) {
                LinkedHashMap tdata = (LinkedHashMap) taccount.get(e);

                Set<String> keys = tdata.keySet();

                for( String k :keys ) {
                    naccount.put(k, tdata.get(k));
                }
            }

            accounts.add(naccount);
        }

        this.setEncryptionType((String) data.get("encryption_type"));
        this.setAccounts(accounts);

		return true;
	}
}