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
import egwwinlogon.dokan.lib.DokanOperations.UnlockFileCallback;

/**
 * EgwWinFSUnlockFileCallback
 * @author Stefan Werfling
 */
public class EgwWinFSUnlockFileCallback implements UnlockFileCallback, IEgwWinFSVolumeCallback {

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
	 * @param byteOffset
	 * @param length
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, long byteOffset, long length, DokanFileInfo dokanFileInfo) {
		return 0;
	}
}
