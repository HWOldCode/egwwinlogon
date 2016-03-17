/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * EgwWinFSRByteMemoryStack
 * @author Stefan Werfling
 */
public class EgwWinFSRByteMemoryStack {
	
	/**
	 * Buffer
	 */
	protected ArrayList<byte[]> _buffer = new ArrayList<>();
	
	/**
	 * index size
	 */
	protected int _indexSize = 0;
	
	/**
	 * offset
	 */
	protected long _offset = 0;
	
	/**
	 * 
	 * @param levelSize 
	 */
	public EgwWinFSRByteMemoryStack(int indexSize) {
		this._indexSize = indexSize;
	}
	
	/**
	 * add
	 * @param bytes 
	 */
	public void add(byte[] bytes) {
		this._buffer.add(bytes);
	}
	
	/**
	 * get
	 * @return 
	 */
	public byte[] get() {
		if( this._buffer.size() > 0 ) {
			return this._buffer.get(0);
		}
		
		return null;
	}
	
	/**
	 * getIndexSize
	 * @return 
	 */
	public int getIndexSize() {
		return this._buffer.size();
	}
	
	/**
	 * isIndexSize
	 * @return 
	 */
	public boolean isIndexSize() {
		return this.getIndexSize() >= this._indexSize;
	}
	
	public void clear() {
		this._buffer = new ArrayList<>();
	}
}