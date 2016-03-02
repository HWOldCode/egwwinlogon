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
import egwwinlogon.dokan.lib.DokanLibrary;
import egwwinlogon.dokan.lib.DokanOperations.FindFilesCallback;
import egwwinlogon.dokan.lib.win.Win32FindData;
import egwwinlogon.winapi.ProcessList;
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
		
		/*EgwWinFsVirtualFile vf = new EgwWinFsVirtualFile("/", WinNT.FILE_ATTRIBUTE_DIRECTORY);
		
		vf.putFile(new EgwWinFsVirtualFile("public", WinNT.FILE_ATTRIBUTE_DIRECTORY));
		vf.putFile(new EgwWinFsVirtualFile("public2", WinNT.FILE_ATTRIBUTE_DIRECTORY));
		
		for( EgwWinFsVirtualFile file : vf.getFiles() ) {
            Win32FindData data = file.getWin32FindData();
            if( data != null ) {
                fillFindDataCallback.invoke(data, dokanFileInfo);
            }
        }
		
		if( dokanFileInfo != null ) {
			ProcessList.ProcessInfo process = ProcessList.getProcessByPId(dokanFileInfo.processId.intValue());
			System.out.println(process.getProcessExeFile());
			
			process.getProcessOwner();
		}*/
		
		return 0;
	}
}
