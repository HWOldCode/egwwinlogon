/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.ReadFileCallback;

/**
 *
 * @author swe
 */
public class EgwWinFSReadFileCallback implements ReadFileCallback, IEgwWinFSVolumeCallback {

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
	 * @param numberOfBytesToRead
	 * @param numberOfBytesRead
	 * @param offset
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, Pointer buffer, int numberOfBytesToRead, IntByReference numberOfBytesRead, long offset, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onReadFile(path, buffer, numberOfBytesToRead, numberOfBytesRead, offset, dokanFileInfo);
		}
		
		return WinNT.ERROR_SUCCESS;
	}
}
