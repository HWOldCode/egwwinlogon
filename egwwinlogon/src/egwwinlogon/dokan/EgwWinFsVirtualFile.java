/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.dokan.lib.win.ByHandleFileInformation;
import egwwinlogon.dokan.lib.win.Win32FindData;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EgwWinFsVirtualFile
 * @author Stefan Werfling
 */
public class EgwWinFsVirtualFile {
	
	public static final int ALLOWED_TO_SET_ATTR = WinNT.FILE_ATTRIBUTE_ARCHIVE | WinNT.FILE_ATTRIBUTE_HIDDEN |
            WinNT.FILE_ATTRIBUTE_NORMAL | WinNT.FILE_ATTRIBUTE_NOT_CONTENT_INDEXED |
            WinNT.FILE_ATTRIBUTE_OFFLINE | WinNT.FILE_ATTRIBUTE_READONLY |
            WinNT.FILE_ATTRIBUTE_SYSTEM | WinNT.FILE_ATTRIBUTE_TEMPORARY;
	
	protected String _name				= null;
    protected String _alternativeName		= null;
    protected Date _creationTime			= null;
    protected Date _lastAccessTime		= null;
    protected Date _lastWriteTime			= null;
    protected int _flagsAndAttributes		= 0;
	protected EgwWinFsVirtualFile _parent = null;
	
	private EgwWinFsVirtualFileMemoryStream _content = null;
    private Map<String, EgwWinFsVirtualFile> _files;
	
	/**
	 * 
	 * @param name
	 * @param flagsAndAttributes 
	 */
	public EgwWinFsVirtualFile(String name, int flagsAndAttributes) {
        this._name					= name;
        this._alternativeName		= "";
        this._flagsAndAttributes	= flagsAndAttributes;
        this._creationTime			= new Date();
        this._lastAccessTime		= new Date();
        this._lastWriteTime			= new Date();

        if( this.isDirectory() ) {
            this._files = new HashMap<>();
        } else {
            this._content = new EgwWinFsVirtualFileMemoryStream();
        }
    }
	
	/**
	 * getName
	 * @return 
	 */
	public String getName() {
        return this._name;
    }

	/**
	 * getSize
	 * @return 
	 */
    public long getSize() {
        if( this.isDirectory() ) {
            long sum = 0;
			
            for( EgwWinFsVirtualFile file: this._files.values() ) {
                sum += file.getSize();
            }
            return sum;
        } 
		else {
            return this._content.getSize();
        }
    }

	/**
	 * setContent
	 * @param content 
	 */
	public void setContent(String content) {
		this._content.setContent(content);
	}
	
	/**
	 * getLastWriteTime
	 * @return 
	 */
    public Date getLastWriteTime() {
        return this._lastWriteTime;
    }

	/**
	 * getLastAccessTime
	 * @return 
	 */
    public Date getLastAccessTime() {
        return this._lastAccessTime;
    }

	/**
	 * getCreationTime
	 * @return 
	 */
    public Date getCreationTime() {
        return this._creationTime;
    }

	/**
	 * getFlagsAndAttributes
	 * @return 
	 */
    public int getFlagsAndAttributes() {
        return this._flagsAndAttributes;
    }
	
	/**
	 * isReadOnly
	 * @return 
	 */
	public boolean isReadOnly() {
        return EgwWinFsVirtualFile._in(this._flagsAndAttributes, WinNT.FILE_ATTRIBUTE_READONLY);
    }

	/**
	 * isDirectory
	 * @return 
	 */
    public boolean isDirectory() {
        return EgwWinFsVirtualFile._in(this._flagsAndAttributes, WinNT.FILE_ATTRIBUTE_DIRECTORY);
    }

	/**
	 * isDeleteOnClose
	 * @return 
	 */
    public boolean isDeleteOnClose() {
        return EgwWinFsVirtualFile._in(this._flagsAndAttributes, WinNT.FILE_FLAG_DELETE_ON_CLOSE);
    }
	
	/**
	 * _in
	 * @param var
	 * @param flag
	 * @return 
	 */
	static protected boolean _in(long var, long flag) {
        return (var & flag) != 0;
    }

