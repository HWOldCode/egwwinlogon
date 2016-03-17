/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.WriteFileCallback;

/**
 * EgwWinFSWriteFileCallback
 * @author Stefan Werfling
 */
public class EgwWinFSWriteFileCallback implements WriteFileCallback, IEgwWinFSVolumeCallback {

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
	 * @param buffer
	 * @param numberOfBytesToWrite
	 * @param numberOfBytesWritten
	 * @param offset
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, Pointer buffer, int numberOfBytesToWrite, IntByReference numberOfBytesWritten, long offset, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onWriteFile(path, buffer, numberOfBytesToWrite, numberOfBytesWritten, offset, dokanFileInfo);
		}
		
		return WinNT.ERROR_SUCCESS;
	}
}
