/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi.mpr;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;

/**
 * NETRESOURCEA
 * @see http://msdn.microsoft.com/en-us/library/windows/desktop/aa385353(v=vs.85).aspx
 * @author Stefan Werfling
 */
public class NETRESOURCEA extends Structure
{
	public int dwScope;
	public int dwType;
	public int dwDisplayType;
	public int dwUsage;
	public String lpLocalName;
	public String lpRemoteName;
	public String lpComment;
	public String lpProvider;

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
	 * NETRESOURCEA
	 * @param mem 
	 */
	public NETRESOURCEA(Pointer mem) {
		super(mem);
		read();
	}

	/**
	 * NETRESOURCEA
	 */
	public NETRESOURCEA() {
		super();
	}
}