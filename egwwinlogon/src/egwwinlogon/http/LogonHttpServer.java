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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import org.apache.log4j.spi.LoggingEvent;

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
     * Handler List
     */
    protected ArrayList<LogonHttpServerHandler> _handlerList = new ArrayList();
    
    /**
     * server is started
     */
    protected Boolean _isStarted = false;
    
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
        this._server = HttpServer.create(new InetSocketAddress(
            Inet4Address.getByName("127.0.0.1"), this._port), 0);
        
        this._server.setExecutor(null);
    }

    /**
     * start
     * @throws IOException
     */
    public void start() throws IOException {
        if( this._server != null ) {
            this._server.start();
            this._isStarted = true;
        }
    }

    /**
     * stop
     */
    public void stop() {
        if( this._server != null ) {
            this._server.stop(0);
            this._isStarted = false;
        }
    }

    /**
     * isStarted
     * @return boolean
     */
    public Boolean isStarted() {
        return this._isStarted;
    }
    
    /**
     * createContext
     *
     * @param url
     * @param handler
     */
    public HttpContext createContext(String url, LogonHttpServerHandler handler) {
        if( this._server != null ) {
            this._handlerList.add(handler);
            return this._server.createContext(url, handler);
        }
        
        return null;
    }
    
    /**
     * getHandler
     * 
     * @param classname
     * @return LogonHttpServerHandler
     */
    public LogonHttpServerHandler getHandler(String classname) {
        for( LogonHttpServerHandler le: this._handlerList ) {
            if( le.getClass().getName() == classname ) {
                return le;
            }
        }
        
        return null;
    }
}
