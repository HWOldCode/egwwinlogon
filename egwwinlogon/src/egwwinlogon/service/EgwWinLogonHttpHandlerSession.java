/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.jegroupware.egroupware.EgroupwareSession;
import com.sun.net.httpserver.HttpExchange;
import egwwinlogon.http.LogonHttpServerHandler;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

/**
 * EgwWinLogonHttpHandlerSession
 * @author Stefan Werfling
 */
public class EgwWinLogonHttpHandlerSession extends LogonHttpServerHandler {

    /**
     * _getUrl
     * @return
     */
    protected String _getUrl() {
        return "/session";
    }

    @Override
    public void handle(HttpExchange t) {
        try {
            Map<String, String> params = LogonHttpServerHandler.queryToMap(
                    t.getRequestURI().getQuery());

            String path = t.getRequestURI().getPath();

            if( params.containsKey("user") ) {
                String user = params.get("user");
                Egroupware egw = Egroupware.findInstance(user);

                if( egw != null ) {
                    String response = "";

                    String config = EgroupwareConfig.toSerializableString(egw.getConfig());
                    response = response + "config:" + new String(
                        Base64.encodeBase64(config.getBytes())) + ";";

                    String session = EgroupwareSession.toSerializableString(egw.getSession());
                    response = response + "session:" + new String(
                        Base64.encodeBase64(session.getBytes())) + ";";

                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }

        } catch( Exception ex) {
            Logger.getLogger(EgwWinLogonHttpHandlerConfig.class.getName()).log(
                Level.SEVERE, null, ex);
        }
    }
}