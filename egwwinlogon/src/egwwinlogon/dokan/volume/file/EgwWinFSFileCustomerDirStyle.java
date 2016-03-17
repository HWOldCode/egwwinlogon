/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.volume.file;

import com.sun.jna.platform.win32.WinNT;
import egwwinlogon.dokan.EgwWinFsVirtualFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EgwWinFSFileCustomerDirStyle
 * @author Stefan Werfling
 */
public class EgwWinFSFileCustomerDirStyle extends EgwWinFsVirtualFile {

	public static final int STYLE_NONE			= 0;
	public static final int STYLE_DRIVE			= 1;
	public static final int STYLE_NETDRIVE		= 2;
	public static final int STYLE_EGW			= 3;
	public static final int STYLE_FIRST_APRIL	= 99;
	
	/**
	 * dir icon file
	 */
	private EgwWinFsVirtualFile _dirIconFile = null;
	
	/**
	 * EgwWinFSFileCustomerDirStyle
	 * @param name
	 * @param flagsAndAttributes 
	 */
	public EgwWinFSFileCustomerDirStyle(int style) {
		super("desktop.ini",
			WinNT.FILE_ATTRIBUTE_HIDDEN |
			WinNT.FILE_ATTRIBUTE_SYSTEM);
		
		if( this._isFirstApril() ) {
			style = EgwWinFSFileCustomerDirStyle.STYLE_FIRST_APRIL;
		}
		
		switch( style ) {
			case EgwWinFSFileCustomerDirStyle.STYLE_DRIVE:
				this.setContent("[.ShellClassInfo]\r\nIconResource=C:\\Windows\\system32\\SHELL32.dll,8\r\n");
				break;
				
			case EgwWinFSFileCustomerDirStyle.STYLE_NETDRIVE:
				this.setContent("[.ShellClassInfo]\r\nIconResource=C:\\Windows\\system32\\SHELL32.dll,9\r\n");
				break;
				
			case EgwWinFSFileCustomerDirStyle.STYLE_EGW:
				this.setDirIconFile("egwwinlogon/user/resources/tileimage128.ico");
				this.setContent("[.ShellClassInfo]\r\nIconResource=tileimage128.ico,0\r\n");
				break;
				
			case EgwWinFSFileCustomerDirStyle.STYLE_FIRST_APRIL:
				this.setDirIconFile("egwwinlogon/user/resources/firstapril.ico");
				this.setContent("[.ShellClassInfo]\r\nIconResource=firstapril.ico,0\r\n");
				break;
		}
	}
	
	/**
	 * _isFirstApril
	 * @return 
	 */
	private boolean _isFirstApril() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM"); 
        Date currentTime = new Date();
		
		if( "01.04".equals(formatter.format(currentTime)) ) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * setDirIconFile
	 * @param icon 
	 */
	public void setDirIconFile(String icon) {
		this._dirIconFile = new EgwWinFSFileResourceStream(icon);
	}
	
	/**
	 * setParent
	 * @param parent 
	 */
	protected void setParent(EgwWinFsVirtualFile parent) {
		super.setParent(parent);
		
		if( this._dirIconFile != null ) {
			parent.putFile(this._dirIconFile);
		}
	}
}
