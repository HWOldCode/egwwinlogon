/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.Egroupware;
import com.jegroupware.egroupware.EgroupwareConfig;
import com.jegroupware.egroupware.EgroupwareJson;
import com.jegroupware.egroupware.exceptions.EGroupwareExceptionRedirect;
import egwwinlogon.service.EgroupwarePGina;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * EgroupwareMachineLogging
 * @author Stefan Werfling
 */
public class EgroupwareMachineLogging extends EgroupwareJson implements Appender, Runnable {
   
    /**
     * menuaction
     */
    public static final String EGW_HTTP_GET_ML_ACTION = "elogin.elogin_machine_logging_ui.ajax_logging";
    
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(EgroupwareMachineLogging.class);
    
    /**
     * Appender Layout
     */
    protected Layout _appender_layout;
    
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
     * Appender EventsList
     */
    protected Map<String, EgroupwareMachineLoggingEvent> _appender_eventsList = new HashMap<String, EgroupwareMachineLoggingEvent>();
    
    /**
     * Appender EventsList uid index 
     */
    protected List<String> _appender_eventsList_uidindex = new ArrayList<String>();
    
    /**
     * timer for reqest
     */
    protected Timer _requestTimer = null;
    
    /**
     * Egroupwar Config by User (for sending report)
     */
    protected EgroupwareConfig _egwconfig = null;
    
    /**
     * UID
     */
    protected String _uid = "";
    
    /**
     * is send errror
     */
    protected Boolean _isSendError = false;
    
    /**
     * index
     */
    protected long _index = 0;
    
    /**
     * constructor
     * 
     * @param uid
     * @param egwconfig 
     */
    public EgroupwareMachineLogging(String uid, EgroupwareConfig egwconfig) {
        super();
        
        this._uid       = uid;
        this._egwconfig = egwconfig;
        
        this._request_url = this._createJsonMenuaction(
            EgroupwareMachineLogging.EGW_HTTP_GET_ML_ACTION);
        
        // init timer
        
        this._requestTimer = new Timer();
        
        MyTimer myTimer = new MyTimer();
        myTimer.setRunnable(this);
        
        this._requestTimer.schedule(myTimer, 1000, 60000);  // in 10 sec
    }
    
    /**
     * constructor
     * @param egwconfig
     */
    public EgroupwareMachineLogging(EgroupwareConfig egwconfig) {
        this(EgroupwarePGina.getSysFingerprint(), egwconfig);
    }
    
    /**
     * log
     * 
     * @param message
     * @param event
     * @param level 
     */
    public void log(String message, String event, String level) {
        LoggingEvent tevent = new LoggingEvent(
            event, 
            logger.getParent(), 
            Level.toLevel(level), 
            message, null);
        
        this.doAppend(tevent);
    }
    
    /**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();

        String tlogs = "";
        
        for( Map.Entry<String, EgroupwareMachineLoggingEvent> entry : this._appender_eventsList.entrySet() ) {
            EgroupwareMachineLoggingEvent ele = entry.getValue();
            
            // exist in index list
            if( this._appender_eventsList_uidindex.contains(ele.getUnid()) ) {
                continue;
            }
            
            LoggingEvent le = ele.getEvent();
            
            if( tlogs.length() > 0 ) {
                tlogs = tlogs + ",";
            }
            
            String tmsg = (String) le.getMessage();
            
            tlogs = tlogs + "{" + 
                "\"unid\": \"" + ele.getUnid() + "\", " +
                "\"index\": \"" + Long.toString(ele.getIndex()) + "\", " +
                "\"event\": \"" + le.getLoggerName() + "\", " +
                "\"level\": \"" + le.getLevel().toString() + "\", " +
                "\"message\": \"" + new String(Base64.encodeBase64(tmsg.getBytes())) + "\", " +
                "\"logdate\": \"" + Long.toString(le.getTimeStamp()/1000) + "\" " +
                "}";
            
            this._appender_eventsList_uidindex.add(ele.getUnid());
        }
        
        this._appender_eventsList.clear();
        
        data.put("json_data", "{\"request\":{\"parameters\":[" +
            "{\"uid\": \"" + this._uid + "\", \"loggings\": [" + 
            tlogs + "]}" +
            "]}}");

        return data;
    }
    
    /**
     * setRawContent
     * @param content
     */
    @Override
    public void setRawContent(String content) throws EGroupwareExceptionRedirect {
        super.setRawContent(content);

        if( this._json != null ) {
            // TODO
        }
    }

    /**
     * addFilter
     * 
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
     * 
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
     * 
     * @param le 
     */
    @Override
    public void doAppend(LoggingEvent le) {
        this._index++;
        
        EgroupwareMachineLoggingEvent elv = new EgroupwareMachineLoggingEvent(
            le, this._index);
        
        this._appender_eventsList.put(elv.getUnid(), elv);
    }

    /**
     * getName
     * 
     * @return 
     */
    @Override
    public String getName() {
        return this._appender_name;
    }

    /**
     * setErrorHandler
     * 
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
     * 
     * @return 
     */
    @Override
    public ErrorHandler getErrorHandler() {
        return this._appender_errorHandler;
    }

    /**
     * setLayout
     * 
     * @param layout 
     */
    @Override
    public void setLayout(Layout layout) {
        this._appender_layout = layout;
    }

    /**
     * getLayout
     * 
     * @return 
     */
    @Override
    public Layout getLayout() {
        return this._appender_layout;
    }

    /**
     * setName
     * 
     * @param name 
     */
    @Override
    public void setName(String name) {
        this._appender_name = name;
    }

    /**
     * requiresLayout
     * 
     * @return 
     */
    @Override
    public boolean requiresLayout() {
        return (this._appender_layout != null ? true : false);
    }

    /**
     * run
     */
    @Override
    public void run() {
        this._sendLogging();
    }
    
    /**
     * sendLogging
     * @return 
     */
    protected Boolean _sendLogging() {
        try {
            // check can sending
            if( this._egwconfig != null ) {
                Egroupware _egw = Egroupware.getInstance(this._egwconfig);

                if( _egw != null ) {
                    if( _egw.isLogin() ) {
                        _egw.request(this);
                        
                        // clear list
                        Iterator<String> iterator = this._appender_eventsList_uidindex.iterator();
                        
                        while( iterator.hasNext() ) {
                            String tuid = iterator.next();
                            
                            this._appender_eventsList.remove(tuid);
                            this._appender_eventsList_uidindex.remove(tuid);
                        }
             
                        this._isSendError = false;
                        
                        return true;
                    }
                }
            }
        }
        catch( Exception ec ) {
            if( !this._isSendError ) {
                this._index++;
                
                /*EgroupwareMachineLoggingEvent tmp = 
                    new EgroupwareMachineLoggingEvent(new LoggingEvent(
                        ec.getClass().getName(), 
                        logger.getParent(), 
                        Level.ERROR, 
                        "EgroupwareMachineLogging: can`t send logging list: " + 
                            ec.getMessage(), 
                        null
                        ), this._index);
                
                this._appender_eventsList.put(tmp.getUnid(), tmp);*/
                this._isSendError = true;
            }
        }
        
        this._appender_eventsList_uidindex.clear();
        
        return false;
    }
    
    /**
     * MyTimer
     */
    public static class MyTimer extends TimerTask {
            
            protected Runnable _runnable = null;
            
            public MyTimer() {
                super();
            }
            
            public void setRunnable(Runnable run) {
                this._runnable = run;
            }
            
            @Override
            public void run() {
                this._runnable.run();
            }
        };
}