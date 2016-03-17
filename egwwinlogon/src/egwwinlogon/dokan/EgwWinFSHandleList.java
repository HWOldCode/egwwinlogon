/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import egwwinlogon.dokan.lib.DokanFileInfo;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * EgwWinFSHandleList
 * @author Stefan Werfling
 */
public class EgwWinFSHandleList {
	
	protected ObjectArrayList<EgwWinFSHandleFile> _files = new ObjectArrayList<>();
	
	/**
	 * _getHandleFile
	 * @param filename
	 * @return 
	 */
	protected EgwWinFSHandleFile _getHandleFile(String filename) {
		for( EgwWinFSHandleFile e: this._files ) {
			if( e.equals(filename) ) {
				return e;
			}
		}
		
		return null;
	}
	
	/**
	 * createNewHandle
	 * @param filename
	 * @param dfi 
	 */
	public void createNewHandle(String filename, EgwWinFsVirtualFile vfile, DokanFileInfo dfi) {
		EgwWinFSHandleFile _file = this._getHandleFile(filename);
		
		if( _file == null ) {
			_file = new EgwWinFSHandleFile(filename);
			this._files.add(_file);
		}
		
		dfi.context = _file.getHandle().openHandle(vfile);
	}
	
	/**
	 * getVirtualFile
	 * @param filename
	 * @param dfi
	 * @return 
	 */
	public EgwWinFsVirtualFile getVirtualFile(String filename, DokanFileInfo dfi) {
		EgwWinFSHandleFile _file = this._getHandleFile(filename);
		
		if( _file != null ) {
			return _file.getHandle().getVirtualFile((int) (long)dfi.context);
		}
		
		return null;
	}

	/**
	 * closeHandle
	 * @param filename
	 * @param dfi 
	 */
	public void closeHandle(String filename, DokanFileInfo dfi) {
		EgwWinFSHandleFile _file = this._getHandleFile(filename);
		
		if( _file != null ) {
			_file.getHandle().closeHandle((int) (long)dfi.context);
		}
		
		dfi.context = 0;
	}
}
