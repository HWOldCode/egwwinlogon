/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LogonHttpServerHandler
 * @author Stefan Werfling
 */
public class LogonHttpServerHandler implements HttpHandler {

    /**
     * http context
     */
    protected HttpContext _context = null;
    
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
        this._context = server.createContext(this._getUrl(), this);
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
	
	/**
	 * postToMap
	 * @param exchange
	 * @throws IOException 
	 */
	static public Map<String, String> postToMap(HttpExchange exchange) throws IOException {
		
		Headers reqHeaders	= exchange.getRequestHeaders();
		String contentType	= reqHeaders.getFirst("Content-Type");
		String encoding		= "ISO-8859-1";
		
		if( contentType != null ) {
			/*Map<String,String> parms = ValueParser.parse(contentType);
			
			if( parms.containsKey("charset") ) {
				encoding = parms.get("charset");
			}*/
		}
		
		String qry;
		InputStream in = exchange.getRequestBody();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte buf[] = new byte[4096];
    
			for(int n=in.read(buf); n>0; n=in.read(buf) ) {
				out.write(buf, 0, n);
			}
			
			qry = new String(out.toByteArray(), encoding);
		} 
		finally {
			in.close();
		}
		
		Map<String, String> result = new HashMap<String, String>();

        if( qry != null ) {
            for( String param : qry.split("&") ) {
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
}