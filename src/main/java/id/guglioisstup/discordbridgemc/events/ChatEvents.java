package id.guglioisstup.discordbridgemc.events;

import id.guglioisstup.discordbridgemc.config.ConfigManager;
import id.guglioisstup.discordbridgemc.discord.DiscordBot;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

public final class ChatEvents {
    private ChatEvents() {
    }

    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register((message, player, params) -> {
            if (!ConfigManager.get().relayMinecraftChat) {
                return;
            }

            String username = player.getName().getString();
            String content = message.decoratedContent().getString();

            if (content.startsWith("[Discord]")) {
                return;
            }

            DiscordBot.sendChatMessage(username, content);
        });
    }
}