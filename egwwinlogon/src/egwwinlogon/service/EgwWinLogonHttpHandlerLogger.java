package egwwinlogon.service;


import com.sun.net.httpserver.HttpExchange;
import egwwinlogon.egroupware.EgroupwareMachineLogging;
import egwwinlogon.http.LogonHttpServerHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * EgwWinLogonHttpHandlerLogger
 * @author Stefan Werfling
 */
public class EgwWinLogonHttpHandlerLogger extends LogonHttpServerHandler {

    /**
     * Machine Logger
     */
    protected EgroupwareMachineLogging _machineLogger = null;

    /**
     * setMachineLogger
     * @param mlog 
     */
    public void setMachineLogger(EgroupwareMachineLogging mlog) {
        this._machineLogger = mlog;
    }
    
    /**
     * _getUrl
     * @return
     */
    protected String _getUrl() {
        return "/logger";
    }

	/**
	 * handle
	 * @param t
	 * @throws IOException 
	 */
    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            Map<String, String> params = LogonHttpServerHandler.queryToMap(
                    t.getRequestURI().getQuery());

            String path = t.getRequestURI().getPath();

            if( "/logger/".equals(path) ) {
                if( params.containsKey("log_to_egw") ) {
                    String value = params.get("log_to_egw");
                    
                    // logging 
                    if( value.equals("1") ) {
                        if( this._machineLogger != null ) {
                            String message  = params.get("msg");
                            String event    = "httplog";
                            String level    = params.get("level");
                            
                            this._machineLogger.log(message, event, level);
                        }
                    }
                }
            }
        }
        catch( Exception ex ) {
            Logger.getLogger(
                EgwWinLogonHttpHandlerLogger.class.getName()
                ).log(Level.SEVERE, null, ex);
        }
        
        String response = "This is the response, logger";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}