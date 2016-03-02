/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.LongByReference;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.GetDiskFreeSpaceCallback;

/**
 * EgwWinFSGetDiskFreeSpaceCallback
 * @author Stefan Werfling
 */
public class EgwWinFSGetDiskFreeSpaceCallback implements GetDiskFreeSpaceCallback, IEgwWinFSVolumeCallback {

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
	 * @param freeBytesAvailable
	 * @param totalNumbersOfBytes
	 * @param totalNumberOfFreeBytes
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(LongByReference freeBytesAvailable, LongByReference totalNumbersOfBytes, LongByReference totalNumberOfFreeBytes, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onGetDiskFreeSpace(freeBytesAvailable, totalNumbersOfBytes, totalNumberOfFreeBytes, dokanFileInfo);
		}
		
		freeBytesAvailable.setValue(100000);
		totalNumbersOfBytes.setValue(100000);
		totalNumberOfFreeBytes.setValue(100000);
		
		return WinNT.ERROR_SUCCESS;
	}
}
