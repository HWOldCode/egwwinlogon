/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.volume.multiresource;

import com.sun.jna.platform.win32.WinNT;
import egwwinlogon.dokan.EgwWinFsVirtualFile;
import egwwinlogon.dokan.volume.file.EgwWinFSFileCustomerDirStyle;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * EgwWinFSMultiResourceLocalMount
 * @author Stefan Werfling
 */
public class EgwWinFSMultiResourceLocalMount extends EgwWinFsVirtualFile {

	/**
	 * local path
	 */
	protected String _localPath = "";
	
	/**
	 * 
	 * @param path
	 * @param name 
	 */
	public EgwWinFSMultiResourceLocalMount(String path, String name) {
		super(name, WinNT.FILE_ATTRIBUTE_DIRECTORY | WinNT.FILE_ATTRIBUTE_SYSTEM);
		this._localPath = path;
		
		this.putFile(new EgwWinFSFileCustomerDirStyle(EgwWinFSFileCustomerDirStyle.STYLE_DRIVE));
	}
	
	/**
	 * 
	 * @param path
	 * @param name
	 * @param flagsAndAttributes 
	 */
	public EgwWinFSMultiResourceLocalMount(String path, String name, int flagsAndAttributes) {
		super(name, flagsAndAttributes);
		this._localPath = path;
	}
	
	/**
	 * 
	 * @param localFile 
	 */
	public EgwWinFSMultiResourceLocalMount(File localFile) {
		super(localFile.getName(), 0);
		this._localPath = localFile.getPath();
	}
	
	/**
	 * getFiles
	 * @return 
	 */
	@Override
	public List<EgwWinFsVirtualFile> getFiles(String[] path) {
		if( path.length > 0 ) {
			File tfile = new File(this._localPath + path[0]);
			
			if( tfile.exists() ) {
				EgwWinFSMultiResourceLocalMount tlm = new EgwWinFSMultiResourceLocalMount(tfile);
				
				return tlm.getFiles(Arrays.copyOfRange(path, 1, path.length));
			}
		}
		else {
			File tfile = new File(this._localPath);
			String[] filelist = tfile.list();
			
			List<EgwWinFsVirtualFile>  list = super.getFiles(new String[]{});
			
			if( filelist != null ) {
				for( String nfile: filelist ) {
					list.add(new EgwWinFSMultiResourceLocalMount(new File(this._localPath + "/" + nfile)));
				}
			}
			
			return list;
		}
		
		return Collections.emptyList();
	}
}
