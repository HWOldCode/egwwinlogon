/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import com.sun.jna.NativeLong;
import com.sun.jna.WString;
import egwwinlogon.dokan.callback.EgwWinFSCleanupCallback;
import egwwinlogon.dokan.callback.EgwWinFSCloseFileCallback;
import egwwinlogon.dokan.callback.EgwWinFSCreateDirectoryCallback;
import egwwinlogon.dokan.callback.EgwWinFSCreateFileCallback;
import egwwinlogon.dokan.callback.EgwWinFSDeleteDirectoryCallback;
import egwwinlogon.dokan.callback.EgwWinFSDeleteFileCallback;
import egwwinlogon.dokan.callback.EgwWinFSFindFilesCallback;
import egwwinlogon.dokan.callback.EgwWinFSFindFilesWithPatternCallback;
import egwwinlogon.dokan.callback.EgwWinFSFlushFileBuffersCallback;
import egwwinlogon.dokan.callback.EgwWinFSGetDiskFreeSpaceCallback;
import egwwinlogon.dokan.callback.EgwWinFSGetFileInformationCallback;
import egwwinlogon.dokan.callback.EgwWinFSGetFileSecurityCallback;
import egwwinlogon.dokan.callback.EgwWinFSGetVolumeInformationCallback;
import egwwinlogon.dokan.callback.EgwWinFSLockFileCallback;
import egwwinlogon.dokan.callback.EgwWinFSMountCallback;
import egwwinlogon.dokan.callback.EgwWinFSMoveFileCallback;
import egwwinlogon.dokan.callback.EgwWinFSOpenDirectoryCallback;
import egwwinlogon.dokan.callback.EgwWinFSReadFileCallback;
import egwwinlogon.dokan.callback.EgwWinFSSetAllocationSizeCallback;
import egwwinlogon.dokan.callback.EgwWinFSSetEndOfFileCallback;
import egwwinlogon.dokan.callback.EgwWinFSSetFileAttributesCallback;
import egwwinlogon.dokan.callback.EgwWinFSSetFileSecurityCallback;
import egwwinlogon.dokan.callback.EgwWinFSSetFileTimeCallback;
import egwwinlogon.dokan.callback.EgwWinFSUnlockFileCallback;
import egwwinlogon.dokan.callback.EgwWinFSUnmountCallback;
import egwwinlogon.dokan.callback.EgwWinFSWriteFileCallback;
import egwwinlogon.dokan.lib.DokanLibrary;
import egwwinlogon.dokan.lib.DokanOperations;
import egwwinlogon.dokan.lib.DokanOptions;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * EgwWinFS
 * @author Stefan Werfling
 */
public class EgwWinFS extends DokanOperations {
	
	/**
	 * dokan options
	 */
	private DokanOptions _options = null;

	/**
	 * Volume
	 */
	private EgwWinFSVolume _volume = null;
	
	/**
	 * EgwWinFS
	 */
	public EgwWinFS() {
		super();
		this._getDefaultDokanOptions();
		
		System.out.println(DokanLibrary.INSTANCE.DokanDriverVersion());
		System.out.println(DokanLibrary.INSTANCE.DokanVersion());
		
		// callbacks
		this.createFileCallback				= new EgwWinFSCreateFileCallback();
		//this.openDirectoryCallback			= new EgwWinFSOpenDirectoryCallback();
		//this.createDirectoryCallback		= new EgwWinFSCreateDirectoryCallback();
		this.cleanupCallback				= new EgwWinFSCleanupCallback();
		this.closeFileCallback				= new EgwWinFSCloseFileCallback();
		this.readFileCallback				= new EgwWinFSReadFileCallback();
		this.writeFileCallback				= new EgwWinFSWriteFileCallback();
		this.flushFileBuffersCallback		= new EgwWinFSFlushFileBuffersCallback();
		this.getFileInformationCallback		= new EgwWinFSGetFileInformationCallback();
		this.findFilesCallback				= new EgwWinFSFindFilesCallback();
		//this.findFilesWithPatternCallback	= new EgwWinFSFindFilesWithPatternCallback();
		this.setFileAttributesCallback		= new EgwWinFSSetFileAttributesCallback();
		this.setFileTimeCallback			= new EgwWinFSSetFileTimeCallback();
		this.deleteFileCallback				= new EgwWinFSDeleteFileCallback();
		this.deleteDirectoryCallback		= new EgwWinFSDeleteDirectoryCallback();
		this.moveFileCallback				= new EgwWinFSMoveFileCallback();
		this.setEndOfFileCallback			= new EgwWinFSSetEndOfFileCallback();
		this.setAllocationSizeCallback		= new EgwWinFSSetAllocationSizeCallback();
		this.lockFileCallback				= new EgwWinFSLockFileCallback();
		this.unlockFileCallback				= new EgwWinFSUnlockFileCallback();
		this.getDiskFreeSpaceCallback		= new EgwWinFSGetDiskFreeSpaceCallback();
		this.getVolumeInformationCallback	= new EgwWinFSGetVolumeInformationCallback();
		this.mountCallback					= new EgwWinFSMountCallback();
		this.unmountCallback				= new EgwWinFSUnmountCallback();
		this.getFileSecurityCallback		= new EgwWinFSGetFileSecurityCallback();
		this.setFileSecurityCallback		= new EgwWinFSSetFileSecurityCallback();
	}
	
