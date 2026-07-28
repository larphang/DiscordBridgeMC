package id.guglioisstup.discordbridgemc.events;

import id.guglioisstup.discordbridgemc.config.ConfigManager;
import id.guglioisstup.discordbridgemc.discord.DiscordBot;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;

public final class BroadcastEvents {
    private BroadcastEvents() {}

    public static void register() {
        ServerMessageEvents.GAME_MESSAGE.register((MinecraftServer server, Component message, boolean overlay) -> {
            if (!ConfigManager.get().relayDeaths) {
                return;
            }

            String eventMessage = message.getString();

            if (eventMessage.startsWith("[Discord]")) {
                return;
            }

            DiscordBot.broadcastMessage(eventMessage);
        });
    }
}
