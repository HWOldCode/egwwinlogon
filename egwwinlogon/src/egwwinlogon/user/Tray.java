/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

import java.awt.* ;

/**
 *
 * @author test
 */
public class Tray {
    
    private SystemTray _system_tray;
    private TrayIcon _tray_icon;
    //private Image _tray_icon_image;
    
    public Tray() {
        // retrieve SystemTray instance by the factory
        if(SystemTray.isSupported()) {        
            this._system_tray = SystemTray.getSystemTray();
        }

        // load image
        
        
    }
}
