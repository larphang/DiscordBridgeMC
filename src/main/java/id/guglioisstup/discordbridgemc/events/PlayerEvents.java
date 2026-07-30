package id.guglioisstup.discordbridgemc.events;

import id.guglioisstup.discordbridgemc.database.dao.PlayerDao;
import id.guglioisstup.discordbridgemc.player.PlayerSessionManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerEvents {
    private PlayerEvents() { }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            String username = player.getName().getString();

            boolean firstJoin = !PlayerDao.exists(player.getUUID());

            PlayerDao.create(
                player.getUUID(),
                username
            );

            PlayerDao.updateUsername(
                player.getUUID(),
                username
            );

            PlayerDao.updateLastSeen(
                player.getUUID()
            );

            PlayerSessionManager.startSession(player);

            PlayerDao.updateSessionStart(
                player.getUUID(),
                System.currentTimeMillis()
            );

            if (firstJoin) {
                player.sendSystemMessage(Component.literal("haiiii make sure to link your discord with /linkdiscord :3"));
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.player;

            long sessionStart = PlayerDao.getSessionStart(player.getUUID());

            if (sessionStart > 0) {
                long seconds = (System.currentTimeMillis() - sessionStart) / 1000;

                PlayerDao.addPlaytime(player.getUUID(), seconds);
                PlayerDao.updateSessionStart(player.getUUID(), 0);
            }

            PlayerDao.updateLastSeen(player.getUUID());

            PlayerSessionManager.endSession(player.getUUID());
        });
    }
}