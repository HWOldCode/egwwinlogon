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
import egwwinlogon.dokan.lib.DokanLibrary;
import egwwinlogon.dokan.lib.DokanOperations.FindFilesCallback;
import java.io.UnsupportedEncodingException;

/**
 * EgwWinFSFindFilesCallback
 * @author Stefan Werfling
 */
public class EgwWinFSFindFilesCallback implements FindFilesCallback, IEgwWinFSVolumeCallback {

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
	 * @param fillFindDataCallback
	 * @param dokanFileInfo
	 * @return 
	 * @throws java.io.UnsupportedEncodingException 
	 */
	@Override
	public int invoke(WString path, DokanLibrary.FillFindDataCallback fillFindDataCallback, DokanFileInfo dokanFileInfo) throws UnsupportedEncodingException {
		if( this._volume != null ) {
			return this._volume.onFindFiles(path, fillFindDataCallback, dokanFileInfo);
		}
	
		return WinNT.ERROR_SUCCESS;
	}
}
