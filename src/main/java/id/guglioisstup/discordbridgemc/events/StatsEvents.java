package id.guglioisstup.discordbridgemc.events;

import id.guglioisstup.discordbridgemc.database.dao.PlayerDao;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerPlayer;

public final class StatsEvents {
    private StatsEvents() { }

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(
            (world, player, pos, state, blockEntity) -> {
                PlayerDao.addStat(
                    player.getUUID(),
                    "blocks_broken",
                    1
                );
            }
        );

        ServerLivingEntityEvents.AFTER_DEATH.register(
            (entity, damageSource) -> {
                if (entity instanceof ServerPlayer player) {
                    PlayerDao.addStat(
                        player.getUUID(),
                        "deaths",
                        1
                    );
                }

                if (damageSource.getEntity() instanceof ServerPlayer player) {
                    if (entity instanceof ServerPlayer) {
                        PlayerDao.addStat(
                            player.getUUID(),
                            "player_kills",
                            1
                        );
                    } else {
                        PlayerDao.addStat(
                            player.getUUID(),
                            "mob_kills",
                            1
                        );
                    }
                }
            }
        );
    }
}