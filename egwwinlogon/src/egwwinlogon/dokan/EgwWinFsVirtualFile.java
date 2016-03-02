/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import egwwinlogon.dokan.lib.win.ByHandleFileInformation;
import egwwinlogon.dokan.lib.win.Win32FindData;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
	
	private String _name;
    private String _alternativeName;
    private Date _creationTime;
    private Date _lastAccessTime;
    private Date _lastWriteTime;
    private int _flagsAndAttributes;
	
	private String _content;
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
            this._content = "";
        }
    }
	
	/**
	 * 
	 * @return 
	 */
	public String getName() {
        return this._name;
    }

	/**
	 * 
	 * @return 
	 */
    public long getSize() {
        if( this.isDirectory() ) {
            long sum = 0;
			
            for( EgwWinFsVirtualFile file: this._files.values() ) {
                sum += file.getSize();
            }
            return sum;
        } else {
            return this._content.length();
        }
    }

	/**
	 * 
	 * @return 
	 */
    public Date getLastWriteTime() {
        return this._lastWriteTime;
    }

    public Date getLastAccessTime() {
        return this._lastAccessTime;
    }

    public Date getCreationTime() {
        return this._creationTime;
    }

    public int getFlagsAndAttributes() {
        return this._flagsAndAttributes;
    }
	
	public boolean isReadOnly() {
        return EgwWinFsVirtualFile._in(this._flagsAndAttributes, WinNT.FILE_ATTRIBUTE_READONLY);
    }

    public boolean isDirectory() {
        return EgwWinFsVirtualFile._in(this._flagsAndAttributes, WinNT.FILE_ATTRIBUTE_DIRECTORY);
    }

    public boolean isDeleteOnClose() {
        return EgwWinFsVirtualFile._in(this._flagsAndAttributes, WinNT.FILE_FLAG_DELETE_ON_CLOSE);
    }
	
	static private boolean _in(long var, long flag) {
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
        result.sizeHigh			= (int) (this.getSize() >>> 32);
        result.sizeLow			= (int) (this.getSize());

        return result;
    }
	
	/**
	 * getFiles
	 * @return 
	 */
	public List<EgwWinFsVirtualFile> getFiles() {
        if (!isDirectory()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(this._files.values());
    }
	
	public void putFile(EgwWinFsVirtualFile file) {
        //file.setParent(this);
        this._files.put(file.getName(), file);
    }
	
	/**
	 * 
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
	
	@Override
    public int hashCode() {
        int result = this._name != null ? this._name.hashCode() : 0;

        result = 31 * result + (this._alternativeName != null ? this._alternativeName.hashCode() : 0);
        result = 31 * result + (this.isDirectory() ? 1231 : ((int) this._content.length() + (int) (this._content.length() >>> 32)));

        return result;
    }
}