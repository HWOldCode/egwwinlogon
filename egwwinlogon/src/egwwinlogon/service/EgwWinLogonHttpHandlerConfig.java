/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import egwwinlogon.http.LogonHttpServerHandler;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author swe
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
    public void handle(HttpExchange t) throws IOException {
        String response = "This is the response, config";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
