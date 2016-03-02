/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.NativeLong;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.GetFileSecurityCallback;
import egwwinlogon.dokan.lib.win.SecurityDescriptor;

/**
 * EgwWinFSGetFileSecurityCallback
 * @author Stefan Werfling
 */
public class EgwWinFSGetFileSecurityCallback implements GetFileSecurityCallback, IEgwWinFSVolumeCallback {

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
	 * @param securityInfo
	 * @param securityDescriptor
	 * @param bufferLength
	 * @param lengthNeeded
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, IntByReference securityInfo, SecurityDescriptor securityDescriptor, NativeLong bufferLength, LongByReference lengthNeeded, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onGetFileSecurity(path, securityInfo, securityDescriptor, bufferLength, lengthNeeded, dokanFileInfo);
		}
		
		return 0;
	}
}
