package id.guglioisstup.discordbridgemc.monitor;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class TpsMonitor {

    private static long ticks = 0;
    private static long lastTime = System.currentTimeMillis();

    private static double tps = 20.0;
    private static double mspt = 50.0;

    private static MinecraftServer server;

    public static void register() { // i couldnt get the actual tick function to get the right value ??
        ServerTickEvents.END_SERVER_TICK.register(serverInstance -> {
            server = serverInstance;

            ticks++;

            long now = System.currentTimeMillis();

            if (now - lastTime >= 1000) {
                long elapsed = now - lastTime;

                tps = ticks * 1000.0 / elapsed;

                if (tps > 20) {
                    tps = 20;
                }

                mspt = elapsed / (double) ticks;

                ticks = 0;
                lastTime = now;
            }
        });
    }

    public static double getTPS() {
        return Math.round(tps * 100.0) / 100.0;
    }

    public static double getMSPT() {
        return Math.round(mspt * 100.0) / 100.0;
    }
}
