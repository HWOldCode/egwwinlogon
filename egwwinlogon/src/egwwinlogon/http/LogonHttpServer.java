/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.http;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * LogonHttpServer
 * @author Stefan Werfling
 */
public class LogonHttpServer {

    /**
     * init
     */
    protected int _port = 8108;

    /**
     * HttpServer
     */
    protected HttpServer _server = null;

    /**
     * LogonHttpServer
     */
    public LogonHttpServer() {
    }

    /**
     * LogonHttpServer
     * @param port
     */
    public LogonHttpServer(int port) {
        this._port = port;
    }

    /**
     * init
     * @throws IOException
     */
    public void init() throws IOException {
        this._server = HttpServer.create(new InetSocketAddress(this._port), 0);
        this._server.setExecutor(null);
    }

    /**
     * start
     * @throws IOException
     */
    public void start() throws IOException {
        this._server.start();
    }

    /**
     * stop
     */
    public void stop() {
        this._server.stop(0);
    }

    /**
     * createContext
     *
     * @param url
     * @param handler
     */
    public HttpContext createContext(String url, HttpHandler handler) {
        return this._server.createContext(url, handler);
    }
}
