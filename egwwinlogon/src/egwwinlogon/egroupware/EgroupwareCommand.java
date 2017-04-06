/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import egwwinlogon.service.EgroupwarePGina;
import egwwinlogon.winapi.ProcessList;
import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * EgroupwareCommand
 * 
 * @author Stefan Werfling
 */
public class EgroupwareCommand extends EgroupwareCacheList {

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
    public static final String TYPE_USER			= "user";
    public static final String TYPE_SERVICE			= "service";
    public static final String TYPE_WIN_STA0		= "winsta0";
	public static final String TYPE_BYCMD			= "bycmd";

    public static final String EVENT_LOGIN_PRE      = "login_pre";
    public static final String EVENT_LOGIN          = "login";
    public static final String EVENT_LOGIN_AFTER    = "login_after";
    public static final String EVENT_LOCK           = "lock";
    public static final String EVENT_UNLOCK         = "unlock";
	public static final String EVENT_LOGOFF			= "logoff";
    
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
            "{\"uid\": \"" + EgroupwarePGina.getSysFingerprint() + "\", \"netdrivedisable\": \"1\"}" +
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
						
						// convert event
						// -----------------------------------------------------
						if( this._cmds != null ) {
							for( int h=0; h<this._cmds.size(); h++ ) {
								LinkedHashMap cmddata = (LinkedHashMap) this._cmds.get(h);
								
								String cevent = (String) cmddata.get("event");
								
								if( cevent.equals(EgroupwareCommand.EVENT_LOGIN) ) {
									cmddata.put("event", EgroupwareCommand.EVENT_LOGIN_AFTER);
									this._cmds.set(h, cmddata);
								}
							}
						}
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
	 * _executeCommand
	 * 
	 * @param cmddata
	 * @param sessionId 
	 */
	protected void _executeCommand(LinkedHashMap cmddata, int sessionId) throws Exception {
		Boolean throwexec = false;
		
		try {			
			String cmdid        = (String) cmddata.get("id");
			String command      = (String) cmddata.get("command");
			String type			= (String) cmddata.get("type");
			String script_type	= (String) cmddata.get("script_type");
			String script		= (String) cmddata.get("script");
			String mountpoint	= (String) cmddata.get("mount_point_check");	// mount point
			
			File scriptFile		= null;
			int wcount			= 0;
			
			// conditions
			// -----------------------------------------------------------------
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

			// script
			// -----------------------------------------------------------------
			
			if( (!"none".equals(script_type)) && (!"".equals(script_type)) ) {
				//logger.info("_executeCommand script-type: " + script_type);
				
				String scriptFilename = EgroupwarePGina.getAppDirCache() + cmdid + ".";

				if( "vbs".equals(script_type) ) {
					scriptFilename += "vbs";

					command = "wscript \"" + scriptFilename + "\"";
				}
				else if( "batchfile".equals(script_type) ) {
					scriptFilename += "bat";
					
					command = "\"" + scriptFilename + "\"";
				}

				//logger.info("_executeCommand script-file: " + scriptFilename);
				
				scriptFile = new File(scriptFilename);
				
				// clean and write
				if( scriptFile.isFile() && scriptFile.exists() ) {
					//logger.info("_executeCommand script-file delete old script");
					
					Files.delete(scriptFile.toPath());
				}
				
				Files.write(scriptFile.toPath(), script.getBytes());
				
				/*logger.info("_executeCommand script-file write new script size:" + 
					String.valueOf(script.length()));*/
			}

			// exec
			// -----------------------------------------------------------------
			int pid			= -1;
			String exec_cmd = "";

			if( "1".equals(withConsole) ) {
				logger.info("_executeCommand with console");
				
				exec_cmd += "cmd /c ";
			}

			exec_cmd += command;

			//logger.info("_executeCommand execute-Cmd: " + exec_cmd);
			
			if( type.equals(EgroupwareCommand.TYPE_SERVICE) ) {
				pid = EgroupwarePGina.startProcessInWinsta0Winlogon(exec_cmd);
				
				//logger.info("_executeCommand startProcessInWinsta0Winlogon pid: " + String.valueOf(pid));
			}
			else if( type.equals(EgroupwareCommand.TYPE_SERVICE) ) {
				pid = EgroupwarePGina.startProcessInWinsta0Winlogon(exec_cmd);
				
				//logger.info("_executeCommand startProcessInWinsta0Winlogon pid: " + String.valueOf(pid));
			}
			else {
				pid = EgroupwarePGina.startUserProcessInSession(sessionId, exec_cmd);
				
				//logger.info("_executeCommand startUserProcessInSession pid: " + String.valueOf(pid));
			}
			
			// process waiting
			// -----------------------------------------------------------------
			if( "1".equals(process_wait) ) {
				//logger.info("_executeCommand waiting of process pid: " + String.valueOf(pid));
				
				while( ProcessList.existProcessById(pid) ) {
					Thread.sleep(1000);
					wcount += 1000;

					// break out 2 mins
					if( wcount >= (1000*60*2) ) {
						break;
					}
				}
				
				//logger.info("_executeCommand process stop pid: " + String.valueOf(pid));
			}
			
			// check mount point
			// -----------------------------------------------------------------
			if( (mountpoint != null) && (!"".equals(mountpoint)) ) {
				File tmp_mp = new File(mountpoint + ":/");
				
				logger.info("Check mount point: " + mountpoint);
				
				if( !tmp_mp.exists() ) {
					throwexec = true;
					throw new Exception("Mount point '" + mountpoint + "' not ready!");
				}
			}
			
			// cleaning
			// -----------------------------------------------------------------
			if( scriptFile != null ) {
				final int ipid			= pid;
				final File iscriptFile	= scriptFile;
				
				Thread thread = new Thread(){
					
					@Override
					public void run(){
						int wcount	= 0;
						
						try {
							while( ProcessList.existProcessById(ipid) ) {
								Thread.sleep(1000);
								wcount += 1000;

								// break out 2 mins
								if( wcount >= (1000*60*2) ) {
									break;
								}
							}
							
							if( iscriptFile.isFile() && iscriptFile.exists() ) {
								Files.delete(iscriptFile.toPath());
							}
						}
						catch( Exception e ) {
							logger.error(e.getMessage());
						}
					}
				};
            
				thread.start();
			}
		}
		catch( Exception e ) {
			logger.error(e.getMessage());
			
			if( throwexec ) {
				throw e;
			}
		}
	}
	
