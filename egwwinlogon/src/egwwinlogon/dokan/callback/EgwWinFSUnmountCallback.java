/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.platform.win32.WinNT;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.UnmountCallback;

/**
 * EgwWinFSUnmountCallback
 * @author Stefan Werfling
 */
public class EgwWinFSUnmountCallback implements UnmountCallback, IEgwWinFSVolumeCallback {

	/**
	 * volume
	 */
	private EgwWinFSVolume _volume = null;
	
	/**
	 * setVolume
	 * @param volume 
	 */
	@Override
	public void setVolume(EgwWinFSVolume volume) {
		this._volume = volume;
	}
	
	/**
	 * invoke
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onUnmount(dokanFileInfo);
		}
		
		return WinNT.ERROR_SUCCESS;
	}
}
