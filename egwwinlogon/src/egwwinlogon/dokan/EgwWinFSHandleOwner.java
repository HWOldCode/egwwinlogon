/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

/**
 * EgwWinFSHandleOwner
 * @author Stefan Werfling
 */
public class EgwWinFSHandleOwner {
	
	/**
	 * owner sid
	 */
	protected String _ownerSid = "";
	
	/**
	 * handles
	 */
	protected EgwWinFSHandleList _handles = new EgwWinFSHandleList();
	
	/**
	 * 
	 * @param ownersid 
	 */
	public EgwWinFSHandleOwner(String ownersid) {
		this._ownerSid = ownersid;
	}
	
	/**
	 * getOwnerSid
	 * @return 
	 */
	public String getOwnerSid() {
		return this._ownerSid;
	}
	
	/**
	 * equals
	 * @param ownerSid
	 * @return 
	 */
	public Boolean equals(String ownerSid) {
		return this._ownerSid.equals(ownerSid);
	}
	
	/**
	 * getHandleList
	 * @return 
	 */
	public EgwWinFSHandleList getHandleList() {
		return this._handles;
	}
}
