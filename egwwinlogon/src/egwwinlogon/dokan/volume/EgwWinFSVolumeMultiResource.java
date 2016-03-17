/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.volume;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import static com.sun.jna.platform.win32.WinDef.MAX_PATH;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import egwwinlogon.dokan.EgwWinFSHandleList;
import egwwinlogon.dokan.EgwWinFSHandleOwnerList;
import egwwinlogon.dokan.EgwWinFSVolume;
import egwwinlogon.dokan.EgwWinFsVirtualFile;
import egwwinlogon.dokan.lib.DokanFileInfo;
import egwwinlogon.dokan.lib.DokanLibrary;
import egwwinlogon.dokan.lib.win.ByHandleFileInformation;
import egwwinlogon.dokan.lib.win.SecurityDescriptor;
import egwwinlogon.dokan.lib.win.Win32FindData;
import egwwinlogon.dokan.volume.multiresource.EgwWinFSMultiResourceLocalMount;
import egwwinlogon.dokan.volume.multiresource.EgwWinFSMultiResourceRootDirectory;
import egwwinlogon.dokan.volume.multiresource.EgwWinFSMultiResourceSMBMount;
import egwwinlogon.dokan.volume.multiresource.EgwWinFSMultiResourceWebDavMount;
import org.apache.log4j.Logger;
import egwwinlogon.winapi.ProcessList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * EgwWinFSVolumeMultiResource
 * @author Stefan Werfling
 */
public class EgwWinFSVolumeMultiResource extends EgwWinFSVolume {
	
	/**
     * logger
     */
    private static final Logger _logger = Logger.getLogger(EgwWinFSVolumeMultiResource.class);
	
	/**
	 * root dir
	 */
	protected EgwWinFSMultiResourceRootDirectory _root = null;
	
	/**
	 * handles, by open files
	 */
	protected EgwWinFSHandleOwnerList _handles = new EgwWinFSHandleOwnerList();
	
	/**
	 * logging info
	 */
	protected Boolean _loggingInfo = false;
	
	/**
	 * label
	 */
	protected String _label = "WinFS Multi Resource";
	
	/**
	 * file system name
	 */
	protected String _fileSystemName = "EGWWINLOGON";
	
	/**
	 * volume max size
	 */
	protected long _volumeMaxSize = 1024 * 1000 * 1000;
	
