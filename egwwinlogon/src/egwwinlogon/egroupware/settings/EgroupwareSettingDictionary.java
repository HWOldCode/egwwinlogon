/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * EgroupwareSettingDictionary
 * 
 * @author Stefan Werfling
 */
public class EgroupwareSettingDictionary {
   
    /**
     * _dictionary
     */
    static private List<IEgroupwareSetting> _dictionary = new ArrayList<IEgroupwareSetting>();
    
    /**
     * register
     * @param setting 
     */
    static public void register(IEgroupwareSetting setting) {
        EgroupwareSettingDictionary._dictionary.add(setting);
    }
    
    /**
     * getSupportedSettings
     * 
     * @return 
     */
    static public List<String> getSupportedSettings() {
        List<String> _list = new ArrayList<String>();
        
        for( IEgroupwareSetting tsetting: EgroupwareSettingDictionary._dictionary ) {
            _list.add(tsetting.getName());
        }
        
        return _list;
    }
    
    /**
     * getSetting
     * 
     * @param name
     * @return 
     */
    static public IEgroupwareSetting getSetting(String name) {
        for( IEgroupwareSetting tsetting: EgroupwareSettingDictionary._dictionary ) {
            if( name.equals(tsetting.getName()) ) {
                return tsetting;
            }
        }
        
        return null;
    }
}