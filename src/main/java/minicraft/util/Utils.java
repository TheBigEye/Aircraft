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
	
	private static final Runtime runtime = Runtime.getRuntime();

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
    
    public static String formatText(String word) {
        String[] words = word.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String stringWord : words) {
            sb.append(stringWord.substring(0, 1).toUpperCase());
            sb.append(stringWord.substring(1));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /** Sleeps the current thread **/
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

    public static int percentage(int current, int total) {
        return (current * 100) / total;
    }

    

    /** Get the free JVM memory in Mbs **/
    public static long freeMemory() {
        return runtime.freeMemory() >> 20;
    }

    /** Get the the total JVM used memory in Mbs **/
    public static long totalMemory() {
        return runtime.totalMemory() >> 20;
    }

    /** Get the max JVM memory in Mbs **/
    public static long maxMemory() {
        return runtime.maxMemory() >> 20;
    }

    /** Get the heap memory in Mbs **/
    public static long heapMemory() {
        return (runtime.totalMemory() >> 20) - (runtime.freeMemory() >> 20);
    }

    /** Get the non-heap memory in Mbs **/
    public static long nonHeapMemory() {
        return (runtime.maxMemory() >> 20) - (runtime.totalMemory() >> 20);
    }

    /** Get the heap memory usage percent **/
    public static int heapMemoryUsage() {
        return (int) (heapMemory() * 100 / maxMemory());
    }

    /** Get the non-heap memory usage percent **/
    public static int nonHeapMemoryUsage() {
        return (int) (nonHeapMemory() * 100 / maxMemory());
    }

    public static String getGeneralMemoryUsage() {
        long usedMemory = totalMemory() - freeMemory();
        int usagePercentage = (int) ((usedMemory * 100) / maxMemory());
        return String.format("M: %d%% %d/%d", usagePercentage, usedMemory, maxMemory());
    }

    public static String getMemoryAllocationRate() {
        long heapAllocationRate = heapMemory() / 1024; // Convert to MiB
        return String.format("R: %03dMB /s", heapAllocationRate);
    }

    public static String getMemoryAllocation() {
        int allocationPercentage = (int) ((totalMemory() * 100) / maxMemory());
        return String.format("A: %d%% %dMB", allocationPercentage, totalMemory());
    }

	public static String memoryInfo() {
	    long maxMem = maxMemory();
	    long totalMem = totalMemory();
	    long freeMem = freeMemory();
	    return String.format(
	        "%d bytes (%d MB) / %d bytes (%d MB) up to %d bytes (%d MB)", 
	        freeMem << 20, freeMem, 
	        totalMem << 20, totalMem, 
	        maxMem << 20, maxMem
	    );
	}
}