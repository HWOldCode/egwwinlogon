/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;

/**
 * Wtsapi32
 * @author Stefan Werfling
 */
public interface Wtsapi32 extends com.sun.jna.platform.win32.Wtsapi32 {
	
	/**
	 * INSTANCE
	 */
	Wtsapi32 INSTANCE = (Wtsapi32) Native.loadLibrary("Wtsapi32", Wtsapi32.class);
	
	/**
	 * WTSQueryUserToken
	 * @param SessionId
	 * @param phToken
	 * @return 
	 */
	boolean WTSQueryUserToken(int SessionId, PointerByReference phToken);
}
