package id.guglioisstup.discordbridgemc.monitor;

public final class TimeFormatter {
    private TimeFormatter() {}

    public static String format(long seconds) {
        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;

        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m";
        }

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }

        return minutes + "m";
    }
}