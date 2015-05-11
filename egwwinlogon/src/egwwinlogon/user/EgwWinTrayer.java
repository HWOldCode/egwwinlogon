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
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * EgwWinTrayer
 * 
 * @author Stefan Werfling
 */
public class EgwWinTrayer implements EgroupwareEventListener, ActionListener {
    
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
        this._client = new EgwWinLogonClient();
        this._egw = this._client.getEgroupwareInstance(username, url);
        
        if( this._egw != null ) {
            this._egw.addListener(this);
        }
        
        this._trayer = new Trayer();
        this._trayer.setIconTooltip(this._trayerTitle + ": " + username);
        
        // set popup
        PopupMenu popup = new PopupMenu();
        popup.add(new MenuItem("About"));
        popup.addSeparator();
        popup.add(new MenuItem("Addressbook"));
        popup.add(new MenuItem("Calendar"));
        popup.add(new MenuItem("InfoLog"));
        popup.addSeparator();
        popup.add(new MenuItem("Logout"));
        popup.addSeparator();
        popup.add(new MenuItem("Close"));
        popup.addActionListener(this);
        
        this._trayer.setPopupMenu(popup);
        
        if( (this._egw != null) && this._egw.isLogin() ) {
            this._trayer.displayMsgInfo("Egroupware", "Benutzer ist eingelogt.");
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
                        EgroupwareBrowser.open(
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
}