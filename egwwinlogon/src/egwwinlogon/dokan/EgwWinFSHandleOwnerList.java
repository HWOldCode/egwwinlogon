/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * EgwWinFSHandleOwnerList
 * @author Stefan Werfling
 */
public class EgwWinFSHandleOwnerList {
	
	/**
	 * handle owners
	 */
	protected ObjectArrayList<EgwWinFSHandleOwner> _handleOwners = new ObjectArrayList<>();
	
	/**
	 * 
	 */
	public EgwWinFSHandleOwnerList() {
		
	}
	
	/**
	 * addOwner
	 * @param sid 
	 */
	public void addOwner(String sid) {
		this._handleOwners.add(new EgwWinFSHandleOwner(sid));
	}
	
	/**
	 * getHandleList
	 * @param ownerSid
	 * @return 
	 */
	public EgwWinFSHandleList getHandleList(String ownerSid) {
		for( EgwWinFSHandleOwner e: this._handleOwners ) {
			if( e.equals(ownerSid) ) {
				return e.getHandleList();
			}
		}
		
		return null;
	}
}
