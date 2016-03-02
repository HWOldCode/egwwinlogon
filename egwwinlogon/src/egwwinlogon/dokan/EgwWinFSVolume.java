/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanLibrary;
import egwwinlogon.dokan.lib.win.ByHandleFileInformation;
import egwwinlogon.dokan.lib.win.SecurityDescriptor;

/**
 * EgwWinFSVolume
 * @author Stefan Werfling
 */
abstract public class EgwWinFSVolume {
	
	/**
	 * onCleanup
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onCleanup(WString path, DokanFileInfo dokanFileInfo) {
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}
	
	/**
	 * onOpenDirectory
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onOpenDirectory(WString path, DokanFileInfo dokanFileInfo) {
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}

	/**
	 * onCreateDirectory
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onCreateDirectory(WString path, DokanFileInfo dokanFileInfo) {
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}

	/**
	 * onCreateFile
	 * @param path
	 * @param desiredAccess
	 * @param shareMode
	 * @param creationDisposition
	 * @param flagsAndAttributes
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onCreateFile(WString path, int desiredAccess, int shareMode, int creationDisposition, int flagsAndAttributes, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onCloseFile
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onCloseFile(WString path, DokanFileInfo dokanFileInfo) {
		return -WinNT.ERROR_ACCESS_DENIED;
	}

	/**
	 * onDeleteDirectory
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onDeleteDirectory(WString path, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onDeleteFile
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onDeleteFile(WString path, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onFindFiles
	 * @param path
	 * @param fillFindDataCallback
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onFindFiles(WString path, DokanLibrary.FillFindDataCallback fillFindDataCallback, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onFindFilesWithPattern
	 * @param fileName
	 * @param searchPattern
	 * @param fillFindDataCallback
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onFindFilesWithPattern(WString fileName, WString searchPattern, DokanLibrary.FillFindDataCallback fillFindDataCallback, DokanFileInfo dokanFileInfo) {
		return 0;
	}

	/**
	 * onFlushFileBuffers
	 * @param path
	 * @param dokanFileInfo 
	 */
	public void onFlushFileBuffers(WString path, DokanFileInfo dokanFileInfo) {
	}

	/**
	 * onGetDiskFreeSpace
	 * @param freeBytesAvailable
	 * @param totalNumbersOfBytes
	 * @param totalNumberOfFreeBytes
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onGetDiskFreeSpace(LongByReference freeBytesAvailable, LongByReference totalNumbersOfBytes, LongByReference totalNumberOfFreeBytes, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onGetFileInformation
	 * @param path
	 * @param info
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onGetFileInformation(WString path, ByHandleFileInformation info, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onGetFileSecurity
	 * @param path
	 * @param securityInfo
	 * @param securityDescriptor
	 * @param bufferLength
	 * @param lengthNeeded
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onGetFileSecurity(WString path, IntByReference securityInfo, SecurityDescriptor securityDescriptor, NativeLong bufferLength, LongByReference lengthNeeded, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onGetVolumeInformation
	 * @param volumeNameBuffer
	 * @param volumeNameSize
	 * @param volumeSerialNumber
	 * @param maximumComponentLength
	 * @param fileSystemFlags
	 * @param fileSystemNameBuffer
	 * @param fileSystemNameSize
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onGetVolumeInformation(Pointer volumeNameBuffer, int volumeNameSize, IntByReference volumeSerialNumber, IntByReference maximumComponentLength, IntByReference fileSystemFlags, Pointer fileSystemNameBuffer, int fileSystemNameSize, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onLockFile
	 * @param path
	 * @param byteOffset
	 * @param length
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onLockFile(WString path, long byteOffset, long length, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onMount
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onMount(DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onMoveFile
	 * @param existingPath
	 * @param newPath
	 * @param replaceExisting
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onMoveFile(WString existingPath, WString newPath, boolean replaceExisting, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onReadFile
	 * @param path
	 * @param buffer
	 * @param numberOfBytesToRead
	 * @param numberOfBytesRead
	 * @param offset
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onReadFile(WString path, Pointer buffer, int numberOfBytesToRead, IntByReference numberOfBytesRead, long offset, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onSetAllocationSize
	 * @param path
	 * @param length
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onSetAllocationSize(WString path, long length, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onSetEndOfFile
	 * @param path
	 * @param length
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onSetEndOfFile(WString path, long length, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onSetFileAttributes
	 * @param path
	 * @param fileAttributes
	 * @param dokanFileInfo
	 * @return 
	 */
	public int onSetFileAttributes(WString path, int fileAttributes, DokanFileInfo dokanFileInfo) {
		return WinNT.ERROR_SUCCESS;
	}
}