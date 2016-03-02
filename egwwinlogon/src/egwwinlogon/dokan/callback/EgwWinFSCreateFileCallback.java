/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.CreateFileCallback;

/**
 * EgwWinFSCreateFileCallback
 * @author Stefan Werfling
 */
public class EgwWinFSCreateFileCallback implements CreateFileCallback, IEgwWinFSVolumeCallback {

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
	 * @param desiredAccess
	 * @param shareMode
	 * @param creationDisposition
	 * @param flagsAndAttributes
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, int desiredAccess, int shareMode, int creationDisposition, int flagsAndAttributes, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onCreateFile(path, desiredAccess, shareMode, creationDisposition, flagsAndAttributes, dokanFileInfo);
		}
		
		return WinNT.ERROR_SUCCESS;
	}
}
