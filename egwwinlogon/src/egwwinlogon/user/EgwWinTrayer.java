/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.events.EgroupwareAuthentifiactionEvent;
import com.jegroupware.egroupware.events.EgroupwareEvent;
import com.jegroupware.egroupware.events.EgroupwareEventListener;
import com.jegroupware.egroupware.events.EgroupwareEventRequest;
import com.jegroupware.egroupware.events.EgroupwareLogoutEvent;
import com.sun.jna.platform.win32.Advapi32Util;
import egwwinlogon.egroupware.EgroupwareELoginBrowser;
import egwwinlogon.egroupware.EgroupwareMachineLogging;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * EgwWinTrayer
 * 
 * @author Stefan Werfling
 */
public class EgwWinTrayer implements EgroupwareEventListener, ActionListener, MouseListener {
    
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgwWinTrayer.class);
    
    /**
     * EgwWinLogonClient
     */
    protected EgwWinLogonClient _client = null;
    
    /**
     * username
     */
    protected String _username = "";
    
    /**
     * URL
     */
    protected String _url = null;
    
    /**
     * Egroupware
     */
    protected Egroupware _egw = null;
    
    /**
     * Trayer
     */
    protected Trayer _trayer = null;
    
	/**
	 * PopupMenu for trayer
	 */
	protected PopupMenu _pmenu = null;
	
    /**
     * Machine id
     */
    protected String _machine_id = null;
    
    /**
     * trayer title
     */
    protected String _trayerTitle = "EGroupware WinLogon";
    
    /**
     * main
     * @param args String[]
     */
    public static void main(String[] args) {
        if( (args.length > 0) && (args[0] != "") ) {
            // init
            new EgwWinTrayer(args[0]);
        }
        else {
            String currentUserName = Advapi32Util.getUserName();
            new EgwWinTrayer(currentUserName);
        }
    }
    
    /**
     * constructor
     * 
     * @param username 
     */
    public EgwWinTrayer(String username) {
        this(username, null);
    }
    
    /**
     * constructor
     * 
     * @param url
     * @param username 
     */
    public EgwWinTrayer(String username, String url) {
        this._username = username;
        this._url = url;
        
        this._client        = new EgwWinLogonClient();
        this._egw           = this._client.getEgroupwareInstance(username, url);
        this._machine_id    = this._client.getMachineId(url);
        
        // egroupware instance
        this.reloadEgroupwareInstance();

        // ---------------------------------------------------------------------
        
        if( this._egw != null ) {
            this._egw.addListener(this);
        }
        
        this._trayer = new Trayer();
        this._trayer.addMouseListener(this);
        this._trayer.setIconTooltip(this._trayerTitle + ": " + username);
        
        // set popup
        this._pmenu = new PopupMenu();
		
		MenuItem egwstart = new MenuItem("Start EGroupware");
		
		egwstart.setActionCommand("intern:egw");
		
        this._pmenu.add(egwstart);
        this._pmenu.addSeparator();
        
		LinkedList commandList = this._client.getCommands(username, url);
		
		for( int d=0; d<commandList.size(); d++ ) {
			LinkedHashMap ccmd = (LinkedHashMap) commandList.get(d);
			
			if( ccmd.containsKey("name") ) {
				String cmdname = (String) ccmd.get("name");
				MenuItem pmenucmd = new MenuItem(cmdname);
				
				pmenucmd.setActionCommand("cmd:" + cmdname);
				
				this._pmenu.add(pmenucmd);
			}
		}
		
        this._pmenu.addActionListener(this);
        
        this._trayer.setPopupMenu(this._pmenu);
        
        if( (this._egw != null) && this._egw.isLogin() ) {
            this._trayer.displayMsgInfo("Egroupware", "Benutzer ist eingeloggt.");
            
            if( this._machine_id != null ) {
                
                // -------------------------------------------------------------
                EgroupwareMachineLogging egwlog = new EgroupwareMachineLogging(
                        this._machine_id,
                        this._egw.getConfig()
                        );
                    
                // set logger
                Logger tlogger = Logger.getRootLogger();
                tlogger.addAppender(egwlog);
                
                // -------------------------------------------------------------
                
                logger.info("Start EgwWinLogin Trayer ...");
            }
            else {
                this._trayer.displayMsgError("EGroupware", "System ist unbekannt!");
            }
        }
        else {
            this._trayer.displayMsgInfo("EGroupware", "Offline modus, kein Internet?");
        }
    }
    
    /**
     * reloadEgroupwareInstance
     * Thread check is egroupware offline to online
     */
    public void reloadEgroupwareInstance() {
        if( (this._egw == null) || ((this._egw != null) && !this._egw.isLogin()) ) {
            Thread thread = new Thread(){
                
                @Override
                public void run(){
                    while( true ) {
                        _egw = _client.getEgroupwareInstance(_username, _url);
                        
                        if( (_egw != null) && (_egw.isLogin()) ) {
                            if( _trayer != null ) {
                                _trayer.displayMsgInfo(
                                    "Egroupware", 
                                    "Benutzer ist eingeloggt.");
                            }
                            
                            break;
                        }
						/*else {
							if( _trayer != null ) {
								_trayer.displayMsgInfo("EGroupware", "Offline modus, kein Internet?");
							}
						}*/
                        
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(
                                EgwWinTrayer.class.getName()).log(
                                    Level.SEVERE, null, ex);
                        }
                    }
                }
            };
            
            thread.start();
        }
    }

    /**
     * authentificationSucceeded
     * 
     * @param e 
     */
    @Override
    public void authentificationSucceeded(EgroupwareAuthentifiactionEvent e) {
        
    }

    /**
     * authentificationFailed
     * 
     * @param e 
     */
    @Override
    public void authentificationFailed(EgroupwareAuthentifiactionEvent e) {
        
    }

    /**
     * logoutSucceeded
     * 
     * @param e 
     */
    @Override
    public void logoutSucceeded(EgroupwareLogoutEvent e) {
        
    }

    /**
     * logoutFailed
     * 
     * @param e 
     */
    @Override
    public void logoutFailed(EgroupwareLogoutEvent e) {
        
    }

    /**
     * requestSucceeded
     * 
     * @param e 
     */
    @Override
    public void requestSucceeded(EgroupwareEventRequest e) {
        if( e.getRequest() instanceof EgroupwareJson ) {
            EgroupwareJson ejson = (EgroupwareJson) e.getRequest();

            if( this._trayer != null ) {
                //this._trayer.displayMsgError("Trayer", ejson.getRequestUrl());
            }
            
            System.out.println("Trayer: " + ejson.getRequestUrl());
        }
    }

    /**
     * requestFailed
     * 
     * @param e 
     */
    @Override
    public void requestFailed(EgroupwareEventRequest e) {
        
    }

    /**
     * actionPerformed
     * 
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() instanceof PopupMenu ) {
			String[] acmd = e.getActionCommand().split(":");
			String egwurl = null;
			
			if( acmd.length == 2 ) {
				if( "intern".equals(acmd[0]) ) {
					
					if( "egw".equals(acmd[1]) ) {
						egwurl = "menuaction=infolog.infolog_ui.index";
					}
					else {
						return;
					}
					// TODO
				}
				else if( "cmd".equals(acmd[0]) ) {
					this._client.execCommand(this._username, acmd[1], this._url);
					return;
				}
			}
			else {
				return;
			}
			
            /*if( e.getActionCommand() == "Start EGroupware" ) {
                egwurl = "menuaction=infolog.infolog_ui.index";
            }
            else if( e.getActionCommand() == "Addressbook" ) {
                egwurl = "menuaction=addressbook.addressbook_ui.index";
            }
            else if( e.getActionCommand() == "Calendar" ) {
                egwurl = "menuaction=calendar.calendar_uiviews.index";
            }
            else if( e.getActionCommand() == "InfoLog" ) {
                egwurl = "menuaction=infolog.infolog_ui.index";
            }
            else if( e.getActionCommand() == "Close" ) {
                System.exit(1);
            }*/
            
            // egw browser start
            if( egwurl != null ) {
                if( (this._egw != null) && (this._egw.isLogin()) ) {
                    try {
                        EgroupwareELoginBrowser.open(
                            this._egw, 
                            egwurl, 
                            ""
                            );
                        
                        this._trayer.displayMsgInfo(
                            this._trayerTitle, 
                            "Start EGroupware " + e.getActionCommand()
                            );
                    }
                    catch( Exception ex ) {
                        System.out.println(ex.getMessage());
                    }
                }
				else {
					this._trayer.displayMsgInfo("EGroupware", 
						"Offline modus, kein Internet?");
				}
            }
        }
    }

    /**
     * threadAction
     * 
     * @param e 
     */
    @Override
    public void threadAction(EgroupwareEvent e) {
        
    }

    /**
     * mouseClicked
     * @param e 
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if( (e.getButton() == MouseEvent.BUTTON1) && 
            (e.getClickCount() >= 2) ) 
        {
            try {
                if( this._egw.isLogin() ) {
                    this._trayer.displayMsgInfo(
                        this._trayerTitle, 
                        "Start Browser, please wait..."
                        );
                    
                    EgroupwareELoginBrowser.open(this._egw);
                }
                else {
                    this.reloadEgroupwareInstance();
                    
                    this._trayer.displayMsgError("Error", 
                        "Egroupware is offline, please wait for reaload egroupware instance!");
                }
            }
            catch( Exception ex ) {
                logger.error("mouseClicked: " + ex.getMessage());
            }
        }
		else if( e.getButton() == MouseEvent.BUTTON1 ) {
			this._pmenu.show(e.getComponent(), e.getXOnScreen(), e.getYOnScreen());
		}
    }

    /**
     * mousePressed
     * 
     * @param e 
     */
    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    /**
     * mouseReleased
     * 
     * @param e 
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    /**
     * mouseEntered
     * 
     * @param e 
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    /**
     * mouseExited
     * 
     * @param e 
     */
    @Override
    public void mouseExited(MouseEvent e) {
        
    }
}