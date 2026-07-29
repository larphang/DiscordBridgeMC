package id.guglioisstup.discordbridgemc.player;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import id.guglioisstup.discordbridgemc.database.dao.PlayerDao;

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

    public static long getCurrentPlaytime(UUID uuid) {
        long stored = PlayerDao.getPlaytime(uuid);

        long sessionStart = PlayerDao.getSessionStart(uuid);

        if (sessionStart > 0) {
            long currentSession =
                (System.currentTimeMillis() - sessionStart) / 1000;

            return stored + currentSession;
        }

        return stored;
    }
}
