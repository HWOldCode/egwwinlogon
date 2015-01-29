/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import org.hsqldb.Server;

/**
 * EgwWinLogonDb
 * @author Stefan Werfling
 */
public class EgwWinLogonDb {
    
    private Server _hsqlServer = null;
    
    
    public EgwWinLogonDb() {
        // http://blog.rajatpandit.com/2012/10/04/example-code-for-embedded-hsql-for-java/
        this._hsqlServer = new Server();
        this._hsqlServer.setLogWriter(null);
        this._hsqlServer.setSilent(true);
        this._hsqlServer.setDatabaseName(0, "egwwinlogon");
        this._hsqlServer.setDatabasePath(0, "file:db/egwwinlogon"); // ;crypt_key=D3425G675;crypt_type=AES
        //this._hsqlServer.set
    }
    
    /**
     * start
     */
    public void start() {
        this._hsqlServer.start();
    }
    
    /**
     * stop
     */
    public void stop() {
        this._hsqlServer.stop();
    }
}
