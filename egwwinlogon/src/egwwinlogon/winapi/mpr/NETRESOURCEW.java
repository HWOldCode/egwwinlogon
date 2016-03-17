/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi.mpr;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import java.util.Arrays;

/**
 * NETRESOURCEW
 * @see http://msdn.microsoft.com/en-us/library/windows/desktop/aa385353(v=vs.85).aspx
 * @author Stefan Werfling
 */
public class NETRESOURCEW extends Structure
{
	public int dwScope;
	public int dwType;
	public int dwDisplayType;
	public int dwUsage;
	public WString lpLocalName;
	public WString lpRemoteName;
	public WString lpComment;
	public WString lpProvider;

	/**
	 * getFieldOrder
	 * @return 
	 */
	@Override
	protected java.util.List getFieldOrder() {
		return Arrays.asList(new String[] {
			"dwScope",
			"dwType",
			"dwDisplayType",
			"dwUsage",
			"lpLocalName",
			"lpRemoteName",
			"lpComment",
			"lpProvider"
			});
	}

	/**
	 * NETRESOURCEW
	 * @param mem 
	 */
	public NETRESOURCEW(Pointer mem) {
		super(mem);
		read();
	}

	/**
	 * NETRESOURCEW
	 */
	public NETRESOURCEW() {
		super();
	}
}