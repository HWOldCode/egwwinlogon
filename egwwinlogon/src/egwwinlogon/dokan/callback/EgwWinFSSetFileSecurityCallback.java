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
import egwwinlogon.dokan.lib.DokanOperations.SetFileSecurityCallback;
import egwwinlogon.dokan.lib.win.SecurityDescriptor;

/**
 * EgwWinFSSetFileSecurityCallback
 * @author Stefan Werfling
 */
public class EgwWinFSSetFileSecurityCallback implements SetFileSecurityCallback, IEgwWinFSVolumeCallback {

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
	 * @param pSecurityInformation
	 * @param securityDescriptor
	 * @param bufferLength
	 * @param lengthNeeded
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, IntByReference pSecurityInformation, SecurityDescriptor securityDescriptor, NativeLong bufferLength, LongByReference lengthNeeded, DokanFileInfo dokanFileInfo) {
		return 0;
	}
}
