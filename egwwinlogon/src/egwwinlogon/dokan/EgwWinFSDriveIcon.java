/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * EgwWinFSDriveIcon
 * https://github.com/opendedup/sdfs/blob/master/src/org/opendedup/sdfs/windows/utils/DriveIcon.java
 * @author Stefan Werfling
 */
public class EgwWinFSDriveIcon {
	
	/**
	 * addIcon
	 * @param drive
	 * @param icon
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException 
	 */
	public static void addIcon(String drive, String icon) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		/*drive = drive.toUpperCase();
		String drivekey = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\DriveIcons\\" +drive;
		String iconkey = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\DriveIcons\\" +drive+ "\\DefaultIcon";
		String deskey = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\DriveIcons\\" +drive+ "\\DefaultLabel";
		String iconpath = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\SDFS", "path") + File.separator+ "sdfs.ico";
		WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, drivekey);
		WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, iconkey);
		WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, deskey);
		WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, iconkey, "", iconpath);
		WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, deskey, "", "SDFS Deduplicated Volume");*/
	}
}
