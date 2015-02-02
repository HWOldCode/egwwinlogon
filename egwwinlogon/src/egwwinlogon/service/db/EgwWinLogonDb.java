/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service.db;

import egwwinlogon.service.EgwWinLogon;
import java.util.Properties;
import java.util.logging.Level;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;

/**
 * EgwWinLogonDb
 * @author Stefan Werfling
 */
public class EgwWinLogonDb {

    /**
     * Server
     */
    private Server _hsqlServer = null;

    private String _dbname = "egwwinlogon";

    /**
     * EgwWinLogonDb
     */
    public EgwWinLogonDb() {
        // http://blog.rajatpandit.com/2012/10/04/example-code-for-embedded-hsql-for-java/
        this._hsqlServer = new Server();

        try{
            Properties properties = new Properties();
            properties.put("crypt_key", "D3425G675");
            properties.put("crypt_type", "AES");

            this._hsqlServer.setProperties(new HsqlProperties(properties));
        }
        catch( Exception ex ) {
            java.util.logging.Logger.getLogger(EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
        }

        this._hsqlServer.setLogWriter(null);
        this._hsqlServer.setSilent(true);
        this._hsqlServer.setDatabaseName(0, this._dbname);
        this._hsqlServer.setDatabasePath(0, "file:db/egwwinlogon"); // ;crypt_key=D3425G675;crypt_type=AES
        //this._hsqlServer.set
    }

    /**
     * getNewConnection
     * @return EgwWinLogonDbConnection
     */
    public EgwWinLogonDbConnection getNewConnection() {
        return new EgwWinLogonDbConnection("localhost", this._dbname);
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
