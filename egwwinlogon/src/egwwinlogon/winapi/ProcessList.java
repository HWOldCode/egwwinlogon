/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * ProcessList
 * @author Stefan Werfling
 */
public class ProcessList {
    
	/**
	 * windowsProcessId
	 * @source https://github.com/flapdoodle-oss/de.flapdoodle.embed.process/blob/master/src/main/java/de/flapdoodle/embed/process/runtime/Processes.java
	 * @param process
	 * @return 
	 */
	public static Long windowsProcessId(Process process) {
		if( process.getClass().getName().equals("java.lang.Win32Process") || 
			process.getClass().getName().equals("java.lang.ProcessImpl") ) 
		{
			/* determine the pid on windows plattforms */
			try {
				
				Field f = process.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				
				long handl = f.getLong(process);

				Kernel32 kernel = Kernel32.INSTANCE;
				WinNT.HANDLE handle = new WinNT.HANDLE();
				handle.setPointer(Pointer.createConstant(handl));
				
				int ret = kernel.GetProcessId(handle);
				
				return Long.valueOf(ret);
			} 
			catch( Throwable e ) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
    /**
     * getProcessList
     * @return 
     */
    public static List<ProcessInfo> getProcessList() {
        List<ProcessInfo> processList = new ArrayList<ProcessInfo>();
        
        Kernel32 kernel32 = (Kernel32) Native.loadLibrary(
            Kernel32.class, 
            W32APIOptions.UNICODE_OPTIONS
            );
        
        Tlhelp32.PROCESSENTRY32.ByReference processEntry = 
            new Tlhelp32.PROCESSENTRY32.ByReference();
        
        WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(
            Tlhelp32.TH32CS_SNAPPROCESS, 
            new WinDef.DWORD(0)
            );
        
        try {
            while( kernel32.Process32Next(snapshot, processEntry) ) {
                processList.add(new ProcessInfo(
                        processEntry.th32ProcessID.intValue(),
                        processEntry.th32ParentProcessID.intValue(),
                        Native.toString(processEntry.szExeFile)
                    ));
            }
        }
        finally {
            kernel32.CloseHandle(snapshot);
        }
        
        return processList;
    }
    
    /**
     * existProcessById
     * @param pid
     * @return
     */
    static public boolean existProcessById(int pid) {
        List<ProcessInfo> processList = ProcessList.getProcessList();
        
        for( ProcessInfo process: processList ) {
            if( process.getProcessId() == pid ) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * ProcessInfo
     */
    public static final class ProcessInfo {
        
        /**
         * process id
         */
        protected int _processid = 0;
        
        /**
         * parent process id
         */
        protected int _parentProcessid = 0;
        
        /**
         * exefile
         */
        protected String _exefile = "";
        
        /**
         * ProcessInfo
         * 
         * @param processid
         * @param parentProcessid
         * @param exefile 
         */
        public ProcessInfo(int processid, int parentProcessid, String exefile) {
            this._processid = processid;
            this._parentProcessid = parentProcessid;
            this._exefile = exefile;
        }
        
        /**
         * getProcessId
         * @return 
         */
        public int getProcessId() {
            return this._processid;
        }
        
        /**
         * getProcessExeFile
         * @return 
         */
        public String getProcessExeFile() {
            return _exefile;
        }
    }
}
