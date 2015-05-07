/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.sun.jna.*;
import com.sun.jna.win32.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Win32IdleTime
 * 
 * @see http://ochafik.com/blog/?p=98
 * @author ochafik
 */
public class Win32IdleTime {
    
    /**
     * actionPerformed
     * @param e 
     */
    /*public void actionPerformed(ActionEvent e) {
        int idleSec = getIdleTimeMillisWin32() / 1000;
        System.out.println(idleSec);
 
        //To change body of implemented methods use File | Settings | File Templates.
    }*/
 
    public interface Kernel32 extends StdCallLibrary {
		Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class);
 
		/**
		 * Retrieves the number of milliseconds that have elapsed since the system was started.
		 * @see
		 * @return number of milliseconds that have elapsed since the system was started.
		 */
		public int GetTickCount();
	};
 
    /**
     * User32
     */
    public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32)Native.loadLibrary("user32", User32.class);
 
		/**
		 * Contains the time of the last input.
		 * @see
		 */
		public static class LASTINPUTINFO extends Structure {
			public int cbSize = 8;
 
			/// Tick count of when the last input event was received.
			public int dwTime;

            @Override
            protected List getFieldOrder() {
                return Arrays.asList(new String[]{"cbSize", "dwTime"});
            }
		}
 
		/**
		 * Retrieves the time of the last input event.
		 * @see
		 * @return time of the last input event, in milliseconds
		 */
		public boolean GetLastInputInfo(LASTINPUTINFO result);
	};
 
    /**
     * getIdleTimeMillisWin32
     * @return 
     */
    public static int getIdleTimeMillisWin32() {
		User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
		User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        
		return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
	}  
}