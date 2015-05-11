/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * WinLogonUpdater
 * @see http://www.dreamincode.net/forums/topic/190944-creating-an-updater-in-java/
 * @author Stefan Werfling
 */
public class WinLogonUpdater extends Thread {
    
    private final static String _versionURL     = "https://www.hw-softwareentwicklung.de/egwwinlogon_updater/version.html";
    private final static String _historyURL     = "https://www.hw-softwareentwicklung.de/egwwinlogon_updater/history.html";
    private final static String _downloadURL    = "https://www.hw-softwareentwicklung.de/egwwinlogon_updater/url.html";
    
    private final String _root                  = "update/";
    
    /**
     * constructor
     */
    public WinLogonUpdater() {
        this.start();
    }
    
    @Override
    public void run() {
        try {
            this._downloadFile(this._getDownloadLinkFromHost());
            this._unzip();
            this._copyFiles(
                new File(this._root), 
                new File("").getAbsolutePath()
                );
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * getLatestVersion
     * 
     * @return
     * @throws Exception 
     */
    static public String getLatestVersion() throws Exception {
        String data = WinLogonUpdater.getData(WinLogonUpdater._versionURL);
        return data.substring(data.indexOf("[version]")+9, data.indexOf("[/version]"));
    }
    
    /**
     * getWhatsNew
     * 
     * @return
     * @throws Exception 
     */
    static public String getWhatsNew() throws Exception {
        String data = WinLogonUpdater.getData(WinLogonUpdater._historyURL);
        return data.substring(data.indexOf("[history]")+9, data.indexOf("[/history]"));
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
        return data.substring(data.indexOf("[url]")+5, data.indexOf("[/url]"));
    }
    
    /**
     * _downloadFile
     * 
     * @param link
     * @throws MalformedURLException
     * @throws IOException 
     */
    private void _downloadFile(String link) throws MalformedURLException, IOException {
        URL url             = new URL(link);
        URLConnection conn  = url.openConnection();
        InputStream is      = conn.getInputStream();
        long max            = conn.getContentLength();
        //outText.setText(outText.getText()+"\n"+"Downloding file...\nUpdate Size(compressed): "+max+" Bytes");
        BufferedOutputStream fOut = new BufferedOutputStream(
            new FileOutputStream(new File("update.zip")));
        
        byte[] buffer = new byte[32 * 1024];
        int bytesRead = 0;
        int in = 0;
        
        while( (bytesRead = is.read(buffer)) != -1 ) {
            in += bytesRead;
            fOut.write(buffer, 0, bytesRead);
        }
        
        fOut.flush();
        fOut.close();
        is.close();
        //outText.setText(outText.getText()+"\nDownload Complete!");
    }
    
    /**
     * _unzip
     * 
     * @throws IOException 
     */
    protected void _unzip() throws IOException {
        int BUFFER = 2048;
         
        BufferedOutputStream dest = null;
        BufferedInputStream is = null;
         
        ZipEntry entry;
        ZipFile zipfile    = new ZipFile("update.zip");
        Enumeration e      = zipfile.entries();
         
        (new File(this._root)).mkdir();
         
        while(e.hasMoreElements()) {
            entry = (ZipEntry) e.nextElement();
            //outText.setText(outText.getText()+"\nExtracting: " +entry);
            
            if( entry.isDirectory() ) {
                (new File(this._root + entry.getName())).mkdir();
            }
            else {
                (new File(this._root + entry.getName())).createNewFile();
                
                is = new BufferedInputStream
                  (zipfile.getInputStream(entry));
                
                int count;
                byte data[] = new byte[BUFFER];
                
                FileOutputStream fos = new FileOutputStream(this._root + entry.getName());
                
                dest = new BufferedOutputStream(fos, BUFFER);
                
                while ((count = is.read(data, 0, BUFFER)) != -1) {
                   dest.write(data, 0, count);
                }
                
                dest.flush();
                dest.close();
                is.close();
            }
        }
    }

    /**
     * _copyFiles
     * 
     * @param f
     * @param dir
     * @throws IOException 
     */
    protected void _copyFiles(File f,String dir) throws IOException {
        File[]files = f.listFiles();
        
        for( File ff:files) {
            if( ff.isDirectory() ) {
                new File(dir + "/" + ff.getName()).mkdir();
                this._copyFiles(ff,dir+"/"+ff.getName());
            }
            else {
                this._copy(ff.getAbsolutePath(), dir + "/" + ff.getName());
            }
        }
    }
    
    /**
     * _copy
     * 
     * @param srFile
     * @param dtFile
     * @throws FileNotFoundException
     * @throws IOException 
     */
    protected void _copy(String srFile, String dtFile) throws FileNotFoundException, IOException{
        File f1 = new File(srFile);
        File f2 = new File(dtFile);

        InputStream in = new FileInputStream(f1);
        OutputStream out = new FileOutputStream(f2);

        byte[] buf = new byte[1024];
        int len;
        
        while ((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
        }
          
        in.close();
        out.close();
    }
    
    /**
     * _cleanup
     */
    private void _cleanup() {
        //outText.setText(outText.getText()+"\nPreforming clean up...");
        File f = new File("update.zip");
        
        f.delete();
        
        this._remove(new File(this._root));
        new File(this._root).delete();
    }

    /**
     * _remove
     * @param f 
     */
    protected void _remove(File f) {
        File[]files = f.listFiles();
        
        for( File ff:files ) {
            if( ff.isDirectory() ) {
                this._remove(ff);
                ff.delete();
            }
            else {
                ff.delete();
            }
        }
    }
}