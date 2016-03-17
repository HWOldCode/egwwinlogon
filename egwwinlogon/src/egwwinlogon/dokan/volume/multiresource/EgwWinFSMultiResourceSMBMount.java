/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.volume.multiresource;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.dokan.EgwWinFsVirtualFile;
import egwwinlogon.dokan.volume.file.EgwWinFSFileCustomerDirStyle;
import egwwinlogon.dokan.volume.multiresource.smb.EgwWinFSSMBWriteThread;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import jcifs.smb.SmbRandomAccessFile;

/**
 * EgwWinFSMultiResourceSMBMount
 * @author Stefan Werfling
 */
public class EgwWinFSMultiResourceSMBMount extends EgwWinFsVirtualFile {

	static {
		//jcifs.Config.setProperty("jcifs.smb.client.snd_buf_size", Integer.toString(Integer.MAX_VALUE));
		//jcifs.Config.setProperty("jcifs.smb.client.rcv_buf_size", Integer.toString(Integer.MAX_VALUE));
		//jcifs.Config.setProperty("jcifs.netbios.client.writeSize", Integer.toString(655350));
		//jcifs.Config.setProperty( "jcifs.netbios.wins", "192.168.11.4" );
		//jcifs.Config.setProperty("jcifs.resolveOrder", "LMHOSTS,BCAST,DNS");
		//jcifs.Config.setProperty("jcifs.util.loglevel", "3");
		//jcifs.Config.setProperty("jcifs.smb.lmCompatibility", "3");
		//jcifs.Config.setProperty("jcifs.smb.client.useExtendedSecurity", "false");
		//jcifs.Config.setProperty("jcifs.smb.client.dfs.disabled", "true");
	}
	
	/**
	 * Username
	 */
	protected String _username = "";
	
	/**
	 * password
	 */
	protected String _password = "";
	
	/**
	 * is Share Point
	 */
	protected boolean _isSharePoint = false;
	
	/**
	 * smbfile
	 */
	protected SmbFile _smbfile = null;
	
	/**
	 * _input/ouput
	 */
	protected SmbRandomAccessFile _io = null;
	
	/**
	 * output
	 */
	protected SmbFileOutputStream _output = null;
	
	/**
	 * write thread
	 */
	protected EgwWinFSSMBWriteThread _writeThread = null;
	
	protected long t0 = 0;

