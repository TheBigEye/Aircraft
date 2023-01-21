package minicraft.util;

public final class Utils {   
	
	public static final String OS_NAME = System.getProperty("os.name"); // The Operating system name (Windows, Linux, MacOS, etc)
	public static final String OS_ARCH = System.getProperty("os.arch"); // The model type of the Operating system (ARM, x86, x64, AMD64,
	public static final String OS_VERSION = System.getProperty("os.version"); // This is ovbious, stores the Operating system version
	
	public static final String JAVA_ARCH = System.getProperty("sun.arch.data.model"); // This store the JDK / JRE model
	public static final String JAVA_VERSION = System.getProperty("java.version"); // This store the JDK / JRE version
	public static final String JAVA_VENDOR = System.getProperty("java.vendor");  // This store the company responsible for maintaining java
	
	public static final String JVM_NAME = System.getProperty("java.vm.name"); // Details of the current Java virtual machine
	public static final String JVM_INFO = System.getProperty("java.vm.info"); // Info of the current Java virtual machine
	public static final String JVM_VENDOR = System.getProperty("java.vm.vendor"); // meh.. lot text

    public static int clamp(int val, int min, int max) {
        if (val > max) return max;
        if (val < min)  return min; 
        return val;
    }

    public static int randInt(int max) {
        return randInt(0, max);
    }

    public static int randInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static String plural(int num, String word) {
        String p = (num == 1) ? "" : "s";
        return num + " " + word + p;
    }

    /** Sleeps the current thread **/
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

	public static int percentage(int current, int total) {
		double percent = (double)(current * 100)/total;
		int intPercent = (int) (percent + 0.5); // Adds .5 in order to round.
		return intPercent;
	}
		
	/** Get the free JVM memory in Mbs **/
	public static long freeMemory() {
	    return Runtime.getRuntime().freeMemory() / (1024 * 1024);
	}

	/** Get the the total JVM used memory in Mbs **/
	public static long totalMemory() {
	    return Runtime.getRuntime().totalMemory() / (1024 * 1024);
	}

	/** Get the max JVM memory in Mbs **/
	public static long maxMemory() {
	    return Runtime.getRuntime().maxMemory() / (1024 * 1024);
	}
	
	/** Get the heap memory in Mbs **/
	public static long heapMemory() {
	    return (Runtime.getRuntime().totalMemory() / (1024 * 1024)) - (Runtime.getRuntime().freeMemory() / (1024 * 1024));
	}
	
	/** Get the heap memory usage percent **/
	public static int heapMemoryUsage() {
	    return (int) (Long.valueOf(heapMemory()) * 100 / Long.valueOf(maxMemory()));
	}

	public static String memoryInfo() {
	    long maxMem = maxMemory();
	    long totalMem = totalMemory();
	    long freeMem = freeMemory();
	    return String.format(
	    	"%d bytes (%d MB) / %d bytes (%d MB) up to %d bytes (%d MB)", freeMem * (1024 * 1024), freeMem, totalMem * (1024 * 1024), totalMem, maxMem * (1024 * 1024), maxMem
	    );
	}
}