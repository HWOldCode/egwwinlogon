/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.egroupware;

import com.jegroupware.egroupware.EgroupwareJson;
import egwwinlogon.service.EgroupwarePGina;
import egwwinlogon.service.EgwWinLogon;
import egwwinlogon.service.EgwWinLogonUltis;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * EgroupwareCacheList
 * @author Stefan Werfling
 */
abstract public class EgroupwareCacheList extends EgroupwareJson {
	
	/**
	 * _toSerializableString
	 * @return 
	 */
	protected String _toSerializableString() {
		return "{}";
	}
	
	/**
	 * _fromSerializableMap
	 * @param data
	 * @return 
	 */
	protected boolean _fromSerializableMap(Map data) {
		return false;
	}
	
	/**
	 * _fromSerializableString
	 * @param serialize
	 * @return
	 * @throws IOException 
	 */
	static protected EgroupwareCacheList _fromSerializableString(String serialize) throws IOException {
		JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory(){
                public List creatArrayContainer() {
                    return new LinkedList();
                }

                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }
            };

        Map data = null;

        try {
            data = (Map)parser.parse(serialize.trim(), containerFactory);
        } catch( ParseException ex ) {
            Logger.getLogger(
                EgroupwareELoginCache.class.getName()).log(
                    Level.SEVERE,
                    null,
                    ex);

            return null;
        }
		
		if( data.containsKey("classname") ) {
			try {
				EgroupwareCacheList clist = EgroupwareCacheList._objectFromString((String) data.get("classname"));
				
				if( clist._fromSerializableMap(data) ) {
					return clist;
				}
			}
			catch( ClassNotFoundException ex ) {
				Logger.getLogger(EgroupwareCacheList.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch( InstantiationException ex ) {
				Logger.getLogger(EgroupwareCacheList.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch( IllegalAccessException ex ) {
				Logger.getLogger(EgroupwareCacheList.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		return null;
	}
	
	/**
	 * _objectFromString
	 * @param classname
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException 
	 */
	static protected EgroupwareCacheList _objectFromString(String classname) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Object list = Class.forName(classname).newInstance();
		
		if( list instanceof EgroupwareCacheList ) {
			return (EgroupwareCacheList) list;
		}
		
		return null;
	}
	
	/**
     * saveToFile
	 * @param cache
     * @param file
     * @return
	 * @throws java.lang.Exception
     */
    static public Boolean saveToFile(EgroupwareCacheList cache, String file) throws Exception {
        try {
            String content = cache._toSerializableString();
            
            content = EgwWinLogonUltis.getStrEncode(
                content,
                EgroupwarePGina.getDLLHash() + EgroupwarePGina.getSysFingerprint()
                );
            
            Files.write(Paths.get(file), content.getBytes());
        }
        catch( InvalidKeyException exik ) {
            throw new Exception(
                "Wrong configuration, used base JavaCE, please contact your Administrator!");
        }
        catch( Exception ex ) {
            EgroupwarePGina.logError(
                cache.getClass().getName() + ".saveToFile:" + ex.getMessage() + 
                " File: " + file
                );
            
            java.util.logging.Logger.getLogger(
                EgroupwareCommand.class.getName()).log(Level.SEVERE, null, ex);
            
            return false;
        }

        return true;
    }
	/**
     * loadByFile
     * // https://www.tutorials.de/threads/linkedlist-speichern.186639/
	 * 
     * @param file
     * @return
     */
    static public EgroupwareCacheList loadByFile(String file) throws Exception {
        try {
            String content = new String(Files.readAllBytes(Paths.get(file)));
            
            content = EgwWinLogonUltis.getStrDecode(
                content,
                EgroupwarePGina.getDLLHash() + EgroupwarePGina.getSysFingerprint()
                );
            
            //EgroupwarePGina.logInfo("loadByFile Cache: " + content);
            
            return EgroupwareCacheList._fromSerializableString(content);
        }
        catch( InvalidKeyException exik ) {
            throw new Exception(
                "Wrong configuration, used base JavaCE, please contact your Administrator!");
        }
        catch( Exception ex ) {
            EgroupwarePGina.logError(
                "EgroupwareCacheList.loadByFile:" + ex.getMessage() + 
                " File: " + file
                );
            
            java.util.logging.Logger.getLogger(
                EgwWinLogon.class.getName()).log(Level.SEVERE, null, ex);
            
            return null;
        }
    }
}
