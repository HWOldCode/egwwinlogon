package egwwinlogon.dokan.lib.win;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * User: Mykola.Polubutkin
 * Date: 10/11/12
 * Time: 2:36 PM
 */

public class Sid extends Structure {
    public static int ANY_SIZE = 16;

    public byte revision;
    public byte subAuthorityCount;
    public SidIdentifierAuthority identifierAuthority;
    public NativeLong[] subAuthority = new NativeLong[ANY_SIZE];

	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"revision",
			"subAuthorityCount",
			"identifierAuthority",
			"subAuthority"
		});
	}

    public static class Reference extends Sid implements ByReference {
    }

    public Sid() {
    }
}
