/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import java.util.Arrays;

/**
 * EgwWinFsVirtualFileMemoryStream
 * @author Stefan Werfling
 */
public class EgwWinFsVirtualFileMemoryStream {
	
	/**
	 * content
	 */
	private byte[] _content = new byte[0];
	
	/**
	 * 
	 */
	private long _writeSize = 0;
	
	/**
	 * setContent
	 * @param content 
	 */
	public void setContent(String content) {
		try {
			this._content = content.getBytes();
		}
		catch( Exception e ) {
		}
	}
	
	/**
	 * getContent
	 * @return 
	 */
	public String getContent() {
		return new String(this._content);
	}
	
	/**
	 * read
	 * @param buf
	 * @param offset
	 * @param bufferLen
	 * @return 
	 */
	public synchronized int read(byte[] buf, long offset, int bufferLen) {
        if( offset >= this._content.length ) {
            return -1; // trying to read from out of bounds
        }

        int couldRead = (this._content.length - offset < bufferLen) ? (this._content.length - (int) offset) : bufferLen;
        System.arraycopy(this._content, (int) offset, buf, 0, couldRead);
		
        return couldRead;
    }

	/**
	 * write
	 * @param buf
	 * @param offset
	 * @param bytesToWrite
	 * @return 
	 */
    public synchronized int write(byte[] buf, long offset, int bytesToWrite) {
        int newLen = (int) (offset + bytesToWrite);
		
        if( this._content.length < newLen ) {
			this._content = Arrays.copyOf(this._content, newLen);
		}

        System.arraycopy(buf, 0, this._content, (int) offset, bytesToWrite);
		
		//String tmp = new String(this._content);
		//System.out.println(tmp);
		
		this._writeSize += bytesToWrite;
		
        return bytesToWrite;
    }

	/**
	 * getSize
	 * @return 
	 */
    public long getSize() {
        return this._content.length;
    }

	/**
	 * getWriteSize
	 * @return 
	 */
	public long getWriteSize() {
		return this._writeSize;
	}
	
	/**
	 * setSize
	 * @param eof
	 * @return 
	 */
    public boolean setSize(long eof) {
        this._content = Arrays.copyOf(this._content, (int) eof);
		
        return true;
    }
}
