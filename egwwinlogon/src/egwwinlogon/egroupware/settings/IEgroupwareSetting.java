/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware.settings;

import java.util.Map;

/**
 * IEgroupwareSetting
 * @author Stefan Werfling
 */
public interface IEgroupwareSetting {
    
    /**
     * getClassname
     * @return 
     */
    public String getClassname();
    
    /**
     * getName
     * @return 
     */
    public String getName();
    
    /**
     * getDescription
     * @return 
     */
    public String getDescription();
    
    /**
     * getNeedSettingFields
     * @return 
     */
    public Map<String, String> getNeedSettingFields();
    
    /**
     * setSetting
     * @return 
     */
    public Boolean setSetting();
}
