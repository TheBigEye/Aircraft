package minicraft.core;

public class GameInfo {
	
	// RAM used
    public static long max_Memory_bytes; // This store the max JVM memory in bytes
    public static long total_Memory_bytes; // This store the total used memory in bytes
    public static long free_Memory_bytes; // This store the free memory in bytes
    
    public static long max_Memory; // This store the max JVM memory in Mbs
    public static long total_Memory; // This store the total used memory in Mbs
    public static long free_Memory; // This store the free memory in Mbs
    
    // Used in the crash log to get the memory details
    public static String Memory_info;
    
    // System info
    public static String OS_Name; // This store the Operating system name (Windows, Linux, MacOS, etc)
    public static String OS_Arch; // This stores the model type of the Operating system (ARM, x86, x64, AMD64, etc)
    public static String OS_Version; // This is ovbiuos, stores the Operating system version
    
    // Java info
    public static String Java_Arch; // This store the JDK / JRE model
    public static String Java_Version; // This store the JDK / JRE version
    public static String Java_Vendor; // This store the company responsible for maintaining java
    public static String JVM_Name; // Details of the current Java virtual machine
    public static String JVM_Info; // Info of the current Java virtual machine
    public static String JVM_Vendor; // meh.. lot text
	
    // IMPORTANT: This gets all the technical information of the game, such as ram memory,
    // time, and some system specifications such as the operating system and Java version,
    // do not make many modifications, to avoid problems with Crash logs
    
	public static void getInfo(){
		
    	// JVM Runtime Memory, Get information about the RAM memory used
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        max_Memory_bytes = maxMemory; // Max JVM memory (bytes)
        total_Memory_bytes = totalMemory; // Used memory (bytes)
        free_Memory_bytes = freeMemory; // Free memory (bytes)

        max_Memory = maxMemory / 1024L / 1024L; // Max JVM memory (mb)
        total_Memory = totalMemory / 1024L / 1024L; // Used memory (mb)
        free_Memory = freeMemory / 1024L / 1024L; // Free memory (mb)
        
        // Memory details
        Memory_info = free_Memory_bytes + " bytes (" + free_Memory + " MB) / " + total_Memory_bytes + " bytes (" + total_Memory + " MB) up to " + max_Memory_bytes + " bytes (" + max_Memory + " MB)";
  
        
        // Os properties, Get information about the actual OS
        OS_Name = System.getProperty("os.name");
        OS_Arch = System.getProperty("os.arch");
        OS_Version = System.getProperty("os.version");
        
        
        // Java info
        Java_Arch = System.getProperty("sun.arch.data.model");
        Java_Version = System.getProperty("java.version");
        Java_Vendor = System.getProperty("java.vendor");
        JVM_Name = System.getProperty("java.vm.name");
        JVM_Info = System.getProperty("java.vm.info");
        JVM_Vendor = System.getProperty("java.vm.vendor");
           
	}
}
