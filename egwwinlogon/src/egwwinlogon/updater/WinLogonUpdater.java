/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.updater;

import com.jegroupware.egroupware.core.SslUtil;
import egwwinlogon.service.EgwWinLogon;
import egwwinlogon.service.EgwWinLogonUltis;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;

/**
 * WinLogonUpdater
 * @see http://www.dreamincode.net/forums/topic/190944-creating-an-updater-in-java/
 * https://github.com/petesh/jna-mpr/blob/master/MprTest.java
 * @author Stefan Werfling
 */
public class WinLogonUpdater extends Thread {
    
    /**
     * request url
     */
    private final static String _versionURL = "https://www.hw-softwareentwicklung.de/egwwinlogon_updater/version.html";
    
    /**
     * dir for updater
     */
    private String _root = "update/";
    
    /**
     * version old
     */
    private String _oldVersion  = "";
    
    /**
     * version new
     */
    private String _newVersion  = "";
    
    /**
     * Dialog
     */
    private WinLogonUpdaterDialog _dlg = null;
    
	/**
	 * initSslCerts
	 */
	static public void initSslCerts() {
		try {
			SslUtil.ensureSslCertIsInKeystore(
				"thawte_primary_root_ca_v3", 
				ClassLoader.getSystemClassLoader().getResourceAsStream(
					"egwwinlogon/updater/thawte_primary_root_ca_v3.cer"));
			
			SslUtil.ensureSslCertIsInKeystore(
				"thawte_dv_ssl_ca_g2", 
				ClassLoader.getSystemClassLoader().getResourceAsStream(
					"egwwinlogon/updater/thawte_dv_ssl_ca_g2.cer"));
			
			
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
    /**
     * constructor
     */
    public WinLogonUpdater() {
		WinLogonUpdater.initSslCerts();
		
        this._dlg = new WinLogonUpdaterDialog();
        this.run();
    }

    /**
	 * main
	 * @param args String[]
	 */
	public static void main(String[] args) {
        WinLogonUpdater lu = new WinLogonUpdater();
    }
    
    /**
     * run
     */
    @Override
    public void run() {		
        try {
			File tmpFile = new File(System.getProperty("java.io.tmpdir") + "\\update.file"); 
			
			if( tmpFile.exists() ) {
				return;
			}
			else {
				tmpFile.createNewFile();
			}
			
			try {
				String decodedPath = EgwWinLogonUltis.getCurrentJarPath();
				
				this._root = decodedPath + this._root;
				
				String lastVersion  = WinLogonUpdater.getLatestVersion();
				EgwWinLogon wl      = new EgwWinLogon();
				
				if( !wl.egwGetVersion().equals(lastVersion) ) {
					this._newVersion = lastVersion;
					this._oldVersion = wl.egwGetVersion();
					
					this._dlg.showDialog();
					
					this._downloadFile(this._getDownloadLinkFromHost());
					this._unzip();
					this._copyFiles(
							new File(this._root),
							decodedPath//new File("").getAbsolutePath()
						);
					
					JOptionPane.showMessageDialog(null,
							"ELogin Update finish! Please Reboot your System.");
				}
				else {
					this._oldVersion = wl.egwGetVersion();
					this._newVersion = this._oldVersion;
				}
				
				if( this._dlg != null ) {
					Thread.sleep(250);
					this._dlg.close();
					System.exit(0);
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			
			tmpFile.delete();
		}
        catch (IOException ex) {
            Logger.getLogger(WinLogonUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * getLatestVersion
     * @return
     * @throws Exception 
     */
    static public String getLatestVersion() throws Exception {
        String data = WinLogonUpdater.getData(WinLogonUpdater._versionURL);
        return data.substring(data.indexOf("[version]")+9, data.indexOf("[/version]"));
    }
    
    /**
     * getWhatsNew
     * @return
     * @throws Exception 
     */
    static public String getWhatsNew() throws Exception {
        String data = WinLogonUpdater.getData(WinLogonUpdater._versionURL);
        return data.substring(data.indexOf("[history]")+9, data.indexOf("[/history]"));
    }
    
    /**
     * getData
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
        String data = WinLogonUpdater.getData(WinLogonUpdater._versionURL);
        return data.substring(data.indexOf("[url]")+5, data.indexOf("[/url]"));
    }
    
    /**
     * _getDownloadFileSize
     * @return
     * @throws Exception 
     */
    private String _getDownloadFileSize() throws Exception {
        String data = WinLogonUpdater.getData(WinLogonUpdater._versionURL);
        return data.substring(data.indexOf("[filesize]")+10, data.indexOf("[/filesize]"));
    }
    
    /**
     * _downloadFile
     * @param link
     * @throws MalformedURLException
     * @throws IOException 
     */
    private void _downloadFile(String link) throws MalformedURLException, IOException, Exception {
        URL url             = new URL(link);
        URLConnection conn  = url.openConnection();
        InputStream is      = conn.getInputStream();
        long max            = conn.getContentLength();
        int filesize        = Integer.parseInt(this._getDownloadFileSize());
        
        if( filesize == 0 ) {
            return;
        }
        
        BufferedOutputStream fOut = new BufferedOutputStream(
            new FileOutputStream(new File("update.zip")));
        
        byte[] buffer = new byte[32 * 1024];
        int bytesRead = 0;
        int in = 0;
        
        while( (bytesRead = is.read(buffer)) != -1 ) {
            in += bytesRead;
            fOut.write(buffer, 0, bytesRead);
            
            if( this._dlg != null ) {
                int percent = (in * 100 / filesize);
                this._dlg.setProgressBarValue(percent);
            }
        }
        
        fOut.flush();
        fOut.close();
        is.close();
        //outText.setText(outText.getText()+"\nDownload Complete!");
    }
    
    /**
     * _unzip
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
    
    /**
     * getJarDir
     * @param aclass
     * @return 
     */
    public static File getJarDir(Class aclass) {
        URL url;
        String extURL;      //  url.toExternalForm();

        // get an url
        try {
            url = aclass.getProtectionDomain().getCodeSource().getLocation();
        }
        catch (SecurityException ex) {
            url = aclass.getResource(aclass.getSimpleName() + ".class");
        }

        // convert to external form
        extURL = url.toExternalForm();
        
        // prune for various cases
        if( extURL.endsWith(".jar") ) {
            extURL = extURL.substring(0, extURL.lastIndexOf("/"));
        }
        else {  // from getResource
            String suffix = "/" + (aclass.getName()).replace(".", "/") + ".class";
            extURL = extURL.replace(suffix, "");
            
            if( extURL.startsWith("jar:") && extURL.endsWith(".jar!") ) {
                extURL = extURL.substring(4, extURL.lastIndexOf("/"));
            }
        }

        // convert back to url
        try {
            url = new URL(extURL);
        } catch (MalformedURLException mux) {
            // leave url unchanged; probably does not happen
        }

        // convert url to File
        try {
            return new File(url.toURI());
        } 
        catch( URISyntaxException ex ) {
            return new File(url.getPath());
        }
    }
}