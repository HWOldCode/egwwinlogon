/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

/**
 * IEgwWinFSVolumeCallback
 * @author Stefan Werfling
 */
public interface IEgwWinFSVolumeCallback {
	
	/**
	 * setVolume
	 * @param volume 
	 */
	public void setVolume(EgwWinFSVolume volume);
}
