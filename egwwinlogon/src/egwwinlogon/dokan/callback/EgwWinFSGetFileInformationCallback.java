/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.EgwWinFsVirtualFile;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanOperations.GetFileInformationCallback;
import egwwinlogon.dokan.lib.win.ByHandleFileInformation;

/**
 * EgwWinFSGetFileInformationCallback
 * @author Stefan Werfling
 */
public class EgwWinFSGetFileInformationCallback implements GetFileInformationCallback, IEgwWinFSVolumeCallback {

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
	 * @param info
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, ByHandleFileInformation info, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onGetFileInformation(path, info, dokanFileInfo);
		}
		
		EgwWinFsVirtualFile vf = new EgwWinFsVirtualFile("/", WinNT.FILE_ATTRIBUTE_DIRECTORY);
		
		if( vf.fillFileInfo(info) ) {
			dokanFileInfo.isDirectory = (byte)1;
			dokanFileInfo.context = vf.hashCode();
		}
		
		return WinNT.ERROR_SUCCESS;
	}
}
