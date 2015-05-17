/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import egwwinlogon.service.EgroupwareDLL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * EgroupwareCommand
 * 
 * @author Stefan Werfling
 */
public class EgroupwareCommand extends EgroupwareJson {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgroupwareCommand.class);
    
    /**
     * menuaction
     */
    public static final String EGW_HTTP_GET_CMD_ACTION = "elogin.elogin_cmd_ui.ajax_cmd_list";

    /**
     * Types of cmd
     */
    public static final String EGW_CMD_TYPE_USER        = "user";
    public static final String EGW_CMD_TYPE_SERVICE     = "service";

    public static final String EGW_CMD_EVENT_APPSTART   = "appstart";
    
    /**
     * uid of machine
     */
    protected String _uid = "";
    
    /**
     * command type
     */
    protected String _cmdtype = "";

    /**
     * cmds
     */
    protected LinkedList _cmds = null;
    
    /**
     * constructor
     *
     * @param uid
     * @param cmdtype
     */
    public EgroupwareCommand(String uid, String cmdtype) {
        super();

        this._uid       = uid;
        this._cmdtype   = cmdtype;
        this._request_url = this._createJsonMenuaction(
            EgroupwareCommand.EGW_HTTP_GET_CMD_ACTION);
    }

    /**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();

        data.put("json_data", "{\"request\":{\"parameters\":[" +
            "{\"uid\": \"" + this._uid + "\", \"cmdtype\": \"" + this._cmdtype + "\"}" +
            "]}}");

        return data;
    }

    /**
     * setRawContent
     * @param content
     */
    @Override
    public void setRawContent(String content) throws EGroupwareExceptionRedirect {
        super.setRawContent(content);

        if( this._json != null ) {
            LinkedList respsone = (LinkedList) this._json.get("response");

            if( respsone != null ) {
                for( int i=0; i<respsone.size(); i++ ) {
                    LinkedHashMap rcontent = (LinkedHashMap) respsone.get(i);
                    String type = (String) rcontent.get("type");

                    if( type.compareTo("data") == 0 ) {
                        LinkedHashMap data = (LinkedHashMap) rcontent.get("data");
                        
                        this._cmds = (LinkedList) data.get("cmds");
                    }
                }
            }
        }
    }
    
    /**
     * execute
     * @param sessionId 
     */
    public void execute(int sessionId) {
        if( this._cmds != null ) {
            for( int i=0; i<this._cmds.size(); i++ ) {
                String cmd = (String) this._cmds.get(i);
                
                logger.info("EgroupwareCommand execute-Cmd: " + cmd);
                
                try {
                    int pid = EgroupwareDLL.startUserProcessInSession(sessionId, "cmd /c " + cmd); //Runtime.getRuntime().exec("cmd /c " + cmd);
                    
                    logger.info("EgroupwareCommand pid: " + String.valueOf(pid));
                    /*String cmdout = "";
                    
                    Reader r = new InputStreamReader(proc.getInputStream());
                    BufferedReader in = new BufferedReader(r);
                    String line = "";
                    
                    while( (line = in.readLine()) != null ) {
                        cmdout += line + "\r\n";
                    }
                    
                    in.close();
                    
                    logger.info("Cmdline output: " + cmdout);
                    */
                    //Thread.sleep(1000);
                }
                catch( Exception e ) {
                    logger.error(e.getMessage());
                }
            }
        }
    }
}
