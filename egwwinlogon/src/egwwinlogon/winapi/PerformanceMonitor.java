/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi;

import com.sun.management.OperatingSystemMXBean;
import static sun.management.ManagementFactoryHelper.getOperatingSystemMXBean;

/**
 * PerformanceMonitor
 * @author swe
 */
public class PerformanceMonitor {
    private int  availableProcessors = getOperatingSystemMXBean().getAvailableProcessors();
    private long lastSystemTime      = 0;
    private long lastProcessCpuTime  = 0;

    public synchronized double getCpuUsage() {
        if( this.lastSystemTime == 0 ) {
            baselineCounters();
            return 0;
        }

        long systemTime     = System.nanoTime();
        long processCpuTime = 0;

        if( getOperatingSystemMXBean() instanceof OperatingSystemMXBean ) {
            processCpuTime = ( (OperatingSystemMXBean)
                getOperatingSystemMXBean() ).getProcessCpuTime();
        }

        double cpuUsage = (double) ( processCpuTime -
            this.lastProcessCpuTime ) / ( systemTime - this.lastSystemTime );

        this.lastSystemTime     = systemTime;
        this.lastProcessCpuTime = processCpuTime;

        return cpuUsage / this.availableProcessors;
    }

    private void baselineCounters() {
        this.lastSystemTime = System.nanoTime();

        if ( getOperatingSystemMXBean() instanceof OperatingSystemMXBean )
        {
            this.lastProcessCpuTime = ( (OperatingSystemMXBean)
                getOperatingSystemMXBean() ).getProcessCpuTime();
        }
    }
}
