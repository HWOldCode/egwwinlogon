package egwwinlogon.service;


import com.sun.net.httpserver.HttpExchange;
import egwwinlogon.http.LogonHttpServerHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * EgwWinLogonHttpHandlerLogger
 * @author Stefan Werfling
 */
public class EgwWinLogonHttpHandlerLogger extends LogonHttpServerHandler {

    protected LoggerHttpHandler _handler = new LoggerHttpHandler();

    /**
     * getLoggerHandler
     * @return
     */
    public LoggerHttpHandler getLoggerHandler() {
        return this._handler;
    }

    /**
     * _getUrl
     * @return
     */
    protected String _getUrl() {
        return "/logger";
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "This is the response, logger";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}


class LoggerHttpHandler extends Handler {

    @Override
    public void publish(LogRecord lr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws SecurityException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}