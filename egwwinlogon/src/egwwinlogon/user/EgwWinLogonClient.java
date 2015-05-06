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
import org.apache.commons.codec.binary.Base64;

/**
 * EgwWinLogonClient
 * 
 * @author Stefan Werfling
 */
public class EgwWinLogonClient  {

    // BASE URL
    public static final String URL = "http://localhost:8108/";

    public static final String REQUEST_SESSION = "session";

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
System.out.println(request);
/*System.out.println(System.getenv("USERNAME"));
System.out.println(Advapi32Util.getUserName());

for( Account account: Advapi32Util.getCurrentUserGroups())
        System.out.println(account.fqn);
*/
            String buffer = this._client.sendGET(request);
System.out.println(buffer);
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
}
