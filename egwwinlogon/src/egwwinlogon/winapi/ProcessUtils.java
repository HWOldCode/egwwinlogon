/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

/* Java imports. */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* JNA imports. */
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * ProcessUtils
 * @see https://github.com/malyn/jnaplatext/blob/master/src/main/java/com/michaelalynmiller/jnaplatext/win32/ProcessUtils.java
 * @author Stefan Werfling
 */
public class ProcessUtils {
    
    public ProcessUtils() {
        
    }
    
    public static List<ProcessInfo> getProcessList() throws Exception {
        /* Initialize the empty process list. */
        List<ProcessInfo> processList = new ArrayList<ProcessInfo>();

        /* Create the process snapshot. */
        HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(
                Tlhelp32.TH32CS_SNAPPROCESS, new DWORD(0));

        Tlhelp32.PROCESSENTRY32.ByReference pe
            = new Tlhelp32.PROCESSENTRY32.ByReference();
        for (boolean more = Kernel32.INSTANCE.Process32First(snapshot, pe);
                more;
                more = Kernel32.INSTANCE.Process32Next(snapshot, pe)) {
            /* Open this process; ignore processes that we cannot open. */
            HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(
                    0x1000, /* PROCESS_QUERY_LIMITED_INFORMATION */
                    false,
                    pe.th32ProcessID.intValue());
            if (hProcess == null) {
                continue;
            }

            /* Get the image name. */
            char[] imageNameChars = new char[1024];
            IntByReference imageNameLen
                = new IntByReference(imageNameChars.length);
            if (!Kernel32.INSTANCE.QueryFullProcessImageName(
                    hProcess, new DWORD(0), imageNameChars, imageNameLen)) {
                throw new Exception("Couldn't get process image name for "
                        + pe.th32ProcessID.intValue());
            }

            /* Add the process info to our list. */
            processList.add(new ProcessInfo(
                pe.th32ProcessID.intValue(),
                pe.th32ParentProcessID.intValue(),
                new String(imageNameChars, 0, imageNameLen.getValue())));

            /* Close the process handle. */
            Kernel32.INSTANCE.CloseHandle(hProcess);
        }

        /* Close the process snapshot. */
        Kernel32.INSTANCE.CloseHandle(snapshot);

        /* Return the process list. */
        return processList;
    }
    
    /**
     * existProcessById
     * @param pid
     * @return
     * @throws Exception 
     */
    static public boolean existProcessById(int pid) throws Exception {
        List<ProcessInfo> processList = ProcessUtils.getProcessList();
        
        for( ProcessInfo process: processList ) {
            if( process.getProcessId() == pid ) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Stores the information about a Win32 process.
     */
    public static final class ProcessInfo {
        /** Process id. */
        private int processId;

        /** Parent process id. */
        private int parentProcessId;

        /** Path to this process's image. */
        private String imageName;

        /**
         * Constructs a new ProcessInfo object.
         *
         * @param processId Process id.
         * @param parentProcessId Parent process id.
         * @param imageName Process image name.
         */
        public ProcessInfo(
                final int processId,
                final int parentProcessId,
                final String imageName) {
            this.processId = processId;
            this.parentProcessId = parentProcessId;
            this.imageName = imageName;
        }

        /**
         * Returns the process id.
         *
         * @return The process id.
         */
        public int getProcessId() {
            return processId;
        }

        /**
         * Returns the parent process id.
         *
         * @return The parent process id.
         */
        public int getParentProcessId() {
            return parentProcessId;
        }

        /**
         * Returns the image name.
         *
         * @return The image name.
         */
        public String getImageName() {
            return imageName;
        }
    }
}
