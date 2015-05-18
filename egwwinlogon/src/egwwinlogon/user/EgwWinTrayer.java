/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareBrowser;
import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.dialogs.EgroupwareMainWebDialog;
import com.jegroupware.egroupware.events.EgroupwareAuthentifiactionEvent;
import com.jegroupware.egroupware.events.EgroupwareEvent;
import com.jegroupware.egroupware.events.EgroupwareEventListener;
import com.jegroupware.egroupware.events.EgroupwareEventRequest;
import com.jegroupware.egroupware.events.EgroupwareLogoutEvent;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import egwwinlogon.egroupware.EgroupwareCommand;
import egwwinlogon.egroupware.EgroupwareELoginBrowser;
import egwwinlogon.egroupware.EgroupwareMachineLogging;
import egwwinlogon.service.EgroupwareDLL;
import egwwinlogon.winapi.mpr.MprHelper;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TreeMap;
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
     * Egroupware
     */
    protected Egroupware _egw = null;
    
    /**
     * Trayer
     */
    protected Trayer _trayer = null;
    
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
        
        this._client        = new EgwWinLogonClient();
        this._egw           = this._client.getEgroupwareInstance(username, url);
        this._machine_id    = this._client.getMachineId(url);
        
        if( this._egw != null ) {
            this._egw.addListener(this);
        }
        
        this._trayer = new Trayer();
        this._trayer.addMouseListener(this);
        this._trayer.setIconTooltip(this._trayerTitle + ": " + username);
        
        // set popup
        PopupMenu popup = new PopupMenu();
        popup.add(new MenuItem("About"));
        popup.addSeparator();
        popup.add(new MenuItem("Logout"));
        popup.addSeparator();
        popup.add(new MenuItem("Close"));
        popup.addActionListener(this);
        
        this._trayer.setPopupMenu(popup);
        
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
                
                // -------------------------------------------------------------
                /*EgroupwareCommand cmds = new EgroupwareCommand(
                    this._machine_id, 
                    EgroupwareCommand.EGW_CMD_TYPE_USER);

                try {
                    this._egw.request(cmds);
                    cmds.execute();
                }
                catch( Exception e ) {
                    this._trayer.displayMsgError("Egroupware", e.getMessage());
                }*/
                
                //int r = MprHelper.mountW("\\\\192.168.0.252\\video", "I:", "megasave", "1234");
                //int r=0;
                //logger.info("Mount-Return: " + Integer.toString(r));
            }
            else {
                this._trayer.displayMsgError("Egroupware", "System ist unbekannt!");
            }
        }
        else {
            this._trayer.displayMsgInfo("Egroupware", "Offline modus, kein Internet?");
        }
    }

    @Override
    public void authentificationSucceeded(EgroupwareAuthentifiactionEvent e) {
        
    }

    @Override
    public void authentificationFailed(EgroupwareAuthentifiactionEvent e) {
        
    }

    @Override
    public void logoutSucceeded(EgroupwareLogoutEvent e) {
        
    }

    @Override
    public void logoutFailed(EgroupwareLogoutEvent e) {
        
    }

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

    @Override
    public void requestFailed(EgroupwareEventRequest e) {
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() instanceof PopupMenu ) {
            String egwurl = null;
            
            if( e.getActionCommand() == "About" ) {
                
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
            }
            
            // egw browser start
            if( egwurl != null ) {
                if( this._egw != null ) {
                    try {
                        EgroupwareELoginBrowser.open(
                            this._egw, 
                            egwurl, 
                            ""
                            );
                        
                        this._trayer.displayMsgInfo(
                            this._trayerTitle, 
                            "Start Browser to Egroupware-App " + e.getActionCommand()
                            );
                    }
                    catch( Exception ex ) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
    }

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
            (e.getClickCount() == 2) ) 
        {
            try {
                if( this._egw.isLogin() ) {
                    EgroupwareELoginBrowser.open(this._egw);
                }
            }
            catch( Exception ex ) {
                logger.error("mouseClicked: " + ex.getMessage());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}