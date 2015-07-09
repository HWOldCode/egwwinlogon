/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * ZipFileAppender
 * @author Stefan Werfling
 */
public class ZipFileAppender implements Appender {
    
    /**
     * Layout
     */
    protected Layout _layout = null;
    
    /**
     * filename
     */
    protected String _fileName = null;

    /**
     * password
     */
    protected String _password = "";
    
    /**
     * zip file
     */
    protected ZipFile _zipFile = null;

    /**
     * Appender name
     */
    protected String _appender_name;
    
    /**
     * Appender Priority
     */
    protected Priority _appender_threshold;
    
    /**
     * Appender header filter
     */
    protected Filter _appender_headFilter;
    
    /**
     * Appender tail filter
     */
    protected Filter _appender_tailFilter;
    
    /**
     * Appender Error Handler
     */
    protected ErrorHandler _appender_errorHandler = new OnlyOnceErrorHandler();
    
    
    /**
     * constructor
     */
    public ZipFileAppender() {}

    /**
     * constructor
     * 
     * @param layout
     * @param filename
     * @param append
     * @param password
     * @param bufferedIO
     * @param bufferSize
     * @throws IOException 
     */
    public ZipFileAppender(Layout layout, String filename, String password) throws Exception 
    {
        this._layout = layout;
        this.setFile(filename, password);
    }
    
    /**
     * setFile
     * @param file 
     */
    public void setFile(String file) {
        String val = file.trim();
        this._fileName = val;
    }

    /**
     * getFile
     * @return 
     */
    public String getFile() {
        return this._fileName;
    }

    /**
     * closeFile
     */
    protected void _closeFile() {
        
    }

    /**
     * setFile
     * 
     * @param fileName
     * @param append
     * @param password
     * @param bufferedIO
     * @param bufferSize
     * @throws IOException 
     */
    public synchronized void setFile(String fileName, String password) throws IOException, ZipException 
    {
        this._zipFile = null;
        
        String zipFileName = fileName + ".zip";
        
        LogLog.debug("setFile called: " + zipFileName);

        // create dir 
        if( !(new File(zipFileName)).exists() ) {
            String parentName = new File(zipFileName).getParent();
          
            if( parentName != null ) {
                File parentDir = new File(parentName);
             
                if( !parentDir.exists() && parentDir.mkdirs() ) {
                    // IO
                } 
                else  {
                    throw new IOException("Can`t create dir.");
                }
            }
            else {
                throw new IOException("Can`t get parentname by file.");
            }
        }
        
        this._zipFile = new ZipFile(zipFileName);
    
        this._fileName = fileName;
        this._password = password;
        
        LogLog.debug("setFile ended");
    }

    /**
     * subAppend
     * @param event 
     */
    protected void subAppend(LoggingEvent event) {
        if( this._zipFile == null ) {
            return;
        }
        
        ZipParameters parameters = new ZipParameters();
        
        // compression level
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        
        // set encrypt
        if( !"".equals(this._password) ) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(this._password);
        }
        
        int rand = (int)(Math.random() * 10) + 1;
        
        parameters.setFileNameInZip(
            Long.toString(System.currentTimeMillis()) + "_" +
            Integer.toString(rand) + "_line.log");
        
        parameters.setSourceExternalStream(true);
        
        try {
            String strevent = this._layout.format(event);
            
            InputStream is = new ByteArrayInputStream(
                strevent.getBytes(StandardCharsets.UTF_8));
            
            this._zipFile.addStream(is, parameters);
            
            is.close();
        }
        catch( Exception e ) {
            // nothing
        }
    }
    
    /**
     * reset
     */
    protected void reset() {
        this._closeFile();
        this._fileName = null;
    }

    /**
     * addFilter
     * @param filter 
     */
    @Override
    public void addFilter(Filter filter) {
        if( this._appender_headFilter == null) {
            this._appender_headFilter = filter;
            this._appender_tailFilter = filter;
        } 
        else {
            this._appender_tailFilter.next = filter;
            this._appender_tailFilter = filter;
        }
    }

    /**
     * getFilter
     * @return 
     */
    @Override
    public Filter getFilter() {
        return this._appender_headFilter;
    }

    /**
     * clearFilters
     */
    @Override
    public void clearFilters() {
        this._appender_headFilter = null;
        this._appender_tailFilter = null;
    }

    /**
     * close
     */
    @Override
    public void close() {
        
    }

    /**
     * doAppend
     * @param le 
     */
    @Override
    public void doAppend(LoggingEvent le) {
        this.subAppend(le);
    }

    /**
     * getName
     * @return 
     */
    @Override
    public String getName() {
        return this._appender_name;
    }

    /**
     * setErrorHandler
     * @param eh 
     */
    @Override
    public void setErrorHandler(ErrorHandler eh) {
        if( eh == null ) {
            // TODO
        }
        else {
            this._appender_errorHandler = eh;
        }
    }

    /**
     * getErrorHandler
     * @return 
     */
    @Override
    public ErrorHandler getErrorHandler() {
        return this._appender_errorHandler;
    }

    /**
     * setLayout
     * @param layout 
     */
    @Override
    public void setLayout(Layout layout) {
        this._layout = layout;
    }

    /**
     * getLayout
     * @return 
     */
    @Override
    public Layout getLayout() {
        return this._layout;
    }

    /**
     * setName
     * @param name 
     */
    @Override
    public void setName(String name) {
        this._appender_name = name;
    }

    /**
     * requiresLayout
     * @return 
     */
    @Override
    public boolean requiresLayout() {
        return (this._layout != null ? true : false);
    }
}
