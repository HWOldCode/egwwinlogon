/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * LogonHttpClient
 * @author Stefan Werfling
 */
public class LogonHttpClient {

    /**
     * connection
     */
    private HttpURLConnection _connection = null;

    /**
     * socket timeout
     */
	private int _socketTimeout = 10000;

    /**
     * cookie
     */
    private String _cookie = "";

    /**
     * user URL Encode by POST, see (http://en.wikipedia.org/wiki/POST_(HTTP))
     * default = true
     */
	private boolean _isUrlEncode			= true;

    /**
     * _initConnection
     *
     * @param url
     * @throws Exception
     */
    protected void _initConnection(String url) throws Exception {
        HttpURLConnection.setFollowRedirects(false);

        URL jtUrl = new URL(url);

        this._connection = (HttpURLConnection) jtUrl.openConnection();
		this._connection.setConnectTimeout(this._socketTimeout);

        if( !this._cookie.equals("") ) {
            this._connection.addRequestProperty("Cookie", this._cookie);
        }
    }

    /**
     * _readHeader
     * @throws Exception
     */
    protected void _readHeader() throws Exception {
        if( this._connection != null ) {
            Map<String, List<String>> headers = this._connection.getHeaderFields();
            List<String> values = headers.get("Set-Cookie");

            this._cookie = "";

            if( values != null ) {
                for( String value : values ) {
                    if( !this._cookie.equals("") ) {
                        this._cookie += ";";
                    }

                    this._cookie += value;
                }
            }
        }
    }

    /**
     * _readBuffer
     * @return
     * @throws Exception
     */
    protected String _readBuffer() throws Exception {
        String buff = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(this._connection.getInputStream()));

			String line = bufferedReader.readLine();

            while( line != null ) {
				buff = buff + line + "\r\n";
				line = bufferedReader.readLine();
			}

			bufferedReader.close();

            this._connection.disconnect();
        }
        catch( Exception exp ) {
            throw exp;
        }

        return buff;
    }

    /**
     * sendGET
     *
     * @param url String
     * @return String
     * @throws Exception
     */
    public String sendGET(String url) throws Exception {
        this._initConnection(url);
        this._readHeader();

        return this._readBuffer();
    }

    /**
     * sendPOST
     *
     * @param url
     * @param data
     * @return String
     * @throws Exception
     */
    public String sendPOST(String url, Map<String, String> data) throws Exception {
        this._initConnection(url);

        this._connection.setDoOutput(true);

        String post = "";

        for( String key: data.keySet() ) {
            String value = data.get(key);

            if( !post.equals("") ) {
				post += "&";
			}

            if( this._isUrlEncode ) {
                key		= URLEncoder.encode(key);
				value	= URLEncoder.encode(value);
            }

            post += key + "=" + value;
        }

        OutputStreamWriter wr = new OutputStreamWriter(this._connection.getOutputStream());
        wr.write(post);
        wr.flush();
        wr.close();

        this._readHeader();

        return this._readBuffer();
    }
}
