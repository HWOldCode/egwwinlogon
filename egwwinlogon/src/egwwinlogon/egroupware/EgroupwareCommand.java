/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import egwwinlogon.service.EgroupwarePGina;
import egwwinlogon.service.EgwWinLogonUltis;
import egwwinlogon.winapi.ProcessList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
     * self instance
     */
    public static EgroupwareCommand instance = null;
    
    /**
     * menuaction
     */
    public static final String EGW_HTTP_GET_CMD_ACTION = "elogin.elogin_cmd_ui.ajax_cmd_list";

    /**
     * Types of cmd
     */
    public static final String TYPE_USER        = "user";
    public static final String TYPE_SERVICE     = "service";

    public static final String EVENT_LOGIN_PRE      = "login_pre";
    public static final String EVENT_LOGIN          = "login";
    public static final String EVENT_LOGIN_AFTER    = "login_after";
    public static final String EVENT_LOCK           = "lock";
    public static final String EVENT_UNLOCK         = "unlock";
    
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
    public EgroupwareCommand() {
        super();

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
            "{\"uid\": \"" + EgroupwarePGina.getSysFingerprint() + "\"}" +
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
     * getCmdCount
     * @return 
     */
    public int getCmdCount() {
        if( this._cmds != null ) {
            return this._cmds.size();
        }
        
        return 0;
    }
    
    /**
     * getCmd
     * @param i
     * @return 
     */
    public LinkedHashMap getCmd(int i) {
        if( this._cmds != null ) {
            if( (this._cmds.size() > i) && (i >= 0) ) {
                return (LinkedHashMap) this._cmds.get(i);
            }
        }
        
        return null;
    }
    
    /**
     * setCmds
     * @param cmds 
     */
    public void setCmds(LinkedList cmds) {
        this._cmds = cmds;
    }
    
    /**
     * execute
     * 
     * @param sessionId 
     * @param type
     * @param event
     */
    public void execute(int sessionId, String type, String event) {
        if( this._cmds != null ) {
            for( int i=0; i<this._cmds.size(); i++ ) {
                LinkedHashMap cmddata = (LinkedHashMap) this._cmds.get(i);
                
                try {
                    String cmdid        = (String) cmddata.get("id");
                    String machineid    = (String) cmddata.get("machine_id");
                    String accountid    = (String) cmddata.get("account_id");
                    String command      = (String) cmddata.get("command");
                    String csystem      = (String) cmddata.get("system");
                    String order        = (String) cmddata.get("order");
                    String ctype        = (String) cmddata.get("type");
                    String cevent       = (String) cmddata.get("event");
                    
                    // conditions
                    String withConsole  = "0";  // false
                    String process_wait = "0";  // false
                    
                    if( cmddata.containsKey("condition") ) {
                        LinkedList<String> conditions =  (LinkedList<String>) cmddata.get("condition");
                        
                        if( conditions.contains("with_console") ) {
                            withConsole = "1";
                        }
                        
                        if( conditions.contains("wait") ) {
                            process_wait = "1";
                        }
                    }
                    
                    logger.info("EgroupwareCommand fount-Cmd: " + 
                        "ID: '" + cmdid + "' " + 
                        "MachineId: '" + machineid + "' " + 
                        "AccountId: '" + accountid + "' " + 
                        "Command: '" + command + "' " + 
                        "System: '" + csystem + "' " + 
                        "Order: '" + order + "' " + 
                        "Type: '" + ctype + "' " + 
                        "Event: '" + cevent + "' "
                        );

                    // is right machine
                    if( (!machineid.equals(EgroupwarePGina.getSysFingerprint())) && 
                        (!"all".equals(machineid)) ) 
                    {
                        continue;
                    }

                    // right account
                    // TODO

                    // type and event
                    if( (!type.equals(ctype)) || 
                        (!event.equals(cevent)) ) 
                    {
                        continue;
                    }
                
                    logger.info("EgroupwareCommand execute-Cmd: " + command);
                    
                    int pid = -1;
                    String exec_cmd = "";
                    
                    if( withConsole == "1" ) {
                        exec_cmd += "cmd /c ";
                    }
                    
                    exec_cmd += command;
                    
                    if( type == EgroupwareCommand.TYPE_SERVICE ) {
                        pid = EgroupwarePGina.startProcessInWinsta0Winlogon(exec_cmd);
                    }
                    else {
                        pid = EgroupwarePGina.startUserProcessInSession(sessionId, exec_cmd);
                    }
                    
                    // ---------------------------------------------------------
                    
                    if( process_wait == "1" ) {
                        int wcount = 0;
                        
                        while( ProcessList.existProcessById(pid) ) {
                            Thread.sleep(1000);
                            wcount += 1000;
                            
                            // break out 2 mins
                            if( wcount >= (1000*60*2) ) {
                                break;
                            }
                        }
                    }
                    
                    // ---------------------------------------------------------
                    
                    logger.info("EgroupwareCommand pid: " + String.valueOf(pid));
                    
                    //Thread.sleep(1000);
                }
                catch( Exception e ) {
                    logger.error(e.getMessage());
                }
                
                /*
                try {
                    int pid = EgroupwarePGina.startUserProcessInSession(sessionId, "cmd /c " + cmd); //Runtime.getRuntime().exec("cmd /c " + cmd);
                    
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
                /*}
                catch( Exception e ) {
                    logger.error(e.getMessage());
                }*/
            }
        }
    }
    
    /**
	 * toSerializableString
	 *
	 * @param commands
	 * @return
	 * @throws IOException
	 */
	static public String toSerializableString(EgroupwareCommand commands) throws IOException {
        JSONObject serializable = new JSONObject();
        JSONArray jsonList = new JSONArray();
        
        if( commands.getCmdCount() == 0 ) {
            return null;
        }
        
        for( int i=0; i<commands.getCmdCount(); i++ ) {
            LinkedHashMap cmdData = commands.getCmd(i);
            
            if( cmdData != null ) {
                JSONArray jsonCmdDataList = new JSONArray();
                
                Set<String> keys = cmdData.keySet();
                
                for( String k :keys ) {
                    JSONObject data = new JSONObject();
                    data.put(k, cmdData.get(k));
                    jsonCmdDataList.add(data);
                }

                jsonList.add(jsonCmdDataList);
            }
        }
            
        serializable.put("cmds", jsonList);
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        serializable.put("save_time", stamp.getTime());
        
        return serializable.toJSONString();
    }
    
    /**
	 * fromSerializableString
	 *
	 * @param serialize
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	static public EgroupwareCommand fromSerializableString(String serialize) throws IOException, ClassNotFoundException {
        EgroupwareCommand commands = new EgroupwareCommand();
        
        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory(){
                public List creatArrayContainer() {
                    return new LinkedList();
                }

                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }
            };

        Map data = null;

        try {
            data = (Map)parser.parse(serialize.trim(), containerFactory);
        } catch( ParseException ex ) {
            java.util.logging.Logger.getLogger(
                EgroupwareCommand.class.getName()).log(
                    Level.SEVERE,
                    null,
                    ex);

            return null;
        }
        
        LinkedList<LinkedHashMap> mcommands = new LinkedList();
        
        LinkedList tcommands = (LinkedList) data.get("cmds");
        
        for( int i=0; i<tcommands.size(); i++ ) {
            LinkedList tcmd = (LinkedList) tcommands.get(i);
            LinkedHashMap ncmd = new LinkedHashMap();

            for( int e=0; e<tcmd.size(); e++ ) {
                LinkedHashMap tdata = (LinkedHashMap) tcmd.get(e);

                Set<String> keys = tdata.keySet();

                for( String k :keys ) {
                    ncmd.put(k, tdata.get(k));
                }
            }

            mcommands.add(ncmd);
        }
        
        commands.setCmds(mcommands);
        
        return commands;
    }
    
    /**
     * loadByFile
     *
     * @param file
     * @return
     */
    static public EgroupwareCommand loadByFile(String file) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(file)));
            
            content = EgwWinLogonUltis.getStrDecode(
                content,
                EgroupwarePGina.getDLLHash() + EgroupwarePGina.getSysFingerprint()
                );
            
            //EgroupwarePGina.logInfo("loadByFile Cache: " + content);
            
            return EgroupwareCommand.fromSerializableString(content);
        }
        catch( Exception ex ) {
            EgroupwarePGina.logError(
                "EgroupwareCommand.loadByFile:" + ex.getMessage() + 
                " File: " + file
                );
            
            java.util.logging.Logger.getLogger(
                EgroupwareCommand.class.getName()).log(Level.SEVERE, null, ex);
            
            return null;
        }
    }
    
    /**
     * saveToFile
     * @param commands
     * @param file
     * @return
     */
    static public Boolean saveToFile(EgroupwareCommand commands, String file) {
        try {
            String content = EgroupwareCommand.toSerializableString(commands);
            
            content = EgwWinLogonUltis.getStrEncode(
                content,
                EgroupwarePGina.getDLLHash() + EgroupwarePGina.getSysFingerprint()
                );
            
            Files.write(Paths.get(file), content.getBytes());
        }
        catch( Exception ex ) {
            EgroupwarePGina.logError(
                "EgroupwareCommand.saveToFile:" + ex.getMessage() + 
                " File: " + file
                );
            
            java.util.logging.Logger.getLogger(
                EgroupwareCommand.class.getName()).log(Level.SEVERE, null, ex);
            
            return false;
        }

        return true;
    }
}