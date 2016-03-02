package egwwinlogon.dokan.lib;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import egwwinlogon.dokan.lib.win.ByHandleFileInformation;
import egwwinlogon.dokan.lib.win.SecurityDescriptor;
import egwwinlogon.dokan.lib.win.Win32FindStreamData;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class DokanOperations extends Structure {

    public CreateFileCallback createFileCallback;
    //public OpenDirectoryCallback openDirectoryCallback;
    //public CreateDirectoryCallback createDirectoryCallback;
    public CleanupCallback cleanupCallback;
    public CloseFileCallback closeFileCallback;
    public ReadFileCallback readFileCallback;
    public WriteFileCallback writeFileCallback;
    public FlushFileBuffersCallback flushFileBuffersCallback;
    public GetFileInformationCallback getFileInformationCallback;
    public FindFilesCallback findFilesCallback;
    
	public FindFilesWithPatternCallback findFilesWithPatternCallback;
    
	public SetFileAttributesCallback setFileAttributesCallback;
    public SetFileTimeCallback setFileTimeCallback;
    public DeleteFileCallback deleteFileCallback;
    public DeleteDirectoryCallback deleteDirectoryCallback;
    public MoveFileCallback moveFileCallback;
    public SetEndOfFileCallback setEndOfFileCallback;
    public SetAllocationSizeCallback setAllocationSizeCallback;
    public LockFileCallback lockFileCallback;
    public UnlockFileCallback unlockFileCallback;
    public GetDiskFreeSpaceCallback getDiskFreeSpaceCallback;
    public GetVolumeInformationCallback getVolumeInformationCallback;
	public MountCallback mountCallback;
    public UnmountCallback unmountCallback;
    public GetFileSecurityCallback getFileSecurityCallback;
    public SetFileSecurityCallback setFileSecurityCallback;

	public FindStreamsCallback findStreamsCallback;
	
    public DokanOperations() {
    }

	/**
	 * getFieldOrder
	 * @return 
	 */
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]{
			"createFileCallback",
			"cleanupCallback",
			"closeFileCallback",
			"readFileCallback",
			"writeFileCallback",
			"flushFileBuffersCallback",
			"getFileInformationCallback",
			"findFilesCallback",
			
			//"openDirectoryCallback",
			//"createDirectoryCallback",
			
			"findFilesWithPatternCallback",
			
			"setFileAttributesCallback",
			"setFileTimeCallback",
			"deleteFileCallback",
			"deleteDirectoryCallback",
			"moveFileCallback",
			"setEndOfFileCallback",
			"setAllocationSizeCallback",
			"lockFileCallback",
			"unlockFileCallback",
			"getDiskFreeSpaceCallback",
			"getVolumeInformationCallback",
			"mountCallback",
			"unmountCallback",
			
			"getFileSecurityCallback",
			"setFileSecurityCallback",
			
			"findStreamsCallback"
        });
	}

    public interface CreateFileCallback extends Callback {

        public static int FLAG_BACKUP_SEMANTICS = 0x02_00_00_00;
        public static int FLAG_DELETE_ON_CLOSE = 0x04_00_00_00;
        public static int FLAG_NO_BUFFERING = 0x20_00_00_00;
        public static int FLAG_OPEN_NO_RECALL = 0x00_10_00_00;
        public static int FLAG_OPEN_REPARSE_POINT = 0x00_20_00_00;
        public static int FLAG_OVERLAPPED = 0x40_00_00_00;
        public static int FLAG_POSIX_SEMANTICS = 0x00_10_00_00;
        public static int FLAG_RANDOM_ACCESS = 0x10_00_00_00;
        public static int FLAG_SESSION_AWARE = 0x00_80_00_00;
        public static int FLAG_SEQUENTIAL_SCAN = 0x08_00_00_00;
        public static int FLAG_WRITE_THROUGH = 0x80_00_00_00;

        public static int ACCESS_GENERIC_ALL = 0x10_00_00_00;
        public static int ACCESS_GENERIC_READ = 0x80_00_00_00;
        public static int ACCESS_GENERIC_WRITE = 0x40_00_00_00;
        public static int ACCESS_GENERIC_EXECUTE = 0x20_00_00_00;

        public static int CREATE_NEW = 0x1;
        public static int CREATE_ALWAYS = 0x2;
        public static int OPEN_ALWAYS = 0x4;
        public static int OPEN_EXISTING = 0x3;
        public static int TRUNCATE_EXISTING = 0x5;

        public static int SHARE_DELETE = 0x4;
        public static int SHARE_READ = 0x1;
        public static int SHARE_WRITE = 0x2;

        int invoke(WString path, int desiredAccess, int shareMode, int creationDisposition, int flagsAndAttributes, DokanFileInfo dokanFileInfo);
    }

    public interface OpenDirectoryCallback extends Callback {
        int invoke(WString path, DokanFileInfo dokanFileInfo);
    }

    public interface CreateDirectoryCallback extends Callback {
        int invoke(WString path, DokanFileInfo dokanFileInfo);
    }

    public interface CleanupCallback extends Callback {
        int invoke(WString path, DokanFileInfo dokanFileInfo);
    }

    public interface CloseFileCallback extends Callback {
        int invoke(WString path, DokanFileInfo dokanFileInfo);
    }

    public interface ReadFileCallback extends Callback {
        int invoke(WString path, Pointer buffer, int numberOfBytesToRead, IntByReference numberOfBytesRead, long offset, DokanFileInfo dokanFileInfo);
    }

    public interface WriteFileCallback extends Callback {
        int invoke(WString path, Pointer buffer, int numberOfBytesToWrite, IntByReference numberOfBytesWritten, long offset, DokanFileInfo dokanFileInfo);
    }

    public interface FlushFileBuffersCallback extends Callback {
        int invoke(WString path, DokanFileInfo dokanFileInfo);
    }

    public interface GetFileInformationCallback extends Callback {
        int invoke(WString path, ByHandleFileInformation info, DokanFileInfo dokanFileInfo);
    }

    public interface FindFilesCallback extends Callback {
        int invoke(WString path, DokanLibrary.FillFindDataCallback fillFindDataCallback, DokanFileInfo dokanFileInfo) throws UnsupportedEncodingException;
    }

    public interface FindFilesWithPatternCallback extends Callback {
        int invoke(WString fileName, WString searchPattern, DokanLibrary.FillFindDataCallback fillFindDataCallback, DokanFileInfo dokanFileInfo);
    }

    public interface SetFileAttributesCallback extends Callback {
        int invoke(WString path, int fileAttributes, DokanFileInfo dokanFileInfo);
    }

    public interface SetFileTimeCallback extends Callback {
        int invoke(WString path, FILETIME creationTime, FILETIME lastAccessTime, FILETIME lastWriteTime, DokanFileInfo dokanFileInfo);
    }

    public interface DeleteFileCallback extends Callback {
        int invoke(WString path, DokanFileInfo dokanFileInfo);
    }

    public interface DeleteDirectoryCallback extends Callback {
        int invoke(WString path, DokanFileInfo dokanFileInfo);
    }

    public interface MoveFileCallback extends Callback {
        int invoke(WString existingPath, WString newPath, boolean replaceExisting, DokanFileInfo dokanFileInfo);
    }

    public interface SetEndOfFileCallback extends Callback {
        int invoke(WString path, long length, DokanFileInfo dokanFileInfo);
    }

    public interface SetAllocationSizeCallback extends Callback {
        int invoke(WString path, long length, DokanFileInfo dokanFileInfo);
    }

    public interface LockFileCallback extends Callback {
        int invoke(WString path, long byteOffset, long length, DokanFileInfo dokanFileInfo);
    }

    public interface UnlockFileCallback extends Callback {
        int invoke(WString path, long byteOffset, long length, DokanFileInfo dokanFileInfo);
    }

    public interface GetDiskFreeSpaceCallback extends Callback {
        int invoke(LongByReference freeBytesAvailable, LongByReference totalNumbersOfBytes, LongByReference totalNumberOfFreeBytes, DokanFileInfo dokanFileInfo);
    }

    public interface GetVolumeInformationCallback extends Callback {
        int invoke(Pointer volumeNameBuffer, int volumeNameSize, IntByReference volumeSerialNumber, IntByReference maximumComponentLength, IntByReference fileSystemFlags, Pointer fileSystemNameBuffer, int fileSystemNameSize, DokanFileInfo dokanFileInfo);
    }

	public interface MountCallback extends Callback {
        int invoke(DokanFileInfo dokanFileInfo);
    }
	
    public interface UnmountCallback extends Callback {
        int invoke(DokanFileInfo dokanFileInfo);
    }

    public interface GetFileSecurityCallback extends Callback {
        int invoke(WString path, IntByReference securityInfo, SecurityDescriptor securityDescriptor, NativeLong bufferLength, LongByReference lengthNeeded, DokanFileInfo dokanFileInfo);
    }

    public interface SetFileSecurityCallback extends Callback {
        int invoke(WString path, IntByReference pSecurityInformation, SecurityDescriptor securityDescriptor, NativeLong bufferLength, LongByReference lengthNeeded, DokanFileInfo dokanFileInfo);
    }
	
	public interface FindStreamsCallback extends Callback {
		int invoke(WString path, Win32FindStreamData findStreamData, DokanFileInfo dokanFileInfo);
	}
}
