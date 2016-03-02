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
import egwwinlogon.dokan.lib.DokanOperations.OpenDirectoryCallback;
import egwwinlogon.winapi.ProcessList;

/**
 * EgwWinFSOpenDirectoryCallback
 * @author Stefan Werfling
 */
public class EgwWinFSOpenDirectoryCallback implements OpenDirectoryCallback, IEgwWinFSVolumeCallback {

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
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString path, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onOpenDirectory(path, dokanFileInfo);
		}
		
		if( dokanFileInfo != null ) {
			ProcessList.ProcessInfo process = ProcessList.getProcessByPId(dokanFileInfo.processId.intValue());
			System.out.println(process.getProcessExeFile());
			
			process.getProcessOwner();
		}
		
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}
}
