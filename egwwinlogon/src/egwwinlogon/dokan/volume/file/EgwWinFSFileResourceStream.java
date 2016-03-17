/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.volume.file;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.dokan.EgwWinFsVirtualFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;

/**
 * EgwWinFSFileResourceStream
 * @author Stefan Werfling
 */
public class EgwWinFSFileResourceStream extends EgwWinFsVirtualFile {
	
	/**
	 * stream file
	 */
	private URL _streamFile;
	
	/**
	 * EgwWinFSFileResourceStream
	 * @param streamFile 
	 */
	public EgwWinFSFileResourceStream(String streamFile) {
		super(
			Paths.get(streamFile).getFileName().toString(),
			WinNT.FILE_ATTRIBUTE_HIDDEN
			);
		
		this._streamFile =  ClassLoader.getSystemClassLoader().getResource(streamFile);
	}
	
	/**
	 * getSize
	 * @return 
	 */
	@Override
    public long getSize() {
		try {
			File afile = new File(this._streamFile.toURI());

			if( afile.exists() ) {
				return afile.length();
			}
		}
		catch( Exception ex ) {
			System.out.println(ex.getMessage());
		}
		
		return 0;
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
		try {
			File afile = new File(this._streamFile.toURI());

			if( afile.exists() ) {
				this.setLastAccessTime(new Date());
			
				InputStream input = new FileInputStream(afile);
				input.skip(offset);

				byte[] buf = new byte[bufferLen];
				int read = input.read(buf);

				input.close();
				
				if( read == -1 ) {
					bytesRead.setValue(0);
				}
				else {
					bytesRead.setValue(read);
					readBuf.write(0, buf, 0, read);

					return true;
				}
			}
		}
		catch( Exception ex ) {
			System.out.println(ex.getMessage());
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
		return false;
	}
}