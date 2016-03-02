package egwwinlogon.dokan.lib.win;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import java.util.Arrays;
import java.util.List;

public class ByHandleFileInformation extends Structure {
    public int attributes;
    public FILETIME creationTime;
    public FILETIME lastAccessTime;
    public FILETIME lastWriteTime;
    public int volumeSerialNumber;
    public int fileSizeHigh;
    public int fileSizeLow;
    public int numberOfLinks;
    public int fileIndexHigh;
    public int fileIndexLow;

	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"attributes",
			"creationTime",
			"lastAccessTime",
			"lastWriteTime",
			"volumeSerialNumber",
			"fileSizeHigh",
			"fileSizeLow",
			"numberOfLinks",
			"fileIndexHigh",
			"fileIndexLow"
		});
	}
}
