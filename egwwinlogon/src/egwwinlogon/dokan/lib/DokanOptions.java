package egwwinlogon.dokan.lib;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import java.util.Arrays;
import java.util.List;

public class DokanOptions extends Structure {
	
    public static final int DOKAN_OPTION_DEBUG			= 1;	// ouput debug message
    public static final int DOKAN_OPTION_STDERR			= 2;	// ouput debug message to stderr
	public static final int DOKAN_OPTION_ALT_STREAM		= 4;	// use alternate stream
    public static final int DOKAN_OPTION_WRITE_PROTECT	= 8;	// mount drive as write-protected.
    public static final int DOKAN_OPTION_NETWORK		= 16;	// use network drive, you need to
																// install Dokan network provider.
    public static final int DOKAN_OPTION_REMOVABLE		= 32;	// use removable drive

    public short version;
    public short threadCount;
    public NativeLong options;
    public long globalContext;
    public WString mountPoint;
	public NativeLong timeout;
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"version",
			"threadCount",
			"options",
			"globalContext",
			"mountPoint",
			"timeout"
		});
	}

    public static class Reference extends DokanOptions implements ByReference {
    }

    public DokanOptions() {
    }

	/**
	 * constructor
	 * @param version
	 * @param threadCount
	 * @param options
	 * @param globalContext
	 * @param mountPoint 
	 */
    public DokanOptions(short version, short threadCount, NativeLong options, long globalContext, WString mountPoint) {
        this.version		= version;
        this.threadCount	= threadCount;
        this.options		= options;
        this.globalContext	= globalContext;
        this.mountPoint		= mountPoint;
		//this.timeout		= 100;
    }
}
