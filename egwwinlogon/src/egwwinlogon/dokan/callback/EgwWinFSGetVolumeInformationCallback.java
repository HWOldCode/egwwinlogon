/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.Pointer;
import static com.sun.jna.platform.win32.WinDef.MAX_PATH;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.GetVolumeInformationCallback;

/**
 * EgwWinFSGetVolumeInformationCallback
 * @author Stefan Werfling
 */
public class EgwWinFSGetVolumeInformationCallback implements GetVolumeInformationCallback, IEgwWinFSVolumeCallback {

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
	 * @param volumeNameBuffer
	 * @param volumeNameSize
	 * @param volumeSerialNumber
	 * @param maximumComponentLength
	 * @param fileSystemFlags
	 * @param fileSystemNameBuffer
	 * @param fileSystemNameSize
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(Pointer volumeNameBuffer, int volumeNameSize, IntByReference volumeSerialNumber, IntByReference maximumComponentLength, IntByReference fileSystemFlags, Pointer fileSystemNameBuffer, int fileSystemNameSize, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onGetVolumeInformation(volumeNameBuffer, volumeNameSize, volumeSerialNumber, maximumComponentLength, fileSystemFlags, fileSystemNameBuffer, fileSystemNameSize, dokanFileInfo);
		}
		
		volumeNameBuffer.setString(0, "EgwWinFs" + "\0", true);
		volumeSerialNumber.setValue(0xBEAF);
		maximumComponentLength.setValue(MAX_PATH);
		fileSystemFlags.setValue(WinNT.FILE_UNICODE_ON_DISK | WinNT.FILE_CASE_PRESERVED_NAMES);
		fileSystemNameBuffer.setString(0, "EgwWinFs" + "\0", true);
		
		return WinNT.ERROR_SUCCESS;
	}
}
