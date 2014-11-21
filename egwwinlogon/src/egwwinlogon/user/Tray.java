/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.user;

//import java.net.URL;
import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareBrowser;

import java.io.InputStream;
//import java.io.IOException;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.* ;
//import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
//import javax.swing.*;

/**
 *
 * @author test
 */
public class Tray implements MouseListener {
    
    static final String resource_path = "egwwinlogon/user/resources/";
    private SystemTray _system_tray;
    private TrayIcon _tray_icon;
    private Egroupware _egw;
    
    
    /**
     * load icon-file by image-name & return Image
     * 
     * @param image String
     * @return Image
     * @throws Exception 
     */
    public static Image getImage(String image) throws Exception {
        InputStream input_stream = ClassLoader.getSystemClassLoader().getResourceAsStream(Tray.resource_path + image);
        return ImageIO.read(input_stream);
    }

    
    public Tray(Egroupware egw) {
        // retrieve SystemTray instance by the factory
        if(SystemTray.isSupported()) {
            this._system_tray = SystemTray.getSystemTray();
            String image = "tileimage.png";
            Image image_icon;
            // icon
            try {
                // load image
                 image_icon = Tray.getImage("tileimage32.png");
                 
                 // popup menu
                 
                 
                 this._tray_icon = new TrayIcon(image_icon, "EGroupware");
                 
                 this._system_tray.add(this._tray_icon);
            }
            catch(Exception exception) {
                System.err.println(exception.getMessage());
            }
            
            this._egw = egw;
        }   
    }
    
    
    // ************************** MouseListener interface implementation ***
    public void mouseClicked(MouseEvent e) {
        //System.out.println(e.toString());
        try {
            EgroupwareBrowser.open(this._egw);
        }
        catch(Exception exception) {
            System.err.println(exception.getMessage());
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
