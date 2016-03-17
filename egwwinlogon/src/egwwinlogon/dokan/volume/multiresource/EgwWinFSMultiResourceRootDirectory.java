/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.volume.multiresource;

import com.sun.jna.platform.win32.WinNT;
import egwwinlogon.dokan.EgwWinFsVirtualFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EgwWinFSMultiResourceRootDirectory
 * @author Stefan Werfling
 */
public class EgwWinFSMultiResourceRootDirectory extends EgwWinFsVirtualFile {

	/**
	 * <Owner SID, <moutnname, Directory>>
	 */
	protected Map<String, Map<String, EgwWinFsVirtualFile>> _resources = new HashMap<>();
	
	/**
	 * constructor
	 */
	public EgwWinFSMultiResourceRootDirectory() {
		super("/", WinNT.FILE_ATTRIBUTE_DIRECTORY);
	}
	
	/**
	 * addOwnerSid
	 * @param sid
	 * @return 
	 */
	public boolean addOwnerSid(String sid) {
		if( this._resources.containsKey(sid) ) {
			return false;
		}
		
		this._resources.put(sid, new HashMap());
		
		return true;
	}
	
	/**
	 * addMount
	 * @param ownersid
	 * @param vfile
	 * @return 
	 */
	public boolean addMount(String ownersid, EgwWinFsVirtualFile vfile) {
		return this.addMount(ownersid, vfile.getName(), vfile);
	}
	
	/**
	 * addMount
	 * @param ownersid
	 * @param mountname
	 * @param vfile
	 * @return 
	 */
	public boolean addMount(String ownersid, String mountname, EgwWinFsVirtualFile vfile) {
		HashMap mounts = (HashMap) this._resources.get(ownersid);
		
		if( mounts != null ) {
			if( mounts.containsKey(mountname) ) {
				mounts.remove(mountname);
			}
			
			mounts.put(mountname, vfile);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * getFiles
	 * @param path
	 * @param ownersid
	 * @return 
	 */
	public List<EgwWinFsVirtualFile> getFiles(String path, String ownersid) {		
		return getFiles(this._parsePath(path), ownersid);
	}
	
	/**
	 * getFiles
	 * @param ownersid
	 * @return 
	 */
	public List<EgwWinFsVirtualFile> getFiles(String[] path, String ownersid) {
		HashMap mounts	= (HashMap) this._resources.get(ownersid);
		ArrayList _list = new ArrayList<>();
		
		if( mounts != null ) {
			for( Object value: mounts.values()) {
				EgwWinFsVirtualFile _vfile = (EgwWinFsVirtualFile)value;
				
				if( path.length == 0 ) {
					if( _vfile.isDirectory() ) {
						_list.add(_vfile);
					}
				}
				else if( _vfile.getName().equals(path[0]) ) {
					return _vfile.getFiles(Arrays.copyOfRange(path, 1, path.length));
				}
			}
		}
		else {
			System.out.println(ownersid);
		}
		
        return _list;
    }
	
	/**
	 * getFile
	 * @param path
	 * @param ownersid
	 * @return 
	 */
	public EgwWinFsVirtualFile getFile(String path, String ownersid) {
		return this.getFile(this._parsePath(path), ownersid);
	}
	
	/**
	 * getFile
	 * @param path
	 * @param ownersid
	 * @return 
	 */
	public EgwWinFsVirtualFile getFile(String[] path, String ownersid) {
		if( path.length == 0 ) {
			return this;
		}
		
		HashMap mounts	= (HashMap) this._resources.get(ownersid);
		
		if( mounts != null ) {
			if( path.length > 0 ) {
				if( mounts.containsKey(path[0]) ) {
					EgwWinFsVirtualFile cfile = (EgwWinFsVirtualFile) mounts.get(path[0]);
			
					if( cfile != null ) {
						if( path.length == 1 ) {
							return cfile;
						}
						else {
							if( cfile.isDirectory() ) {
								return cfile.getFile(Arrays.copyOfRange(path, 1, path.length));
							}
						}
					}
				}
			}
		}
		else {
			System.out.println(ownersid);
		}
		
		return null;
	}
	
	/**
	 * createFile
	 * 
	 * @param path
	 * @param flagsAndAttributes
	 * @param ownersid
	 * @return 
	 */
	public EgwWinFsVirtualFile createFile(String path, int flagsAndAttributes, String ownersid) {
		return this.createFile(this._parsePath(path), flagsAndAttributes, ownersid);
	}
	
	/**
	 * createFile
	 * 
	 * @param path
	 * @param flagsAndAttributes
	 * @param ownersid
	 * @return 
	 */
	public EgwWinFsVirtualFile createFile(String[] path, int flagsAndAttributes, String ownersid) {
		if( path.length == 0 ) {
			return this;
		}
		
		HashMap mounts	= (HashMap) this._resources.get(ownersid);
		
		if( mounts != null ) {
			if( path.length > 0 ) {
				if( mounts.containsKey(path[0]) ) {
					EgwWinFsVirtualFile cfile = (EgwWinFsVirtualFile) mounts.get(path[0]);
					
					if( cfile != null ) {
						if( path.length == 1 ) {
							return cfile;
						}
						else {
							return cfile.createFile(
								Arrays.copyOfRange(path, 1, path.length), 
								flagsAndAttributes
								);
						}
					}
				}
			}
		}
		
		return null;
	}
}