	/**
	 * getWin32FindData
	 * @return 
	 */
	public Win32FindData getWin32FindData() {
        Win32FindData result = new Win32FindData();
        byte[] nameBuf;

        try {
            nameBuf = (this._name + "\0").getBytes("UTF-16LE");
            System.arraycopy(nameBuf, 0, result.fileName, 0, nameBuf.length);

            nameBuf = (this._alternativeName + "\0").getBytes("UTF-16LE");
            System.arraycopy(nameBuf, 0, result.alternativeFileName, 0, nameBuf.length);
        } catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            return null;
        }

        result.fileAttributes	= this._flagsAndAttributes;
        result.creationTime		= new WinBase.FILETIME(this._creationTime);
        result.lastAccess		= new WinBase.FILETIME(this._lastAccessTime);
        result.lastWrite		= new WinBase.FILETIME(this._lastWriteTime);
		
		if( !this.isDirectory() ) {
			result.sizeHigh			= (int) (this.getSize() >>> 32);
			result.sizeLow			= (int) (this.getSize());
		}

        return result;
    }
	
	/**
	 * getFiles
	 * @return 
	 */
	public List<EgwWinFsVirtualFile> getFiles(String path) {
		return this.getFiles(this._parsePath(path));
	}
	
	/**
	 * getFiles
	 * @return 
	 */
	public List<EgwWinFsVirtualFile> getFiles(String[] path) {
		if( path.length > 0 ) {
			if( this._files.containsKey(path[0]) ) {
				EgwWinFsVirtualFile cfile = this._files.get(path[0]);
				
				if( cfile.isDirectory() ) {
					return cfile.getFiles(Arrays.copyOfRange(path, 1, path.length));
				}
			}
		}
		else {
			if( !this.isDirectory() ) {
				return Collections.emptyList();
			}

			return new ArrayList<>(this._files.values());
		}
		
		return Collections.emptyList();
    }
	
	/**
	 * getFile
	 * @param path
	 * @return 
	 */
	public EgwWinFsVirtualFile getFile(String path) {
		return this.getFile(this._parsePath(path));
	}
	
	/**
	 * getFile
	 * @param path
	 * @return 
	 */
	public EgwWinFsVirtualFile getFile(String[] path) {
		if( path.length == 0 ) {
			return null;
		}
		
		if( this._files.containsKey(path[0]) ) {
			EgwWinFsVirtualFile cfile = this._files.get(path[0]);
			
			if( cfile != null ) {
				if( path.length == 1 ) {
					return cfile;
				}
				else {
					if( cfile.isDirectory() ) {
						return cfile.getFile(Arrays.copyOfRange(path, 1, path.length));
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * putFile
	 * @param file 
	 */
	public void putFile(EgwWinFsVirtualFile file) {
        file.setParent(this);
        this._files.put(file.getName(), file);
    }
	
	/**
	 * createFile
	 * @param path
	 * @param flagsAndAttributes
	 * @return 
	 */
	public EgwWinFsVirtualFile createFile(String path, int flagsAndAttributes) {
		return this.createFile(this._parsePath(path), flagsAndAttributes);
	}
	
	/**
	 * createFile
	 * 
	 * @param path
	 * @param flagsAndAttributes
	 * @return 
	 */
	public EgwWinFsVirtualFile createFile(String[] path, int flagsAndAttributes) {
		if( path.length == 0 ) {
			return this;
		}
		
		if( path.length == 1 ) {
			EgwWinFsVirtualFile nfile = new EgwWinFsVirtualFile(path[0], flagsAndAttributes);
			
			this.putFile(nfile);
			
			return nfile;
		}
		else {
			if( this._files.containsKey(path[0]) ) {
				EgwWinFsVirtualFile cfile = this._files.get(path[0]);
				
				if( cfile != null ) {
					return cfile.createFile(
						Arrays.copyOfRange(path, 1, path.length), 
						flagsAndAttributes
						);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * fillFileInfo
	 * @param fileInfo
	 * @return 
	 */
	public boolean fillFileInfo(ByHandleFileInformation fileInfo) {
        fileInfo.attributes				= this.getFlagsAndAttributes();
        fileInfo.creationTime			= new WinBase.FILETIME(this.getCreationTime());
        fileInfo.lastAccessTime			= new WinBase.FILETIME(this.getLastAccessTime());
        fileInfo.lastWriteTime			= new WinBase.FILETIME(this.getLastWriteTime());
        fileInfo.fileSizeHigh			= (int) (this.getSize() >>> 32);
        fileInfo.fileSizeLow			= (int) (this.getSize());
        fileInfo.numberOfLinks			= 1;
        fileInfo.volumeSerialNumber		= 0xBEAF;

        return true;
    }
	
	/**
	 * hashCode
	 * @return 
	 */
	@Override
    public int hashCode() {
        int result = this._name != null ? this._name.hashCode() : 0;

        result = 31 * result + (this._alternativeName != null ? this._alternativeName.hashCode() : 0);
        result = 31 * result + (this.isDirectory() ? 1231 : ((int) this._content.getSize()+ (int) (this._content.getSize() >>> 32)));

        return result;
    }
	
	/**
	 * _parsePath
	 * @param path
	 * @return 
	 */
	protected String[] _parsePath(String path) {
        String trailingsBSlashRemoved = path.toString().replaceAll("^\\\\", "");
		
        if (trailingsBSlashRemoved.length() == 0) {
            return new String[0];
        }
		
        return trailingsBSlashRemoved.split("\\\\+");
    }
	
	/**
	 * open
	 * @return 
	 */
	public boolean open() {
		return true;
	}
	
	/**
	 * close
	 * @return 
	 */
	public boolean close() {
		return true;
	}
	
	/**
	 * read
	 * @param readBuf
	 * @param offset
	 * @param bufferLen
	 * @param bytesRead
	 * @return 
	 */
	public boolean read(Pointer readBuf, long offset, int bufferLen, IntByReference bytesRead) {
        this.setLastAccessTime(new Date());

        byte[] buf = new byte[bufferLen];
        int read = this._content.read(buf, offset, bufferLen);

        if( read == -1 ) {
            bytesRead.setValue(0);
            return false;
        } 
		else {
            bytesRead.setValue(read);
            readBuf.write(0, buf, 0, read);
			
            return true;
        }
    }
	
	/**
	 * write
	 * @param writeBuf
	 * @param offset
	 * @param bytesToWrite
	 * @param bytesWritten
	 * @return 
	 */
    public boolean write(Pointer writeBuf, long offset, int bytesToWrite, IntByReference bytesWritten) {
        this.setLastAccessTime(new Date());
        this.setLastWriteTime(new Date());

        byte[] buf = new byte[bytesToWrite];
        writeBuf.read(0, buf, 0, bytesToWrite);
        int written = this._content.write(buf, offset, bytesToWrite);
        bytesWritten.setValue(written);
        return true;
    }

	/**
	 * setLastAccessTime
	 * @param lastAccessTime 
	 */
	protected void setLastAccessTime(Date lastAccessTime) {
		this._lastAccessTime = lastAccessTime;
	}

	/**
	 * setLastWriteTime
	 * @param date 
	 */
	private void setLastWriteTime(Date lastWriteTime) {
		this._lastWriteTime = lastWriteTime;
	}

	/**
	 * setParent
	 * @param parent 
	 */
	protected void setParent(EgwWinFsVirtualFile parent) {
		this._parent = parent;
	}
	
	/**
	 * getParent
	 * @return 
	 */
	public EgwWinFsVirtualFile getParent() {
		return this._parent;
	}
	
	/**
	 * _deleteChild
	 * @param virtualFile
	 * @return 
	 */
	protected EgwWinFsVirtualFile _deleteChild(EgwWinFsVirtualFile virtualFile) {
        return this._files.remove(virtualFile.getName());
    }
	
	/**
	 * delete
	 * @return 
	 */
	public boolean delete() {
		return this._parent._deleteChild(this) != null;
    }
}