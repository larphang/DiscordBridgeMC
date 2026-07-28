package id.guglioisstup.discordbridgemc.monitor;

public final class UptimeMonitor {
    private static long startTime;
    private UptimeMonitor() {}

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static String getFormatted() {
        long seconds = (System.currentTimeMillis() - startTime) / 1000;

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        return hours + "h " + minutes + "m";
    }
}
