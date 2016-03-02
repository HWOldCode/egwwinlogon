/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.WString;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.DeleteDirectoryCallback;

/**
 * EgwWinFSDeleteDirectoryCallback
 * @author Stefan Werfling
 */
public class EgwWinFSDeleteDirectoryCallback implements DeleteDirectoryCallback, IEgwWinFSVolumeCallback {

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
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onDeleteDirectory(path, dokanFileInfo);
		}
		
		return 0;
	}
}
