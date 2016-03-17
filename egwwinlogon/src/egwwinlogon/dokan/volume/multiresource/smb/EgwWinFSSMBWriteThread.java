/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.volume.multiresource.smb;

import java.io.File;
import java.io.IOException;
/*import java.io.PipedInputStream;
import java.io.PipedOutputStream;*/
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.SmbRandomAccessFile;

/**
 * EgwWinFSSMBWriteThread
 * @author Stefan Werfling
 */
public class EgwWinFSSMBWriteThread extends Thread {

	/**
	 * close
	 */
	protected boolean _close = false;
	
	/**
	 * ended
	 */
	protected boolean _ended = false;
	
	/**
	 * output
	 */
	protected SmbRandomAccessFile _io = null;
	
	/**
	 * pipe output
	 */
	//protected PipedOutputStream _out = null;
	
	/**
	 * pipe input
	 */
	//protected PipedInputStream _in = null;
	
	/**
	 * tmp file
	 */
	protected File _tmpFile = null;
	
	/**
	 * access file
	 */
	protected RandomAccessFile _af = null;
	
	/**
	 * add offset
	 */
	protected long _addOffset = 0;
	
	/**
	 * cache size
	 */
	protected long _cacheSize = 0;
	
	/**
	 * EgwWinFSSMBWriteThread
	 * @param output 
	 * @throws java.io.IOException 
	 */
	public EgwWinFSSMBWriteThread(SmbRandomAccessFile inout) throws IOException {
		this._io	= inout;
		//this._out	= new PipedOutputStream();
		//this._in	= new PipedInputStream(this._out, 1024 * 1000 * 32);
	}
	
	/**
	 * write
	 * @param buffer 
	 * @throws java.io.IOException 
	 */
	public void write(byte[] buffer, long offset) throws IOException {
		if( this._af != null ) {
			
			if( this._addOffset <= offset ) {
				this._af.seek(offset);
				this._af.write(buffer);
				this._addOffset = offset;
			}
			else {
				this._io.seek(offset);
				this._io.write(buffer);
			}
		}
		//this._out.write(buffer, 0, buffer.length);
		//this._out.flush();
	}
	
	/**
	 * start
	 */
	@Override
	public void start() {
		try {
			this._tmpFile	= File.createTempFile("smb", "egwt");
			
			System.out.println("tmpFile: " + this._tmpFile.getPath());
			
			this._af		= new RandomAccessFile(this._tmpFile, "rw");
		} catch( IOException ex ) {
			Logger.getLogger(EgwWinFSSMBWriteThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		this._close = false;
		this._ended = false;
		super.start();
	}
	
	/**
	 * getSize
	 * @return 
	 */
	public long getSize() {
		return this._cacheSize;
	}
	
	/**
	 * run
	 */
	@Override
	public void run() {
		try {
			//BufferedInputStream input = new BufferedInputStream(this._in);
			long coffset	= 0;
			long blockSize	= 1024 * 1000 * 32;
			long readSize	= 0;
			
			while( !this._close ) {
				Thread.sleep(10);
				
				while( (this._af.length() > coffset) ) {
					readSize = this._af.length() - coffset;
					
					if( readSize > blockSize ) {
						readSize = blockSize;
					}
						
					int size = (int)readSize;
					
					byte[] buffer = new byte[size];

					this._af.seek(coffset);
					this._af.read(buffer);
					
					this._io.seek(coffset);
					this._io.write(buffer);
					
					coffset += buffer.length;
					
					// set new size
					if( this._cacheSize < coffset ) {
						this._cacheSize = coffset;
					}
				}
			}
		} catch( IOException ex ) {
			Logger.getLogger(EgwWinFSSMBWriteThread.class.getName()).log(Level.SEVERE, null, ex);
		} catch( InterruptedException ex ) {
			Logger.getLogger(EgwWinFSSMBWriteThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		this._ended = true;
		
		try {
			this._af.close();
		} catch (IOException ex) {
			Logger.getLogger(EgwWinFSSMBWriteThread.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		this._tmpFile.delete();
		
		this._af = null;
		this._tmpFile = null;
	}
	
	/**
	 * close
	 */
	public void close() {
		this._close = true;
	}
	
	/**
	 * eofAndClose
	 */
	public void eofAndClose() {
		this.close();
		
		while( !this._ended ) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				Logger.getLogger(EgwWinFSSMBWriteThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
