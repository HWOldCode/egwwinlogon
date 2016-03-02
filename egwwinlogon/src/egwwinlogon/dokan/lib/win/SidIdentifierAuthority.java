package egwwinlogon.dokan.lib.win;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * User: Mykola.Polubutkin
 * Date: 10/11/12
 * Time: 2:37 PM
 */
public class SidIdentifierAuthority extends Structure {
    public byte[] value = new byte[6];

	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"value"
		});
	}
}
