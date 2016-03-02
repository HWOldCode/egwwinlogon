package egwwinlogon.dokan.lib;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import egwwinlogon.dokan.lib.DokanOptions;

public class DokanFileInfo extends Structure {
    public long context;
    public long dokanContext;
    public DokanOptions.Reference dokanOptions;
    public NativeLong processId;
    public byte isDirectory;
    public byte deleteOnClose;
    public byte pagingIo;
    public byte synchronousIo;
    public byte noCache;
    public byte writeToEndOfFile;

	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{"context", "dokanContext", "dokanOptions", "processId", "isDirectory", "deleteOnClose", "pagingIo", "synchronousIo", "noCache", "writeToEndOfFile"});
	}
}
