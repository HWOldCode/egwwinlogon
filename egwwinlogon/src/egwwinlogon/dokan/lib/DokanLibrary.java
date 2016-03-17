package egwwinlogon.dokan.lib;

import egwwinlogon.dokan.lib.DokanFileInfo;
import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import egwwinlogon.dokan.lib.win.Win32FindData;

/**
 * https://github.com/dokan-dev/dokany/blob/master/dokan/dokan.h
 * https://github.com/sainaen/JDokan/blob/master/src/main/java/ua/com/infopulse/jdokan/virtualfs/VirtualFileSystem.java
 * @author Stefan Werfling
 */
public interface DokanLibrary extends Library {
	
    public static final String JNA_LIBRARY_NAME			= "dokan1";
	
    public static final NativeLibrary JNA_NATIVE_LIB	= NativeLibrary.getInstance(DokanLibrary.JNA_LIBRARY_NAME);
    public static final DokanLibrary INSTANCE			= (DokanLibrary) Native.loadLibrary(DokanLibrary.JNA_LIBRARY_NAME, DokanLibrary.class);

    public static final int DOKAN_VERSION				= 800;

    public static final int DOKAN_SUCCESS				= 0;

    public static final int DOKAN_ERROR					= -1;
    public static final int DOKAN_DRIVE_LETTER_ERROR	= -2;
    public static final int DOKAN_DRIVER_INSTALL_ERROR	= -3;
    public static final int DOKAN_START_ERROR			= -4;
    public static final int DOKAN_MOUNT_ERROR			= -5;
    public static final int DOKAN_MOUNT_POINT_ERROR		= -6;
	
    public interface FillFindDataCallback extends Callback {
        int invoke(Win32FindData findData, DokanFileInfo pDokanFileInfo);
    }

    int DokanMain(DokanOptions dokanOptions, DokanOperations dokanOperations);

    boolean DokanUnmount(String driveLetter);

    boolean DokanRemoveMountPoint(String mountPoint);

    boolean DokanIsNameInExpression(String expression, String name, boolean ignoreCase);

    long DokanVersion();

    NativeLong DokanDriverVersion();

    boolean DokanResetTimeout(NativeLong Timeout, DokanFileInfo dokanFileInfo);

    int DokanOpenRequestorToken(DokanFileInfo dokanFileInfo);
	
	void DokanMapKernelToUserCreateFileFlags(int FileAttributes, int CreateOptions, int CreateDisposition, IntByReference outFileAttributesAndFlags, IntByReference outCreationDisposition);
}