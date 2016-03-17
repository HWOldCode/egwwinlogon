/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

/**
 * EgwWinFSHandleFile
 * @author Stefan Werfling
 */
public class EgwWinFSHandleFile {
	
	/**
	 * filename
	 */
	protected String _filename = "";
	
	/**
	 * handle
	 */
	protected EgwWinFSHandle _handle = new EgwWinFSHandle();
	
	/**
	 * 
	 * @param filename 
	 */
	public EgwWinFSHandleFile(String filename) {
		this._filename = filename;
	}
	
	/**
	 * equals
	 * @param filename
	 * @return 
	 */
	public Boolean equals(String filename) {
		return this._filename.equals(filename);
	}
	
	/**
	 * getHandle
	 * @return 
	 */
	public EgwWinFSHandle getHandle() {
		return this._handle;
	}
}
