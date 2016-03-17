/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.dokan.lib;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * DokanIOSecurityContext
 * @author Stefan Werfling
 */
public class DokanIOSecurityContext extends Structure {

	public DokanAccessState accessState;
	public int desiredAccess;
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"accessState",
			"desiredAccess"
			});
	}
	
	public static class Reference extends DokanIOSecurityContext implements ByReference {
    }
}
