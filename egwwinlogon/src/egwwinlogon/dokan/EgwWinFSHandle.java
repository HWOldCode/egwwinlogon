/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * EgwWinFSHandle
 * @author Stefan Werfling
 */
public class EgwWinFSHandle {
	
	/**
	 * Handle Index
	 */
	static protected ObjectArrayList<Integer> _handleIndex = new ObjectArrayList<>();
	
	/**
	 * _getFreeHandle
	 * @return 
	 */
	static protected Integer _getFreeHandle() {
		Integer tmp = Long.hashCode(System.currentTimeMillis());
		
		while( _handleIndex.indexOf(tmp) > 0 ) {
			tmp = Long.hashCode(System.currentTimeMillis());
		}
		
		_handleIndex.add(tmp);
		
		return tmp;
	}
	
	/**
	 * _removeHandle
	 * @param fp
	 */
	static protected void _removeHandle(Integer fp) {
		Integer _index = _handleIndex.indexOf(fp);
		
		if( _index > 0 ) {
			_handleIndex.remove((int)_index);
		}
	}
	
	// -------------------------------------------------------------------------

	/**
	 * file pointers
	 */
	protected ObjectArrayList<Integer> _filePointer = new ObjectArrayList<>();
	
	/**
	 * virtual files
	 */
	protected ObjectArrayList<EgwWinFsVirtualFile> _vfiles = new ObjectArrayList<>();
	
	/**
	 * openHandle
	 * @param vfile
	 * @return 
	 */
	public Integer openHandle(EgwWinFsVirtualFile vfile) {
		Integer fp = _getFreeHandle();
		
		this._filePointer.add(fp);
		this._vfiles.add(vfile);
		
		return fp;
	}
	
	/**
	 * getVirtualFile
	 * @param fp
	 * @return 
	 */
	public EgwWinFsVirtualFile getVirtualFile(Integer fp) {
		Integer _index = this._filePointer.indexOf(fp);
		
		if( _index > -1 ) {
			return this._vfiles.get((int)_index);
		}
		
		return null;
	}

	/**
	 * closeHandle
	 * @param fp 
	 */
	public void closeHandle(Integer fp) {
		Integer _index = this._filePointer.indexOf(fp);
		
		if( _index > -1 ) {
			this._filePointer.remove((int)_index);
			this._vfiles.remove((int)_index);
			_removeHandle(fp);
		}
	}
}
