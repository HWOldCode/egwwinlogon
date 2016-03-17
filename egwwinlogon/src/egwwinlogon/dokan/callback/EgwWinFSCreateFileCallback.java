/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanIOSecurityContext;
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
	 * @param securityContext
	 * @param desiredAccess
	 * @param fileAttributes
	 * @param shareAccess
	 * @param createDisposition
	 * @param createOptions
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, IntByReference securityContext, int desiredAccess, int fileAttributes, int shareAccess, int createDisposition, int createOptions, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onCreateFile(path, securityContext, desiredAccess, fileAttributes, shareAccess, createDisposition, createOptions, dokanFileInfo);
		}
		
		return WinNT.ERROR_SUCCESS;
	}
}
