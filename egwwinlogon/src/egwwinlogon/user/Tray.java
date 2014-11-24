/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

//import java.net.URL;
import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.jegroupware.egroupware.EgroupwareBrowser;
import egwwinlogon.service.EgwWinLogon;

import java.io.InputStream;
//import java.io.IOException;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.* ;
//import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import javax.swing.*;

/**
 *
 * @author test
 */
public class Tray implements MouseListener {
    
    private static final Logger logger = LoggerFactory.getLogger(EgwWinLogon.class);
    
    static final String resource_path = "egwwinlogon/user/resources/";
    protected SystemTray _system_tray;
    protected TrayIcon _tray_icon;
    protected Egroupware _egw;
    
    
    /**
     * load icon-file by image-name & return Image
     * 
     * @param file_name String
     * @param system_tray SystemTray
     * @return Image
     * @throws Exception 
     */
    public static Image getImage(String file_name, SystemTray system_tray) throws Exception {
        InputStream input_stream = ClassLoader.getSystemClassLoader().getResourceAsStream(Tray.resource_path + file_name);
        Image image = ImageIO.read(input_stream);
        Dimension dimension = system_tray.getTrayIconSize();   
        Image scaled_image = image.getScaledInstance(dimension.width, dimension.height, Image.SCALE_REPLICATE);
        return scaled_image;
    }

    
    public Tray(Egroupware egw) {
        this._egw = egw;
        // retrieve SystemTray instance by the factory
        if(SystemTray.isSupported()) {
            this._system_tray = SystemTray.getSystemTray();

            // icon
            try {
                // load image
                  Image image_icon = Tray.getImage("tileimage128.png", this._system_tray);
                 
                 // popup menu
                
                // tooltip
                String tooltip;
                EgroupwareConfig egw_config = this._egw.getConfig();
                
                if(this._egw.isLogin()) {
                    tooltip = "Eingeloggt als " + egw_config.getUser();
                }
                else {
                    tooltip = egw_config.getUser() + " ist nicht angemeldet";
                }
                
                
                 
                this._tray_icon = new TrayIcon(image_icon, tooltip);
                 
                //this._tray_icon.setImageAutoSize(true);
                 
                this._system_tray.add(this._tray_icon);
                this._tray_icon.addMouseListener(this);
            }
            catch(Exception exception) {
                logger.error(exception.getMessage());
            }
        }   
    }
    
    
    // ************************** MouseListener interface implementation ***
    public void mouseClicked(MouseEvent e) {
        int button = e.getButton();
        int count = e.getClickCount();
        
        if((button == MouseEvent.BUTTON1) && (count == 2)) {
       
            try {
                EgroupwareBrowser.open(this._egw);
            }
            catch(Exception exception) {
                logger.error(exception.getMessage());
            }
        }
    }
    
    
    
    
    
    
    
    public void mouseEntered(MouseEvent e) {

    }
    
    
    public void mouseExited(MouseEvent e) {

    }
    
    
    public void mousePressed(MouseEvent e) {

    }
    
    
    public void mouseReleased(MouseEvent e) {

    }
}