	/**
	 * executeId
	 * 
	 * @param sessionId
	 * @param id 
	 */
	public void executeId(int sessionId, String id) {
		if( this._cmds != null ) {
            for( int i=0; i<this._cmds.size(); i++ ) {
                LinkedHashMap cmddata = (LinkedHashMap) this._cmds.get(i);
                
                try {
					String cmdid = (String) cmddata.get("id");
					
					if( cmdid.equals(id) ) {
						this._executeCommand(cmddata, sessionId);
					}
				}
                catch( Exception e ) {
                    logger.error(e.getMessage());
                }
            }
        }
	}
	
	/**
	 * executeName
	 * 
	 * @param sessionId
	 * @param name 
	 */
	public void executeName(int sessionId, String name) {
		if( this._cmds != null ) {
            for( int i=0; i<this._cmds.size(); i++ ) {
                LinkedHashMap cmddata = (LinkedHashMap) this._cmds.get(i);
                
                try {
					String cmdname	= (String) cmddata.get("name");
					
					if( cmdname.equals(name) ) {
						this._executeCommand(cmddata, sessionId);
					}
				}
                catch( Exception e ) {
                    logger.error(e.getMessage());
                }
            }
        }
	}
	
    /**
     * executeEvent
     * 
     * @param sessionId 
     * @param type
     * @param event
	 * @throws java.lang.Exception
     */
    public void executeEvent(int sessionId, String type, String event) throws Exception {
		/*logger.info(
			"execute run by type: " + type + 
			" event: " + event + 
			" sessionId: " + String.valueOf(sessionId));*/
		
		LinkedList execmd = new LinkedList();
		
		// sorting
		// ---------------------------------------------------------------------
        if( this._cmds != null ) {
            for( int i=0; i<this._cmds.size(); i++ ) {
                LinkedHashMap cmddata = (LinkedHashMap) this._cmds.get(i);
                
                try {
                    String machineid    = (String) cmddata.get("machine_id");
                    String cmdname		= (String) cmddata.get("name");
                    String order        = (String) cmddata.get("order");
                    String ctype        = (String) cmddata.get("type");
                    String cevent       = (String) cmddata.get("event");

                    // is right machine
                    if( (!machineid.equals(EgroupwarePGina.getSysFingerprint())) && 
                        (!"all".equals(machineid)) ) 
                    {
                        continue;
                    }

                    // right account
                    // TODO

					// event
                    if( (!event.equals(cevent)) ) {
                        continue;
                    }
					
					// type
					if( (!type.equals(EgroupwareCommand.TYPE_BYCMD)) ) {
						if( (!type.equals(ctype)) ) {
							continue;
						}
					}
                
                    // ---------------------------------------------------------

					int orderInt = Integer.parseInt(order);
					
					if( execmd.size() < (orderInt+1) ) {
						for( int t=execmd.size(); t<(orderInt+1); t++ ) {
							execmd.add(new LinkedList());
						}
					}
					
					LinkedList orderList = (LinkedList) execmd.get(orderInt);

					//logger.info("execute cmd add to order list name: " + cmdname);
					
					orderList.add(cmddata);
					
					execmd.set(orderInt, orderList);
                }
                catch( Exception e ) {
                    logger.error(e.getMessage());
					
					throw e;
                }
            }
        }
		
		// exec in order
		// ---------------------------------------------------------------------
		for( int i=0; i<execmd.size(); i++  ) {
			LinkedList orderList = (LinkedList) execmd.get(i);
			
			for( int e=0; e<orderList.size(); e++ ) {
				LinkedHashMap cmddata = (LinkedHashMap) orderList.get(e);
				
				try {
					this._executeCommand(cmddata, sessionId);
				}
				catch( Exception g ) {
                    logger.error(g.getMessage());
					throw g;
                }
			}
		}
    }
    
    /**
	 * _toSerializableString
	 * @return
	 */
	@Override
	protected String _toSerializableString() {
        JSONObject serializable = new JSONObject();
        JSONArray jsonList = new JSONArray();
        
        if( this.getCmdCount() == 0 ) {
            return null;
        }
        
        for( int i=0; i<this.getCmdCount(); i++ ) {
            LinkedHashMap cmdData = this.getCmd(i);
            
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
        
		// todo to main class
		serializable.put("classname", this.getClass().getName());
		
        return serializable.toJSONString();
    }
    
    /**
	 * _fromSerializableMap
	 * @param data
	 * @return 
	 */
	@Override
	protected boolean _fromSerializableMap(Map data) {
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
        
        this.setCmds(mcommands);
        
        return true;
    }
}