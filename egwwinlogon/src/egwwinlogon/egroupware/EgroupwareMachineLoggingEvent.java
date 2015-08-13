/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import java.util.UUID;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * EgroupwareMachineLoggingEvent
 * 
 * @author Stefan Werfling
 */
public class EgroupwareMachineLoggingEvent {

    /**
     * unid
     */
    protected String _unid = "";
    
    /**
     * event
     */
    protected LoggingEvent _event = null;
    
    /**
     * index
     */
    protected long _index = 0;
    
    /**
     * constructor
     * 
     * @param event 
     * @param index
     */
    public EgroupwareMachineLoggingEvent(LoggingEvent event, long index) {
        this._unid = UUID.randomUUID().toString();
        this._event = event;
        this._index = index;
    }
    
    /**
     * getUnid
     * 
     * @return 
     */
    public String getUnid() {
        return this._unid;
    }
    
    /**
     * getEvent
     * @return 
     */
    public LoggingEvent getEvent() {
        return this._event;
    }
    
    /**
     * getIndex
     * 
     * @return 
     */
    public long getIndex() {
        return this._index;
    }
}