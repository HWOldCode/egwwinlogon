package egwwinlogon.dokan.lib.win;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * User: Mykola.Polubutkin
 * Date: 10/11/12
 * Time: 2:28 PM
 */

public class SecurityDescriptor extends Structure {
    public byte revision;
    public byte sbz1;
    public short control; //SECURITY_DESCRIPTOR_CONTROL
    public Sid owner;
    public Sid group;
    public Acl sacl;
    public Acl dacl;

	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"revision",
			"sbz1",
			"control",
			"owner",
			"group",
			"sacl",
			"dacl"
		});
	}

    public static class Reference extends SecurityDescriptor implements ByReference {
    }

    public SecurityDescriptor() {
    }
}
