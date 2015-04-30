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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
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
public class EgroupwareMachineLogging extends EgroupwareJson implements Appender {
   
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
    ArrayList<LoggingEvent> _appender_eventsList = new ArrayList();
    
    /**
     * Egroupwar Config by User (for sending report)
     */
    protected EgroupwareConfig _egwconfig = null;
    
    /**
     * UID
     */
    protected String _uid = "";
    
    /**
     * constructor
     * @param uid
     */
    public EgroupwareMachineLogging(String uid, EgroupwareConfig egwconfig) {
        super();

        this._uid = uid;
        this._egwconfig = egwconfig;
        
        this._request_url = this._createJsonMenuaction(
            EgroupwareMachineLogging.EGW_HTTP_GET_ML_ACTION);
    }
    
    /**
     * getPost
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> getPost() {
        Map<String, String> data = new HashMap<>();

        String tlogs = "";
        
        for( LoggingEvent le: this._appender_eventsList ) {
            if( tlogs.length() > 0 ) {
                tlogs = tlogs + ",";
            }
            
            tlogs = tlogs + "{" + 
                "\"event\": \"" + le.getLoggerName() + "\", " +
                "\"level\": \"" + le.getLevel().toString() + "\", " +
                "\"message\": \"" + le.getMessage() + "\", " +
                "\"logdate\": \"" + Long.toString(le.getTimeStamp()/1000) + "\" " +
                "}";
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

    @Override
    public Filter getFilter() {
        return this._appender_headFilter;
    }

    @Override
    public void clearFilters() {
        this._appender_headFilter = null;
        this._appender_tailFilter = null;
    }

    @Override
    public void close() {
    }

    @Override
    public void doAppend(LoggingEvent le) {
        this._appender_eventsList.add(le);
       
        ArrayList<LoggingEvent> tlist = 
            (ArrayList<LoggingEvent>) this._appender_eventsList.clone();
        
        try {
            // check can sending
            if( this._egwconfig != null ) {
                Egroupware _egw = Egroupware.getInstance(this._egwconfig);

                if( _egw != null ) {
                    if( _egw.isLogin() ) {
                        _egw.request(this);
                        
                        tlist.clear();
                        tlist = null;
                    }
                }
            }
        }
        catch( Exception ec ) {
            logger.error("EgroupwareMachineLogging: can`t send logging list");
            
            for( LoggingEvent tle: this._appender_eventsList ) {
                tlist.add(tle);
            }
            
            this._appender_eventsList = tlist;
        }
    }

    @Override
    public String getName() {
        return this._appender_name;
    }

    @Override
    public void setErrorHandler(ErrorHandler eh) {
        if( eh == null ) {
            // TODO
        }
        else {
            this._appender_errorHandler = eh;
        }
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this._appender_errorHandler;
    }

    @Override
    public void setLayout(Layout layout) {
        this._appender_layout = layout;
    }

    @Override
    public Layout getLayout() {
        return this._appender_layout;
    }

    @Override
    public void setName(String name) {
        this._appender_name = name;
    }

    @Override
    public boolean requiresLayout() {
        return (this._appender_layout != null ? true : false);
    }
}