	/**
	 * _getDefaultDokanOptions
	 */
	private void _getDefaultDokanOptions() {
		short version		= (short) DokanLibrary.DOKAN_VERSION;
		short threadCount	= 0; //for default (5) value
		NativeLong options	= new NativeLong(DokanOptions.DOKAN_OPTION_ALT_STREAM);
		int globalContext	= 0;
		WString mountPoint	= new WString("M");	// default s
			
		this._options = new DokanOptions(
			version, threadCount, options, globalContext, mountPoint);
	}
	
	/**
	 * init
	 */
	public void init() {
		int status = DokanLibrary.INSTANCE.DokanMain(this._options, this);
		
		switch (status) {
			case DokanLibrary.DOKAN_DRIVE_LETTER_ERROR:
				System.out.println("Drive letter error");
				break;
			case DokanLibrary.DOKAN_DRIVER_INSTALL_ERROR:
				System.out.println("Driver install error");
				break;
			case DokanLibrary.DOKAN_MOUNT_ERROR:
				System.out.println("Mount error");
				break;
			case DokanLibrary.DOKAN_START_ERROR:
				System.out.println("Start error");
				break;
			case DokanLibrary.DOKAN_ERROR:
				System.out.println("Unknown error");
				break;
			case DokanLibrary.DOKAN_SUCCESS:
				System.out.println("Success");
				break;
			default:
				System.out.println("Unknown status");
				break;
		}
	}
	
	/**
	 * setVolume
	 * @param volume 
	 */
	public void setVolume(EgwWinFSVolume volume) {
		((IEgwWinFSVolumeCallback)this.createFileCallback).setVolume(volume);
		//((IEgwWinFSVolumeCallback)this.openDirectoryCallback).setVolume(volume);
		//((IEgwWinFSVolumeCallback)this.createDirectoryCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.cleanupCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.closeFileCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.readFileCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.writeFileCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.flushFileBuffersCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.getFileInformationCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.findFilesCallback).setVolume(volume);
		//((IEgwWinFSVolumeCallback)this.findFilesWithPatternCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.setFileAttributesCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.setFileTimeCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.deleteFileCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.deleteDirectoryCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.moveFileCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.setEndOfFileCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.setAllocationSizeCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.lockFileCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.unlockFileCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.getDiskFreeSpaceCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.getVolumeInformationCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.mountCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.unmountCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.getFileSecurityCallback).setVolume(volume);
		((IEgwWinFSVolumeCallback)this.setFileSecurityCallback).setVolume(volume);
		
		this._volume = volume;
	}
	
	public static void main(String[] args) {
		EgwWinFS fs = new EgwWinFS();
		fs.init();
	}
	
	void showVersions(String driveLetter) {
		try {
			
			String user = "admin";
			String pass ="1234";
			
			String sharedFolder="public";
			String path="smb://192.168.11.4/"+sharedFolder+"/test.txt";
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",user, pass);
			SmbFile smbFile = new SmbFile(path,auth);
			SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFile);
			smbfos.write("testing....and writing to a file".getBytes());
			System.out.println("completed ...nice !");
		} catch (Exception ex) {
			Logger.getLogger(EgwWinFS.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}