	/**
	 * constructor
	 */
	public EgwWinFSVolumeMultiResource() {
		this._root = new EgwWinFSMultiResourceRootDirectory();
		
		File configFile = new File("config.json");
		
		if( configFile.exists() ) {
			JSONParser parser = new JSONParser();
			ContainerFactory containerFactory = new ContainerFactory(){
					public List creatArrayContainer() {
						return new LinkedList();
					}

					public Map createObjectContainer() {
						return new LinkedHashMap();
					}
				};
			
			try {
				Map json = (Map)parser.parse( 
					(new String(Files.readAllBytes(configFile.toPath()))).trim(), 
					containerFactory
					);
				
				Iterator entries = json.entrySet().iterator();
				
				while( entries.hasNext() ) {
					Entry tEntry = (Entry) entries.next();
					
					String ownerid = (String) tEntry.getKey();
					this.addOwnerSid(ownerid);
					
					LinkedHashMap ownerconfig = (LinkedHashMap) tEntry.getValue();
					
					if( ownerconfig.containsKey("mounts") ) {
						LinkedList<LinkedHashMap> mounts = (LinkedList<LinkedHashMap>) ownerconfig.get("mounts");
						
						for( int i=0; i<mounts.size(); i++ ) {
							LinkedHashMap mount = mounts.get(i);
							
							if( ((String) mount.get("type")).equals("smb") ) {
								String name = (String) mount.get("name");
								String ip = (String) mount.get("ip");
								String username = (String) mount.get("username");
								String password = (String) mount.get("password");
								
								this._root.addMount(
									ownerid, 
									new EgwWinFSMultiResourceSMBMount(
										name, 
										ip, 
										username, 
										password
										));
							}
						}
					}
				}
				
				//LinkedList<LinkedHashMap> mentrys = null;
			} catch (IOException ex) {
				java.util.logging.Logger.getLogger(EgwWinFSVolumeMultiResource.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ParseException ex) {
				java.util.logging.Logger.getLogger(EgwWinFSVolumeMultiResource.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		/*else {
			String tmpsid = "S-1-5-21-1062538719-3308691286-404400658-1001";
			String tmpsid2 = "S-1-5-21-1318477050-3409206289-4093497550-1000";

			this.addOwnerSid(tmpsid);
			this.addOwnerSid(tmpsid2);

			
			//this._root.addMount(tmpsid, new EgwWinFSMultiResourceSMBMount("disk-0", "192.168.0.26", "anonymous", ""));
			//this._root.addMount(tmpsid, new EgwWinFSMultiResourceSMBMount("disk-0-1", "192.168.0.26", "anonymous", ""));
			this._root.addMount(tmpsid, new EgwWinFSMultiResourceSMBMount("public", "192.168.11.4", "admin", "1234"));
			this._root.addMount(tmpsid, new EgwWinFSMultiResourceSMBMount("win.logon", "192.168.11.4", "admin", "1234"));
			this._root.addMount(tmpsid2, new EgwWinFSMultiResourceSMBMount("test.test", "192.168.10.5", "test.test", "Za3bCmJNz2FE"));
			this._root.addMount(tmpsid, new EgwWinFSMultiResourceWebDavMount("home", "", "", ""));
			this._root.addMount(tmpsid, new EgwWinFSMultiResourceLocalMount("C:/", "MyC"));
			//this._root.addMount(tmpsid, new EgwWinFSMultiResourceLocalMount("\\\\admin:1234@192.168.11.4\\public\\", "MyPublic"));
			// Test TODO
			//this._root.putFile(new EgwWinFsVirtualFile("public", WinNT.FILE_ATTRIBUTE_DIRECTORY));
			//this._root.putFile(new EgwWinFsVirtualFile("public2", WinNT.FILE_ATTRIBUTE_DIRECTORY));
			//this._root.putFile(new EgwWinFsVirtualFile("public3", WinNT.FILE_ATTRIBUTE_DIRECTORY));
		}*/
	}
	
	/**
	 * setLabel
	 * @param label 
	 */
	public void setLabel(String label) {
		this._label = label;
	}
	
	/**
	 * getLabel
	 * @return 
	 */
	public String getLabel() {
		return this._label;
	}
	
	/**
	 * setFileSystemName
	 * @param name 
	 */
	public void setFileSystemName(String name) {
		this._fileSystemName = name;
	}
	
	/**
	 * addOwnerSid
	 * @param sid 
	 */
	public void addOwnerSid(String sid) {
		if( this._loggingInfo ) {
			_logger.info("addOwnerSid: " + sid);
		}
		
		this._root.addOwnerSid(sid);
		this._handles.addOwner(sid);
	}
	
	/**
	 * removeOwnerSid
	 * @param sid 
	 */
	public void removeOwnerSid(String sid) {
		
	}
	
	/**
	 * addMount
	 * @param ownersid
	 * @param mount
	 */
	public void addMount(String ownersid, EgwWinFsVirtualFile mount) {
		if( this._loggingInfo ) {
			_logger.info("addMount ownersid: " + ownersid + " mountname: " + mount.getName());
		}
		
		this._root.addMount(ownersid, mount);
	}
	
	/**
	 * removeMount
	 * @param ownersid
	 * @param mount 
	 */
	public void removeMount(String ownersid, EgwWinFsVirtualFile mount) {
		
	}
	
	/**
	 * _getProcessOwnerSid
	 * @param dokanFileInfo
	 * @return 
	 */
	protected String _getProcessOwnerSid(DokanFileInfo dokanFileInfo) {
		if( dokanFileInfo != null ) {
			ProcessList.ProcessInfo process = ProcessList.getProcessByPId(dokanFileInfo.processId.intValue());
			
			String ownersid = process.getProcessOwner();
			
			// filter by exe TODO
			if( this._loggingInfo ) {
				System.out.println("_getProcessOwnerSid by Processid: " + 
					dokanFileInfo.processId.toString() + " Exe: " + 
					process.getProcessExeFile() + " OwnerSid: " + ownersid);
			}
			
			return ownersid;
		}
		else {
			if( this._loggingInfo ) {
				_logger.error("_getProcessOwnerSid dokanFileInfo is empty!");
			}
		}
		
		return null;
	}
	
	/**
	 * _checkPath
	 * @param path
	 * @return 
	 */
	protected String _checkPath(WString path ) {
		String tpath = path.toString();
		
		String[] _filters = new String[]{":"};
		
		for( String filter: _filters ) {
			if( tpath.indexOf(filter) > 0 ) {
				return null;
			}
		}
		
		return tpath;
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
	@Override
	public int onGetVolumeInformation(Pointer volumeNameBuffer, int volumeNameSize, IntByReference volumeSerialNumber, IntByReference maximumComponentLength, IntByReference fileSystemFlags, Pointer fileSystemNameBuffer, int fileSystemNameSize, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onGetVolumeInformation: ");
		}
		
		// TODO
		volumeNameBuffer.setString(0, "a3ds-Cloud" + "\0", true);
		volumeSerialNumber.setValue(0xBEAF);
		maximumComponentLength.setValue(MAX_PATH);
		fileSystemFlags.setValue(WinNT.FILE_UNICODE_ON_DISK | WinNT.FILE_CASE_PRESERVED_NAMES);
		fileSystemNameBuffer.setString(0, "ARTUR1" + "\0", true);
		
		return WinNT.ERROR_SUCCESS;
	}
	
	/**
	 * onGetDiskFreeSpace
	 * @param freeBytesAvailable
	 * @param totalNumbersOfBytes
	 * @param totalNumberOfFreeBytes
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onGetDiskFreeSpace(LongByReference freeBytesAvailable, LongByReference totalNumbersOfBytes, LongByReference totalNumberOfFreeBytes, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onGetDiskFreeSpace: ");
		}
		
		// TODO
		freeBytesAvailable.setValue(this._volumeMaxSize);
		totalNumbersOfBytes.setValue(this._volumeMaxSize);
		totalNumberOfFreeBytes.setValue(this._volumeMaxSize);
		
		return WinNT.ERROR_SUCCESS;
	}
	
	/**
	 * onFindFiles
	 * @param path
	 * @param fillFindDataCallback
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onFindFiles(WString path, DokanLibrary.FillFindDataCallback fillFindDataCallback, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onFindFiles: " + path.toString());
		}
		
		String ownersid = this._getProcessOwnerSid(dokanFileInfo);
		//HashMap handles = this._getOwnerHandles(ownersid);
		String spath	= this._checkPath(path);
		
		if( spath == null ) {
			return -WinNT.ERROR_PATH_NOT_FOUND;
		}
		
		for( EgwWinFsVirtualFile file : this._root.getFiles(spath, ownersid) ) {
            Win32FindData data = file.getWin32FindData();
			
            if( data != null ) {		
                fillFindDataCallback.invoke(data, dokanFileInfo);
            }
        }
		
		return WinNT.ERROR_SUCCESS;
	}
	
	/**
	 * onGetFileInformation
	 * @param path
	 * @param info
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onGetFileInformation(WString path, ByHandleFileInformation info, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onGetFileInformation: " + path.toString());
		}
		
		EgwWinFsVirtualFile mfile	= null;
		String spath				= this._checkPath(path);
		String ownersid				= this._getProcessOwnerSid(dokanFileInfo);
		
		if( this._loggingInfo ) {
			System.out.println("Search by handle: " + Long.toString(dokanFileInfo.context));
		}
		
		if( dokanFileInfo.context != 0 ) {
			EgwWinFSHandleList handles = this._handles.getHandleList(ownersid);
			
			if( handles != null ) {
				mfile = handles.getVirtualFile(spath, dokanFileInfo);
			}
			else {
				if( this._loggingInfo ) {
					System.out.println("Unknow Sid: " + ownersid);
				}
				
				return -WinNT.ERROR_FILE_NOT_FOUND;
			}
		}
		
		// ---------------------------------------------------------------------
		
		if( mfile == null ) {
			if( spath == null ) {
				return -WinNT.ERROR_FILE_NOT_FOUND;
			}

			mfile = this._root.getFile(spath, ownersid);
		}
		
		// ---------------------------------------------------------------------
		
		if( mfile != null ) {
			if( mfile.fillFileInfo(info) ) {
				if( mfile.isDirectory() ) {
					dokanFileInfo.isDirectory = (byte)1;
				}
				else {
					dokanFileInfo.isDirectory = (byte)0;
				}
				
				return WinNT.ERROR_SUCCESS;
			}
		}
		
		return -WinNT.ERROR_FILE_NOT_FOUND;
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
	@Override
	public int onReadFile(WString path, Pointer buffer, int numberOfBytesToRead, IntByReference numberOfBytesRead, long offset, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onReadFile: " + path.toString());
		}
		
		boolean opened				= false;
		EgwWinFsVirtualFile mfile	= null;
		String spath				= this._checkPath(path);
		String ownersid				= this._getProcessOwnerSid(dokanFileInfo);
		
		// ---------------------------------------------------------------------
		
		if( this._loggingInfo ) {
			System.out.println("onReadFile, open handle: " + Long.toString(dokanFileInfo.context));
		}
		
		// ---------------------------------------------------------------------
		
		EgwWinFSHandleList handles = this._handles.getHandleList(ownersid);
			
		if( handles != null ) {
			mfile = handles.getVirtualFile(spath, dokanFileInfo);

			// -----------------------------------------------------------------
			if( mfile == null ) {
				mfile = this._root.getFile(
					spath, 
					ownersid
					);
				
				if( mfile != null ) {
					mfile.open();
					opened = true;
				}
			}
			
			// -----------------------------------------------------------------
			
			if( mfile != null ) {
				try {
					int errorCode = WinNT.ERROR_SUCCESS;
					
					if( !mfile.read(buffer, offset, numberOfBytesToRead, numberOfBytesRead) ) {
						errorCode = -WinNT.ERROR_FILE_NOT_FOUND;
					}

					if( opened ) {
						mfile.close();
					}
					
					return errorCode;
				}
				catch( Exception ex ) {
					System.out.println("Error: ");
				}
			}
		}
		else {
			if( this._loggingInfo ) {
				System.out.println("Unknow Sid: " + ownersid);
			}
		}
		
		// ---------------------------------------------------------------------
		
		
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}
	
	/**
	 * onWriteFile
	 * @param path
	 * @param buffer
	 * @param numberOfBytesToWrite
	 * @param numberOfBytesWritten
	 * @param offset
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onWriteFile(WString path, Pointer buffer, int numberOfBytesToWrite, IntByReference numberOfBytesWritten, long offset, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			//_logger.info("onWriteFile: " + path.toString());
			System.out.println("onWriteFile: " + path.toString());
		}
		
		EgwWinFsVirtualFile mfile	= null;
		String spath				= this._checkPath(path);
		String ownersid				= this._getProcessOwnerSid(dokanFileInfo);
		
		// ---------------------------------------------------------------------
		
		if( this._loggingInfo ) {
			System.out.println("onWriteFile, open handle: " + Long.toString(dokanFileInfo.context));
		}
		
		// ---------------------------------------------------------------------
		
		EgwWinFSHandleList handles = this._handles.getHandleList(ownersid);
			
		if( handles != null ) {
			mfile = handles.getVirtualFile(spath, dokanFileInfo);

			if( mfile != null ) {
				if( !mfile.write(buffer, offset, numberOfBytesToWrite, numberOfBytesWritten) ) {
					return -WinNT.ERROR_FILE_CORRUPT;
				}

				return WinNT.ERROR_SUCCESS;
			}
		}
		else {
			if( this._loggingInfo ) {
				System.out.println("Unknow Sid: " + ownersid);
			}
		}
		
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}
	
	/**
	 * onCreateFile
	 * @param path
	 * @param securityContext
	 * @param desiredAccess
	 * @param fileAttributes
	 * @param shareAccess
	 * @param createDisposition
	 * @param createOptions
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onCreateFile(WString path, IntByReference securityContext, int desiredAccess, int fileAttributes, int shareAccess, int createDisposition, int createOptions, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onCreateFile: " + path.toString());
		}
		
		String ownersid = this._getProcessOwnerSid(dokanFileInfo);
		String spath	= this._checkPath(path);
		
		if( spath == null ) {
			return -WinNT.ERROR_FILE_NOT_FOUND;
		}
		
		IntByReference fileAttributesAndFlags = new IntByReference();
		IntByReference creationDisposition = new IntByReference();
		
		DokanLibrary.INSTANCE.DokanMapKernelToUserCreateFileFlags(
			fileAttributes, shareAccess, createDisposition, fileAttributesAndFlags, creationDisposition);
		
		EgwWinFsVirtualFile mfile = this._root.getFile(
			spath, 
			ownersid
			);
		
		/*if( mfile != null ) {
			if( mfile.isDirectory() ) {
				dokanFileInfo.isDirectory	= (byte) 1;
			}
			else {
				dokanFileInfo.isDirectory	= (byte) 0;
			}
			
			dokanFileInfo.context = mfile.hashCode();
		}*/
		
		switch( creationDisposition.getValue() ) {
			/*case WinNT.CREATE_ALWAYS:
				System.out.println("CREATE_ALWAYS");
				return this._createAlways(mfile, path, desiredAccess, shareAccess, fileAttributes, dokanFileInfo);
			*/	
			case WinNT.CREATE_NEW:
				return this._createNew(mfile, ownersid, spath, desiredAccess, shareAccess, fileAttributesAndFlags.getValue(), dokanFileInfo);
				
			case WinNT.OPEN_EXISTING:
				return this._openExisting(mfile, ownersid, spath, desiredAccess, shareAccess, fileAttributesAndFlags.getValue(), dokanFileInfo);
				
				
			/*case WinNT.OPEN_ALWAYS:
				System.out.println("OPEN_ALWAYS");
				return WinNT.ERROR_SUCCESS;*/
			case WinNT.TRUNCATE_EXISTING:
				System.out.println("TRUNCATE_EXISTING");
				break;
		}
		
		return  WinNT.SEVERITY_ERROR;
		//return -WinNT.ERROR_FILE_NOT_FOUND;
	}
	
	/**
	 * _createNew
	 * @param file
	 * @param ownersid
	 * @param path
	 * @param desiredAccess
	 * @param shareMode
	 * @param flagsAndAttributes
	 * @param dokanFileInfo
	 * @return 
	 */
	protected int _createNew(EgwWinFsVirtualFile file, String ownersid, String path, int desiredAccess, int shareMode, int flagsAndAttributes, DokanFileInfo dokanFileInfo) {
		int returnCode	= WinNT.ERROR_SUCCESS;
		
		if( file != null ) {
            if( file.isReadOnly() ) {
                return -WinNT.ERROR_ACCESS_DENIED;
            }
			
            return -WinNT.ERROR_ALREADY_EXISTS;
        }
		else {
			if( dokanFileInfo.isDirectory == (byte) 1 ) {
				flagsAndAttributes = WinNT.FILE_ATTRIBUTE_DIRECTORY;
			}
			else {
				flagsAndAttributes = WinNT.FILE_ATTRIBUTE_NORMAL;
			}
			
            file = this._root.createFile(
				path,
				flagsAndAttributes,
				ownersid
				);
			
            if( file == null ) {
                return -WinNT.ERROR_PATH_NOT_FOUND;
            }
        }

		// ---------------------------------------------------------------------
		
		file.open();
		
		
        dokanFileInfo.isDirectory	= file.isDirectory() ? (byte) 1 : 0;

		if( !file.isDirectory() ) {
			EgwWinFSHandleList handles = this._handles.getHandleList(ownersid);
			
			if( handles != null ) {
				handles.createNewHandle(path, file, dokanFileInfo);

				if( this._loggingInfo ) {
					System.out.println("New file, open handle: " + Long.toString(dokanFileInfo.context));
				}
			}
			else {
				System.out.println("Unknow Sid: " + ownersid);
			}
		}
		
		return returnCode;
	}
	
	/**
	 * _openExisting
	 * @param file
	 * @param ownersid
	 * @param path
	 * @param desiredAccess
	 * @param shareAccess
	 * @param value
	 * @param dokanFileInfo
	 * @return 
	 */
	protected int _openExisting(EgwWinFsVirtualFile file, String ownersid, String path, int desiredAccess, int shareAccess, int value, DokanFileInfo dokanFileInfo) {
		if( file != null ) {
			file.open();
			
			dokanFileInfo.isDirectory = file.isDirectory() ? (byte) 1 : 0;

			if( !file.isDirectory() ) {
				EgwWinFSHandleList handles = this._handles.getHandleList(ownersid);
				
				if( handles != null ) {
					handles.createNewHandle(path, file, dokanFileInfo);

					if( this._loggingInfo ) {
						System.out.println("Exist file, open handle: " + Long.toString(dokanFileInfo.context));
					}
				}
				else {
					if( this._loggingInfo ) {
						System.out.println("Unknow Sid: " + ownersid);
					}
				}
			}
			
			//file.incOpenCounter();

			return WinNT.ERROR_SUCCESS;
		}
		
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}
	
	/**
	 * onDeleteFile
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onDeleteFile(WString path, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onDeleteFile: " + path.toString());
		}
		
		
		EgwWinFsVirtualFile mfile	= null;
		String spath				= this._checkPath(path);
		String ownersid				= this._getProcessOwnerSid(dokanFileInfo);
		
		// ---------------------------------------------------------------------
		
		if( this._loggingInfo ) {
			System.out.println("onDeleteFile, open handle: " + Long.toString(dokanFileInfo.context));
		}
		
		// ---------------------------------------------------------------------
		
		EgwWinFSHandleList handles = this._handles.getHandleList(ownersid);
			
		if( handles != null ) {
			mfile = handles.getVirtualFile(spath, dokanFileInfo);

			if( mfile != null ) {
				// TODO ACL
				
				return WinNT.ERROR_SUCCESS;
			}
		}
		else {
			if( this._loggingInfo ) {
				System.out.println("Unknow Sid: " + ownersid);
			}
		}
		
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}
	
	/**
	 * onMoveFile
	 * @param existingPath
	 * @param newPath
	 * @param replaceExisting
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onMoveFile(WString existingPath, WString newPath, boolean replaceExisting, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onMoveFile: " + existingPath.toString());
		}
		
		String spath = this._checkPath(existingPath);
		
		if( spath == null ) {
			return -WinNT.ERROR_FILE_NOT_FOUND;
		}
		
		String npath = this._checkPath(newPath);
		
		if( npath == null ) {
			return -WinNT.ERROR_FILE_NOT_FOUND;
		}
		
		EgwWinFsVirtualFile nfile = this._root.getFile(
			npath, 
			this._getProcessOwnerSid(dokanFileInfo)
			);
		
		if( !replaceExisting ) {
			if( nfile != null ) {
				return -WinNT.ERROR_FILE_EXISTS;
			}
		}
		
		EgwWinFsVirtualFile mfile = this._root.getFile(
			spath, 
			this._getProcessOwnerSid(dokanFileInfo)
			);
		
		if( mfile != null ) {
			try {
				URL urlNewPath = new URL(npath);

				EgwWinFsVirtualFile ndir = this._root.getFile(
					urlNewPath.getPath(), 
					this._getProcessOwnerSid(dokanFileInfo)
					);
				
				if( ndir == null ) {
					return -WinNT.ERROR_FILE_NOT_FOUND;
				}
				
				
			}
			catch( Exception ex ) {
				System.out.println(ex.getMessage());
			}
		}
		
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}
	
	/**
	 * onDeleteDirectory
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onDeleteDirectory(WString path, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onMoveFile: " + path.toString());
		}
		
		String spath = this._checkPath(path);
		
		if( spath == null ) {
			return -WinNT.ERROR_FILE_NOT_FOUND;
		}
		
		EgwWinFsVirtualFile mfile = this._root.getFile(
			spath, 
			this._getProcessOwnerSid(dokanFileInfo)
			);
		
		if( mfile != null ) {
			if( mfile.isDirectory() ) {
				List<EgwWinFsVirtualFile> files = mfile.getFiles("");
				
				if( files.size() > 0 ) {
					return -WinNT.ERROR_DIR_NOT_EMPTY;
				}
				
				return WinNT.ERROR_SUCCESS;
			}
		}
		
		return -WinNT.ERROR_FILE_NOT_FOUND;
	}
	
	/**
	 * onCleanup
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onCleanup(WString path, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onCleanup: " + path.toString());
		}
		
		EgwWinFsVirtualFile mfile	= null;
		String spath				= this._checkPath(path);
		String ownersid				= this._getProcessOwnerSid(dokanFileInfo);
		
		// ---------------------------------------------------------------------
		
		if( dokanFileInfo.context != 0 ) {
			EgwWinFSHandleList handles = this._handles.getHandleList(ownersid);
			
			if( handles != null ) {
				mfile = handles.getVirtualFile(spath, dokanFileInfo);

				if( mfile != null ) {
					mfile.close();

					if( this._loggingInfo ) {
						System.out.println("Close Handle: " + Long.toString(dokanFileInfo.context));
					}

					handles.closeHandle(spath, dokanFileInfo);
				}

				// -----------------------------------------------------------------

				if( dokanFileInfo.deleteOnClose == (byte)1 ) {
					if( mfile != null ) {
						if( !mfile.delete() ) {

						}
					}
				}
			}
			else {
				if( this._loggingInfo ) {
					System.out.println("Unknow Sid: " + ownersid);
				}
			}
		}
		
		// ---------------------------------------------------------------------
		
		if( dokanFileInfo.isDirectory == (byte)1 ) {
			if( dokanFileInfo.deleteOnClose == (byte)1 ) {
				mfile = this._root.getFile(
					spath, 
					ownersid
					);

				if( mfile != null ) {
					if( mfile.isDirectory() ) {
						if( !mfile.delete() ) {

						}
					}
				}
			}
		}
		
		return WinNT.ERROR_SUCCESS;
	}
	
	/**
	 * onCloseFile
	 * @param path
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onCloseFile(WString path, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onCloseFile: " + path.toString());
		}
		
		return WinNT.ERROR_SUCCESS;
	}
	
	/**
	 * onSetFileAttributes
	 * @param path
	 * @param fileAttributes
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onSetFileAttributes(WString path, int fileAttributes, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onSetFileAttributes: " + path.toString());
		}
		
		return WinNT.ERROR_SUCCESS;
	}
	
	/**
	 * onSetAllocationSize
	 * @param path
	 * @param length
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onSetAllocationSize(WString path, long length, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onSetAllocationSize: " + path.toString());
		}
		
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onSetEndOfFile
	 * @param path
	 * @param length
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onSetEndOfFile(WString path, long length, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onSetEndOfFile: " + path.toString());
		}
		
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onSetFileSecurity
	 * @param path
	 * @param pSecurityInformation
	 * @param securityDescriptor
	 * @param bufferLength
	 * @param lengthNeeded
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onSetFileSecurity(WString path, IntByReference pSecurityInformation, SecurityDescriptor securityDescriptor, NativeLong bufferLength, LongByReference lengthNeeded, DokanFileInfo dokanFileInfo) {
		if( /*this._loggingInfo*/ true ) {
			System.out.println("onSetFileSecurity: " + path.toString());
		}
		
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onSetFileTime
	 * @param path
	 * @param creationTime
	 * @param lastAccessTime
	 * @param lastWriteTime
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onSetFileTime(WString path, WinBase.FILETIME creationTime, WinBase.FILETIME lastAccessTime, WinBase.FILETIME lastWriteTime, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onSetFileTime: " + path.toString());
		}
		
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onUnlockFile
	 * @param path
	 * @param byteOffset
	 * @param length
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onUnlockFile(WString path, long byteOffset, long length, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onUnlockFile: " + path.toString());
		}
		
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onUnmount
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onUnmount(DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onUnmount: ");
		}
		
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
	@Override
	public int onLockFile(WString path, long byteOffset, long length, DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onLockFile: " + path.toString());
		}
		
		return WinNT.ERROR_SUCCESS;
	}

	/**
	 * onMount
	 * @param dokanFileInfo
	 * @return 
	 */
	@Override
	public int onMount(DokanFileInfo dokanFileInfo) {
		if( this._loggingInfo ) {
			System.out.println("onMount: ");
		}
		
		return WinNT.ERROR_SUCCESS;
	}
}