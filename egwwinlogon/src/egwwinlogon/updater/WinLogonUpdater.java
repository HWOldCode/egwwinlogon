/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.updater;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * WinLogonUpdater
 * @see http://www.dreamincode.net/forums/topic/190944-creating-an-updater-in-java/
 * @author Stefan Werfling
 */
public class WinLogonUpdater {
    
    private final static String _versionURL     = "https://www.hw-softwareentwicklung.de/egwwinlogon_updater/version.html";
    private final static String _historyURL     = "https://www.hw-softwareentwicklung.de/egwwinlogon_updater/history.html";
    private final static String _downloadURL    = "https://www.hw-softwareentwicklung.de/egwwinlogon_updater/url.html";
    
    public WinLogonUpdater() {
        Thread worker = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    //this._downloadFile(this._getDownloadLinkFromHost());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        worker.start();
    }
    
    /**
     * getLatestVersion
     * 
     * @return
     * @throws Exception 
     */
    static public String getLatestVersion() throws Exception {
        String data = WinLogonUpdater.getData(WinLogonUpdater._versionURL);
        return data.substring(data.indexOf("[version]")+9,data.indexOf("[/version]"));
    }
    
    /**
     * getWhatsNew
     * 
     * @return
     * @throws Exception 
     */
    static public String getWhatsNew() throws Exception {
        String data = WinLogonUpdater.getData(WinLogonUpdater._historyURL);
        return data.substring(data.indexOf("[history]")+9,data.indexOf("[/history]"));
    }
    
    /**
     * getData
     * 
     * @param address
     * @return
     * @throws Exception 
     */
    static private String getData(String address)throws Exception {
        int c               = 0;
        URL url             = new URL(address);
        InputStream html    = url.openStream();
        StringBuffer buffer = new StringBuffer("");

        while( c != -1 ) {
            c = html.read();    
            buffer.append((char)c);
        }
        
        return buffer.toString();
    }
    
    /**
     * _getDownloadLinkFromHost
     * @return
     * @throws Exception 
     */
    private String _getDownloadLinkFromHost() throws Exception {
        String data = WinLogonUpdater.getData(WinLogonUpdater._downloadURL);
        return data.substring(data.indexOf("[url]")+5,data.indexOf("[/url]"));
    }
    
    private void _downloadFile(String link) throws MalformedURLException, IOException {
        URL url             = new URL(link);
        URLConnection conn  = url.openConnection();
        InputStream is      = conn.getInputStream();
        long max            = conn.getContentLength();
        //outText.setText(outText.getText()+"\n"+"Downloding file...\nUpdate Size(compressed): "+max+" Bytes");
        BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(new File("update.zip")));
        byte[] buffer = new byte[32 * 1024];
        int bytesRead = 0;
        int in = 0;
        while ((bytesRead = is.read(buffer)) != -1) {
            in += bytesRead;
            fOut.write(buffer, 0, bytesRead);
        }
        fOut.flush();
        fOut.close();
        is.close();
        //outText.setText(outText.getText()+"\nDownload Complete!");

    }
}
