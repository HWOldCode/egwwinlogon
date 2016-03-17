/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.lib;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import egwwinlogon.dokan.lib.win.SecurityDescriptor;
import java.util.Arrays;
import java.util.List;

/**
 * DokanAccessState
 * @author Stefan Werfling
 */
public class DokanAccessState extends Structure {
	
	public boolean securityEvaluated;
	public boolean generateAudit;
	public boolean generateOnClose;
	public boolean auditPrivileges;
	public NativeLong flags;
	public int remainingDesiredAccess;
	public int previouslyGrantedAccess;
	public int originalDesiredAccess;
	public SecurityDescriptor securityDescriptor;
	public WString objectName;
	public WString objectType;
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"securityEvaluated", 
			"generateAudit",
			"generateOnClose",
			"auditPrivileges",
			"flags",
			"remainingDesiredAccess",
			"previouslyGrantedAccess",
			"originalDesiredAccess",
			"securityDescriptor",
			"objectName",
			"objectType",
			});
	}
	
	/*public static class Reference extends DokanAccessState implements ByReference {
    }*/
}
