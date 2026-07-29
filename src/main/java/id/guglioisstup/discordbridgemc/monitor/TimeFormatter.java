package id.guglioisstup.discordbridgemc.monitor;

public final class TimeFormatter {
    private TimeFormatter() {}

    public static String format(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }

        return minutes + "m";
    }
}