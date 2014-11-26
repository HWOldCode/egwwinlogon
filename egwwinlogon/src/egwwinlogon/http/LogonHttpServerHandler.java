/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * LogonHttpServerHandler
 * @author Stefan Werfling
 */
public class LogonHttpServerHandler implements HttpHandler {

    /**
     * _getUrl
     * @return
     */
    protected String _getUrl() {
        return "/";
    }

    /**
     * register
     * @param server
     */
    public void register(LogonHttpServer server) {
        server.createContext(this._getUrl(), this);
    }

    /**
     * handle
     * @param t
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange t) throws IOException {
    }

    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    static public Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();

        if( query != null ) {
            for( String param : query.split("&") ) {
                String pair[] = param.split("=");

                if( pair.length>1 ) {
                    result.put(pair[0], pair[1]);
                } else {
                    result.put(pair[0], "");
                }
            }
        }

        return result;
    }

    /**
     * getFile
     * @param file
     * @return
     */
    static public InputStream getFile(String file) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(file);
    }
}
