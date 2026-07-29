package id.guglioisstup.discordbridgemc.player;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSessionManager {
    private static final Map<UUID, Long> sessions = new ConcurrentHashMap<>();

    private PlayerSessionManager() {
    }

    public static void startSession(ServerPlayer player) {
        sessions.put(player.getUUID(), System.currentTimeMillis());
    }

    public static void endSession(UUID uuid) {
        sessions.remove(uuid);
    }

    public static long getSessionTime(UUID uuid) {
        Long start = sessions.get(uuid);

        if (start == null) {
            return 0;
        }

        return (System.currentTimeMillis() - start) / 1000;
    }
}
