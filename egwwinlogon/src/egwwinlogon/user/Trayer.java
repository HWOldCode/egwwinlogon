/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseListener;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Trayer
 * 
 * @author Stefan Werfling
 */
public class Trayer {

    /**
     * SystemTray
     */
    protected SystemTray _systemtray = null;
    
    /**
     * TrayIcon
     */
    protected TrayIcon _trayicon = null;
    
    /**
     * constructor
     */
    public Trayer() {
        if( SystemTray.isSupported() ) {
            this._systemtray = SystemTray.getSystemTray();
            
            try {
                Image imageicon = Trayer.getImage(
                    "egwwinlogon/user/resources/tileimage128.png", 
                    this._systemtray);
                
                this._trayicon = new TrayIcon(imageicon);
                this._systemtray.add(this._trayicon);
            }
            catch( Exception e ) {
                
            }
        }
    }
    
    /**
     * setIconTooltip
     * 
     * @param msg 
     */
    public void setIconTooltip(String msg) {
        if( this._systemtray != null ) {
            this._trayicon.setToolTip(msg);
        }
    }
    
    /**
     * addMouseListener
     * @param listener 
     */
    public void addMouseListener(MouseListener listener) {
        if( this._systemtray != null ) {
            this._trayicon.addMouseListener(listener);
        }
    }
    
    /**
     * displayMsgError
     * 
     * @param title
     * @param msg 
     */
    public void displayMsgError(String title, String msg) {
        if( this._trayicon != null ) {
            this._trayicon.displayMessage(
                title, 
                msg, 
                TrayIcon.MessageType.ERROR);
        }
    }
    
    /**
     * displayMsgError
     * 
     * @param title
     * @param msg 
     */
    public void displayMsgInfo(String title, String msg) {
        if( this._trayicon != null ) {
            this._trayicon.displayMessage(
                title, 
                msg, 
                TrayIcon.MessageType.INFO);
        }
    }
    
    /**
     * setPopupMenu
     * 
     * @param popup 
     */
    public void setPopupMenu(PopupMenu popup) {
        if( this._trayicon != null ) {
            this._trayicon.setPopupMenu(popup);
        }
    }
    
    /**
     * getImage
     * 
     * @param filename
     * @param systemtray
     * @return
     * @throws Exception 
     */
    protected static Image getImage(String filename, SystemTray systemtray) throws Exception {
        InputStream input_stream = 
            ClassLoader.getSystemClassLoader().getResourceAsStream(
                filename);
        
        Image image = ImageIO.read(input_stream);
        Dimension dimension = systemtray.getTrayIconSize();
        
        Image scaledimage = image.getScaledInstance(
            dimension.width, 
            dimension.height, 
            Image.SCALE_SMOOTH
            );
        
        return scaledimage;
    }
}