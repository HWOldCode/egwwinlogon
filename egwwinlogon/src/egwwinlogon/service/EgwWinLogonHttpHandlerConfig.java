/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import egwwinlogon.http.LogonHttpServerHandler;
import egwwinlogon.winapi.PlatformInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.IOUtils;

/**
 * EgwWinLogonHttpHandlerConfig
 * @author Stefan Werfling
 */
public class EgwWinLogonHttpHandlerConfig extends LogonHttpServerHandler {

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

                        t.sendResponseHeaders(200, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();

                        return;
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