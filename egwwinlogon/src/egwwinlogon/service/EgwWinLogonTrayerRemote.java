/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.service;

import egwwinlogon.winapi.ProcessList;
import egwwinlogon.winapi.ProcessList.ProcessInfo;

/**
 * EgwWinLogonTrayerRemote
 * @author Stefan Werfling
 */
public class EgwWinLogonTrayerRemote {
	
	/**
	 * _show
	 */
	protected Boolean _show = false;
	
	/**
	 * process id
	 */
	protected int _processId = -1;
	
	/**
	 * constructor
	 */
	public EgwWinLogonTrayerRemote() {
	}
	
	/**
	 * checkProcess
	 * @param sessionId 
	 */
	public void checkProcess(int sessionId) {
		if( this._processId != -1 ) {
			try {
				if( !ProcessList.existProcessById(this._processId) ) {
					this._processId = -1;
				}
			}
			catch( Exception ex ) {
				// TODO
			}
		}
		
		// ---------------------------------------------------------------------
		
		if( this._show ) {
			if( this._processId == -1 ) {
				String username = EgroupwarePGina.getUsername(sessionId);
				String cmdApp	= EgwWinLogonUltis.getUserAppCmd(username);
				
				this._processId = EgroupwarePGina.startUserProcessInSession(
					sessionId, cmdApp);
			}
		}
		else {
			if( this._processId != -1 ) {
				try {
					ProcessInfo pi = ProcessList.getProcessByPId(this._processId);
					
					if( pi != null ) {
						pi.terminate();
					}
				}
				catch( Exception ex ) {
					// TODO
				}
			}
		}
	}
	
	/**
	 * setShow
	 * @param show 
	 */
	public void setShow(Boolean show) {
		this._show = show;
	}
	
	/**
	 * getProcessId
	 * @return 
	 */
	public int getProcessId() {
		return this._processId;
	}
}