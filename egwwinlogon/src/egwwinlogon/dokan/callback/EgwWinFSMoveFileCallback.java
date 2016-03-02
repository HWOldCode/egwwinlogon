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
import egwwinlogon.dokan.lib.DokanOperations.MoveFileCallback;

/**
 * EgwWinFSMoveFileCallback
 * @author Stefan Werfling
 */
public class EgwWinFSMoveFileCallback implements MoveFileCallback, IEgwWinFSVolumeCallback {

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
	 * @param existingPath
	 * @param newPath
	 * @param replaceExisting
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString existingPath, WString newPath, boolean replaceExisting, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onMoveFile(existingPath, newPath, replaceExisting, dokanFileInfo);
		}
		
		return 0;
	}
}
