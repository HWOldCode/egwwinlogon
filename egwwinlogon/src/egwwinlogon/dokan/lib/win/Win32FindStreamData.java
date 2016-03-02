/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.lib.win;

import com.sun.jna.Structure;
import static com.sun.jna.platform.win32.WinDef.MAX_PATH;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import java.util.Arrays;
import java.util.List;

/**
 * Win32FindStreamData
 * @author Stefan Werfling
 */
public class Win32FindStreamData extends Structure {
	
	public LARGE_INTEGER streamSize;
	
	public char[] cStreamName = new char[MAX_PATH + 36];
	
	public Win32FindStreamData() {	
	}
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"streamSize",
			"cStreamName"
		});
	}
}
