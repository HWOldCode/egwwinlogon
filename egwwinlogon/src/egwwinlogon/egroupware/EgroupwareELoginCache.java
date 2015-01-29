/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.EgroupwareNotifications;
import com.jegroupware.egroupware.core.EgroupwareAuth;
import egwwinlogon.service.EgwWinLogon;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

/**
 * EgroupwareELogin
 * @author Stefan Werfling
 */
public class EgroupwareELoginCache extends EgroupwareJson {

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
    public void setRawContent(String content) {
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
	 * toSerializableString
	 *
	 * @param cache
	 * @return
	 * @throws IOException
	 */
	static public String toSerializableString(EgroupwareELoginCache cache) throws IOException {
		ByteArrayOutputStream _baos = new ByteArrayOutputStream();
		ObjectOutputStream _oos = new ObjectOutputStream(_baos);

		_oos.writeObject(cache);
		_oos.flush();

		return _baos.toString("ISO-8859-1");
	}

    /**
	 * fromSerializableString
	 *
	 * @param serialize
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	static public EgroupwareELoginCache fromSerializableString(String serialize) throws IOException, ClassNotFoundException {
		ObjectInputStream _ois = new ObjectInputStream(new ByteArrayInputStream(serialize.getBytes("ISO-8859-1")));
		Object _o = _ois.readObject();
		_ois.close();

		return (EgroupwareELoginCache) _o;
	}

    /**
     * loadByFile
     *
     * @param file
     * @return
     */
    static public EgroupwareELoginCache loadByFile(String file) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(file)));
            return EgroupwareELoginCache.fromSerializableString(content);
        }
        catch( Exception ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * saveToFile
     * @param cache
     * @param file
     * @return
     */
    static public Boolean saveToFile(EgroupwareELoginCache cache, String file) {
        try {
            String content = EgroupwareELoginCache.toSerializableString(cache);
            Files.write(Paths.get(file), content.getBytes());
        }
        catch( Exception ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    // https://www.tutorials.de/threads/linkedlist-speichern.186639/
}