	protected long tot = 0;
	
	
	/**
	 * 
	 * @param name
	 * @param ip
	 * @param username
	 * @param password 
	 */
	public EgwWinFSMultiResourceSMBMount(String name, String ip, String username, String password) {
		super(name, WinNT.FILE_ATTRIBUTE_DIRECTORY | WinNT.FILE_ATTRIBUTE_SYSTEM);
		
		this._username		= username;
		this._password		= password;
		this._isSharePoint	= true;
		
		try {
			NtlmPasswordAuthentication auth;
			
			if( "anonymous".equals(username) ) {
				auth = NtlmPasswordAuthentication.ANONYMOUS;
			}
			else {
				auth = new NtlmPasswordAuthentication("", username, password);
			}
			
			this._smbfile = new SmbFile(
				"smb://" + ip + "/" + name + "/", 
				auth,
				SmbFile.FILE_SHARE_READ | SmbFile.FILE_SHARE_WRITE | SmbFile.FILE_SHARE_DELETE
				);
			
			this._loadSmbInfo();
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		this.putFile(new EgwWinFSFileCustomerDirStyle(EgwWinFSFileCustomerDirStyle.STYLE_NETDRIVE));
	}	
	
	/**
	 * 
	 * @param file
	 * @throws SmbException 
	 */
	public EgwWinFSMultiResourceSMBMount(SmbFile file) throws SmbException {
		super(
			file.getName().replaceAll("/", ""), 
			(file.isDirectory() ?  WinNT.FILE_ATTRIBUTE_DIRECTORY : WinNT.FILE_ATTRIBUTE_NORMAL) 
			);
		
		this._smbfile = file;
		this._loadSmbInfo();
	}
	
	/**
	 * 
	 * @param file
	 * @param username
	 * @param password
	 * @throws SmbException 
	 */
	public EgwWinFSMultiResourceSMBMount(SmbFile file, String username, String password) throws SmbException {
		super(
			file.getName().replaceAll("/", ""), 
			(file.isDirectory() ?  WinNT.FILE_ATTRIBUTE_DIRECTORY : WinNT.FILE_ATTRIBUTE_NORMAL) 
			);
		
		this._username = username;
		this._password = password;
		
		try {
			NtlmPasswordAuthentication auth;
			
			if( "anonymous".equals(username) ) {
				auth = NtlmPasswordAuthentication.ANONYMOUS;
			}
			else {
				auth = new NtlmPasswordAuthentication("", username, password);
			}
			
			String fp = file.getURL().toString();
			
			this._smbfile = new SmbFile(
				fp, 
				auth,
				SmbFile.FILE_SHARE_READ | SmbFile.FILE_SHARE_WRITE | SmbFile.FILE_SHARE_DELETE
				);
			
			this._loadSmbInfo();
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * _loadSmbInfo
	 */
	protected void _loadSmbInfo() {
		try {
			if( this._smbfile != null ) {
				this._creationTime			= new Date(this._smbfile.createTime());
				this._lastWriteTime			= new Date(this._smbfile.getLastModified());
			}
		} 
		catch( SmbException ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * getSize
	 * @return 
	 */
	@Override
    public long getSize() {
		try {
			if( this.isDirectory() ) {
				long sum = 0;

				SmbFile[] smblist = this._smbfile.listFiles();

				for( SmbFile file: smblist ) {
					sum += file.length();
				}

				return sum;
			} 
			else {
				long size = 0;
				
				
				if( (size == 0) && (this._writeThread != null) ) {
					size = this._writeThread.getSize();
				}
				
				if( size == 0 ) {
					size = this._smbfile.length();
				}
				
				return size;
			}
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return 0;
    }
	
	/**
	 * getFiles
	 * @param path
	 * @return 
	 */
	@Override
	public List<EgwWinFsVirtualFile> getFiles(String[] path) {
		if( this._smbfile == null ) {
			return Collections.emptyList();
		}
		
		try {
			if( path.length > 0 ) {
				SmbFile tdes = this._smbfileExist(path[0], this._smbfile);
				
				if( tdes != null ) {
					EgwWinFSMultiResourceSMBMount vfile = new EgwWinFSMultiResourceSMBMount(tdes, this._username, this._password);
						
					return vfile.getFiles(Arrays.copyOfRange(path, 1, path.length));
				}
			}
			else {
				SmbFile[] smblist = this._smbfile.listFiles();
				List<EgwWinFsVirtualFile>  list = super.getFiles(new String[]{});
				
				for( SmbFile file: smblist ) {
					list.add(new EgwWinFSMultiResourceSMBMount(file, this._username, this._password));
				}
				
				return list;
			}
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * getFile
	 * @param path
	 * @return 
	 */
	@Override
	public EgwWinFsVirtualFile getFile(String[] path) {
		if( this._smbfile == null ) {
			return null;
		}
		
		if( path.length == 0 ) {
			return null;
		}
		
		if( path.length == 1 ) {
			EgwWinFsVirtualFile tfile = super.getFile(path);
			
			if( tfile != null ) {
				return tfile;
			}
		}
		
		SmbFile tdes = this._smbfileExist(path[0], this._smbfile);
		
		if( tdes != null ) {
			try {
				EgwWinFSMultiResourceSMBMount vfile = new EgwWinFSMultiResourceSMBMount(tdes, this._username, this._password);
				
				if( path.length == 1 ) {
					return vfile;
				}
				else {
					if( vfile.isDirectory() ) {
						return vfile.getFile(Arrays.copyOfRange(path, 1, path.length));
					}
				}
			}
			catch( Exception ex ) {
				Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @param file
	 * @return 
	 */
	public SmbFile _smbfileExist(String name, SmbFile file) {
		try {
			SmbFile nfile = new SmbFile(file, name + "/");

			if( nfile.exists() ) {
				return nfile;
			}
		}
		catch( Exception ex ) {
			try {
				SmbFile nfile = new SmbFile(file, name);

				if( nfile.exists() ) {
					return nfile;
				}
			}
			catch(Exception ex2) {
				Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		return null;
	}
	
	/**
	 * open
	 * @return 
	 */
	@Override
	public boolean open() {
		if( this._smbfile == null ) {
			return false;
		}
		
		try {
			if( !this._smbfile.isFile() ) {
				return false;
			}
			
			this.setLastAccessTime(new Date());
			
			this._io = new SmbRandomAccessFile(this._smbfile, "rw");
			
				/*this._input = this._smbfile.getInputStream();*/
			//this._output = new SmbFileOutputStream(this._smbfile, true);
			
			if( this._writeThread == null ) {
				this._writeThread = new EgwWinFSSMBWriteThread(this._io);
				this._writeThread.start();
			}
			
			this.t0 = System.currentTimeMillis();
				//this._accessFile = new SmbRandomAccessFile(this._smbfile, "rw");
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return true;
	}
	
	/**
	 * close
	 * @return 
	 */
	@Override
	public boolean close() {
		try {
			
			//this.setLastWriteTime(new Date());
			
			/*if( this._input != null ) {
				this._input.close();
				this._input = null;
			}
			*/
			if( this._io != null ) {
				long t = System.currentTimeMillis() - this.t0;
				
				if( this.tot != 0 ) {
					System.out.println( this.tot + " bytes transfered in " + ( t / 1000 ) + " seconds at " + (( this.tot / 1000 ) / Math.max( 1, ( t / 1000 ))) + "Kbytes/sec" );
				}
				
				this._writeThread.eofAndClose();
				this._writeThread = null;
				
				this._io.close();
				this._io = null;
			}
			
			this.setLastAccessTime(new Date());
			
			return true;
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}

		return false;
	}
	
	/**
	 * read
	 * @param readBuf
	 * @param offset
	 * @param bufferLen
	 * @param bytesRead
	 * @return 
	 */
	@Override
	public boolean read(Pointer readBuf, long offset, int bufferLen, IntByReference bytesRead) {
		if( this._smbfile == null ) {
			return false;
		}
		
		try {
			if( offset > this.getSize() ) {
				return false;
			}
			
			if( this.getSize() < (offset+bufferLen) ) {
				bufferLen = (int)(this.getSize() - offset);
			}
			
			this._io.seek(offset);
			
			byte[] buf = new byte[bufferLen];
			int read = this._io.read(buf);
			
			if( read == -1 ) {
				bytesRead.setValue(0);
			}
			else {
				bytesRead.setValue(read);
				readBuf.write(0, buf, 0, read);
				return true;
			}
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return false;
    }

	/**
	 * write
	 * @param writeBuf
	 * @param offset
	 * @param bytesToWrite
	 * @param bytesWritten
	 * @return 
	 */
	@Override
    public boolean write(Pointer writeBuf, long offset, int bytesToWrite, IntByReference bytesWritten) {
		if( this._writeThread == null ) {
			return false;
		}
		
		try {
			byte[] buf = new byte[bytesToWrite];

			writeBuf.read(0, buf, 0, bytesToWrite);

			this._writeThread.write(buf, offset);
			
			bytesWritten.setValue(bytesToWrite);

			return true;
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return false;
    }

	/**
	 * setLastWriteTime
	 * @param date 
	 */
	private void setLastWriteTime(Date date) {
		if( this._smbfile != null ) {
			try {
				this._smbfile.setLastModified(date.getTime());
			}
			catch( Exception ex ) {
				Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	/**
	 * createFile
	 * 
	 * @param path
	 * @param flagsAndAttributes
	 * @return 
	 */
	@Override
	public EgwWinFsVirtualFile createFile(String[] path, int flagsAndAttributes) {
		if( this._smbfile == null ) {
			return null;
		}
		
		if( path.length == 0 ) {
			return this;
		}
		
		try {
			if( path.length == 1 ) {
				SmbFile nfile;
				
				if( EgwWinFsVirtualFile._in(flagsAndAttributes, WinNT.FILE_ATTRIBUTE_DIRECTORY) ) {
					nfile = new SmbFile(this._smbfile, path[0] + "/");
					
					if( !nfile.exists() ) {
						nfile.mkdirs();
					}
				}
				else {
					nfile = new SmbFile(this._smbfile, path[0]);
					
					if( !nfile.exists() ) {
						//nfile.createNewFile();
						//nfile.setAllowUserInteraction(true);
						SmbFileOutputStream _os = new SmbFileOutputStream(nfile, false);
						_os.close();
					}
				}
				
				return new EgwWinFSMultiResourceSMBMount(nfile, this._username, this._password);
			}
			else {
				SmbFile tdes = this._smbfileExist(path[0], this._smbfile);
		
				if( tdes != null ) {
					EgwWinFSMultiResourceSMBMount vfile = new EgwWinFSMultiResourceSMBMount(tdes, this._username, this._password);
					
					return vfile.createFile(
						Arrays.copyOfRange(path, 1, path.length), 
						flagsAndAttributes
						);
				}
			}
		}
		catch( Exception ex ) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	/**
	 * delete
	 * @return 
	 */
	@Override
	public boolean delete() {
		if( this._smbfile == null ) {
			return false;
		}
		
		try {
			this.close();
			
			Thread.sleep(1000);
			
			this._smbfile.delete();
			
			return true;
		} catch (SmbException ex) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println(ex.getMessage());
		} catch (InterruptedException ex) {
			Logger.getLogger(EgwWinFSMultiResourceSMBMount.class.getName()).log(Level.SEVERE, null, ex);
		}
		
        return false;
    }
}