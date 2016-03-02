/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.callback;

import com.sun.jna.WString;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.IEgwWinFSVolumeCallback;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanLibrary;
import egwwinlogon.dokan.lib.DokanOperations.FindFilesWithPatternCallback;

/**
 * EgwWinFSFindFilesWithPatternCallback
 * @author Stefan Werfling
 */
public class EgwWinFSFindFilesWithPatternCallback implements FindFilesWithPatternCallback, IEgwWinFSVolumeCallback {

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
	 * @param fileName
	 * @param searchPattern
	 * @param fillFindDataCallback
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int invoke(WString fileName, WString searchPattern, DokanLibrary.FillFindDataCallback fillFindDataCallback, DokanFileInfo dokanFileInfo) {
		if( this._volume != null ) {
			return this._volume.onFindFilesWithPattern(fileName, searchPattern, fillFindDataCallback, dokanFileInfo);
		}
		
		return 0;
	}
}
