/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.volume.multiresource;

import com.sun.jna.platform.win32.WinNT;
import egwwinlogon.dokan.EgwWinFsVirtualFile;
import egwwinlogon.dokan.volume.file.EgwWinFSFileCustomerDirStyle;

/**
 * EgwWinFSMultiResourceWebDavMount
 * @author Stefan Werfling
 */
public class EgwWinFSMultiResourceWebDavMount extends EgwWinFsVirtualFile {
	
	/**
	 * 
	 * @param name
	 * @param url
	 * @param username
	 * @param password 
	 */
	public EgwWinFSMultiResourceWebDavMount(String name, String url, String username, String password) {
		super(name, WinNT.FILE_ATTRIBUTE_DIRECTORY | WinNT.FILE_ATTRIBUTE_SYSTEM);
		
		this.putFile(new EgwWinFSFileCustomerDirStyle(EgwWinFSFileCustomerDirStyle.STYLE_EGW));
	}
	
	
}