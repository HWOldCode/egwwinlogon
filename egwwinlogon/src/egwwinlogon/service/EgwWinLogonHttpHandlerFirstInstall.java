/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpExchange;
import egwwinlogon.egroupware.EgroupwareCommand;
import egwwinlogon.egroupware.EgroupwareELoginCache;
import egwwinlogon.egroupware.EgroupwareMachineInfo;
import egwwinlogon.http.LogonHttpServer;
import egwwinlogon.http.LogonHttpServerHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EgwWinLogonHttpHandlerFirstInstall
 * 
 * @author Stefan Werfling
 */
public class EgwWinLogonHttpHandlerFirstInstall extends LogonHttpServerHandler {
   
    /**
     * Authenticator
     */
    protected EWinLogonCacheBasicAuthenticator _auth = null;
    
    /**
     * constructor
     */
    public EgwWinLogonHttpHandlerFirstInstall() {
        super();
        this._auth = new EWinLogonCacheBasicAuthenticator("EGroupware ELogin - Firstinstallation");
    }
    
    /**
     * _getUrl
     * @return
     */
    protected String _getUrl() {
        return "/firstinstall";
    }
    
    /**
     * register
     * @param server
     */
    public void register(LogonHttpServer server) {
        super.register(server);
        
        if( this._context != null ) {
            this._context.setAuthenticator(this._auth);
        }
    }
    
    @Override
    public void handle(HttpExchange t) throws IOException {
        EgroupwareELoginCache _eLoginCache = null;
        
        try {
            Map<String, String> params = LogonHttpServerHandler.queryToMap(
                    t.getRequestURI().getQuery());

            String path     = t.getRequestURI().getPath();
            String response = "";
            
            if( "/firstinstall/".equals(path) ) {
                _eLoginCache = (EgroupwareELoginCache) EgroupwareELoginCache.loadByFile(
                    EgroupwarePGina.getAppDirCache() + "/elogin.cache");

                if( _eLoginCache == null ) {
                    // Egroupware Config
                    EgroupwareConfig config = new EgroupwareConfig();
                    config.setUrl(EgwWinLogon.getSetting("url"));
                    config.setDomain(EgwWinLogon.getSetting("domain"));
                    config.setUser(this._auth.getUsername());
                    config.setPassword(this._auth.getPassword());
                    
                    Egroupware _egw = Egroupware.getInstance(config);
                    
                    try {
                        _egw.login();
                        
                        if( _egw.isLogin() ) {
                            response += "\r\nEGroupware Login: successful.";
                            
                            // machine info send
                            EgroupwareMachineInfo mi = new EgroupwareMachineInfo(
                                EgwWinLogon.getSetting("sysfingerprint"));

                            mi.setMachineName(EgwWinLogon.getSetting("machinename"));
                            _egw.request(mi);
                            
                            // -------------------------------------------------
                            
                            _eLoginCache = new EgroupwareELoginCache();
                            
                             _egw.request(_eLoginCache);

                            if( _eLoginCache.countAccounts() > 0 ) {
                                EgroupwareELoginCache.saveToFile(
                                    _eLoginCache, 
                                    EgroupwarePGina.getAppDirCache() + "/elogin.cache"
                                    );
                                
                                response += "\r\nUser-Cache-List: create and save.";
                            }
                            else {
                                response += "\r\nUser-Cache-List: is Empty and not save!";
                            }
                            
                            // -------------------------------------------------
                            
                            _egw.request(EgroupwareCommand.instance);
                            
                            if( EgroupwareCommand.instance.getCmdCount() > 0 ) {
                                EgroupwareCommand.saveToFile(
                                    EgroupwareCommand.instance, 
                                    EgroupwarePGina.getAppDirCache() + "/ecommands.cache"
                                    );
                                
                                response += "\r\nCommand-Cache-List: create and save.";
                            }
                            else {
                                response += "\r\nCommand-Cache-List: is Empty and not save!";
                            }
                        }
                        else {
                            response += "\r\nEGroupware Login: faild, please check your account.";
                        }
                    }
                    catch( Exception te ) {
                        response += "\r\nEGroupware Error: " + te.getMessage();
                        response += "\r\nBy Config: URL: " + EgwWinLogon.getSetting("url") + 
                            " Doamin: " + EgwWinLogon.getSetting("domain");
                    }
                }
                else {
                    response += "\r\nError, isn`t first install!";
                }
            }
            
            // reset auth
            this._auth.setReturn(false);
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        catch( Exception ex ) {
            Logger.getLogger(
                EgwWinLogonHttpHandlerLogger.class.getName()
                ).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * EWinLogonCacheBasicAuthenticator
     * @author Stefan Werfling
     */
    public static final class EWinLogonCacheBasicAuthenticator extends BasicAuthenticator {

        /**
         * username
         */
        private String _username = "";
        
        /**
         * password
         */
        private String _password = "";
        
        /**
         * return
         */
        private Boolean _return = true;
        
        /**
         * constructor
         * @param string 
         */
        public EWinLogonCacheBasicAuthenticator(String string) {
            super(string);
        }

        /**
         * checkCredentials
         * 
         * @param string
         * @param string1
         * @return 
         */
        @Override
        public boolean checkCredentials(String username, String password) {
            if( "".equals(this._username) ) {
                this._username = username;
                this._password = password;
                
                this._return = true;
            }
            
            if( this._return == false ) {
                this._username = "";
                this._password = "";
            }
            
            return this._return;
        }
        
        /**
         * setReturn
         * @param treturn 
         */
        public void setReturn(Boolean treturn) {
            this._return = treturn;
        }
        
        /**
         * getUsername
         * @return 
         */
        public String getUsername() {
            return this._username;
        }
        
        /**
         * getPassword
         * @return 
         */
        public String getPassword(){
            return this._password;
        }
    }
}