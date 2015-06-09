/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.updater;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * WinLogonUpdaterDialog
 * @author Stefan Werfling
 */
public class WinLogonUpdaterDialog {
    
    /**
     * JFrame
     */
    protected JFrame _parentFrame = null;
    
    protected int _size = 500;
    
    protected JProgressBar _dpb = null;
    
    /**
     * WinLogonUpdaterDialog
     */
    public WinLogonUpdaterDialog() {
    }

    /**
     * showDialog
     */
    public void showDialog() {
        this._parentFrame = new JFrame();
        this._parentFrame.setSize(this._size, 50);
        this._parentFrame.setTitle("ELogin Updater");
        
        try {
            InputStream input_stream = 
                ClassLoader.getSystemClassLoader().getResourceAsStream(
                    "egwwinlogon/user/resources/tileimage128.png");

            Image image = ImageIO.read(input_stream);

            this._parentFrame.setIconImage(image);
        }
        catch( Exception e ) {
            
        }
        
        this._dpb = new JProgressBar(0, this._size);
        this._parentFrame.add(BorderLayout.CENTER, this._dpb);
        this._parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this._parentFrame.setVisible(true);
    }
    
    /**
     * setProgressBarValue
     * @param value 
     */
    public void setProgressBarValue(int value) {
        if( this._dpb != null ) {
            int set = value * this._size / 100;
            this._dpb.setValue(set);
        }
    }
    
    public void close() {
        if( this._parentFrame != null ) {
            this._parentFrame.setVisible(false);
            this._parentFrame.dispose();
        }
    }
}