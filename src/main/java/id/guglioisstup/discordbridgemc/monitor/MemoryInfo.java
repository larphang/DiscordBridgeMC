package id.guglioisstup.discordbridgemc.monitor;

public class MemoryInfo {
    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();

        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static String formatBytes(long bytes) {
        double mb = bytes / 1024.0 / 1024.0;

        return String.format("%.2f MB", mb);
    }
}
