package id.guglioisstup.discordbridgemc.monitor;

public class SystemMonitor {
    public static String getMemoryStatus() {
        long used = MemoryInfo.getUsedMemory();
        long max = MemoryInfo.getMaxMemory();

        return MemoryInfo.formatBytes(used) + " / " + MemoryInfo.formatBytes(max);
    }
}