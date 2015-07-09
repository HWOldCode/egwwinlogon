/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

/* JNA imports. */
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 *
 * @author swe
 */
public interface Kernel32 extends StdCallLibrary, com.sun.jna.platform.win32.Kernel32 {
    
    /** Instance of KERNEL32.DLL for use in accessing native functions. */
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary(
            "kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Retrieves the full name of the executable image for the specified
     * process.
     *
     * @param hProcess Handle to the process.
     * @param dwFlags Type of path format to return.
     * @param lpExeName On output, the path to the executable image.
     * @param lpdwSize On input, the size of lpExeName.  On success, the
     *  number of characters written to the buffer.
     * @return true if the function succeeds, false otherwise.
     */
    boolean QueryFullProcessImageName(
            HANDLE hProcess, DWORD dwFlags,
            char[] lpExeName, IntByReference lpdwSize);
}