package id.guglioisstup.discordbridgemc.discord;

import id.guglioisstup.discordbridgemc.DiscordBridgeMC;
import id.guglioisstup.discordbridgemc.config.Config;
import id.guglioisstup.discordbridgemc.config.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public final class DiscordBot {
    private static JDA jda;

    private DiscordBot() {
    }

    public static void start() {
        if (jda != null) {
            DiscordBridgeMC.LOGGER.warn("Discord bot is already running.");
            return;
        }

        Config config = ConfigManager.get();

        if (config.discord.token.isBlank()) {
            DiscordBridgeMC.LOGGER.warn("Discord bot token is not configured.");
            return;
        }

        try {
            DiscordBridgeMC.LOGGER.info("Starting Discord bot...");

            jda = JDABuilder.createDefault(config.discord.token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new DiscordListener())
                    .build()
                    .awaitReady();
            
            SlashCommands.register(jda);

            DiscordBridgeMC.LOGGER.info(
                "Logged into Discord as {}",
                jda.getSelfUser().getAsTag()
            );

        } catch (Exception e) {
            DiscordBridgeMC.LOGGER.error("Failed to start Discord bot.", e);
            jda = null;
        }
    }

    public static void sendChatMessage(String player, String message) {
        send( "<" + player + "> " + message);
    }

    public static void sendJoinMessage(String player) {
        send(player + " has joined the server");
    }

    public static void sendLeaveMessage(String player) {
        send(player + " has left the server");
    }

    public static void broadcastMessage(String message) {
        send(message);
    }

    public static void sendAdvancementMessage(String player, String advancement) {
        send(player + " has made the advancement [" + advancement + "]");
    }

    public static void sendServerMessage(String message) {
        send("[SERVER] " + message);
    }

    private static void send(String message) {
        if (jda == null) {
            return;
        }

        Config config = ConfigManager.get();

        TextChannel channel = jda.getTextChannelById(config.discord.channelId);

        if (channel == null) {
            DiscordBridgeMC.LOGGER.warn(
                "Discord channel not found: {}",
                config.discord.channelId
            );
            return;
        }

        channel.sendMessage(message).queue(
            null,
            error -> DiscordBridgeMC.LOGGER.error(
                "Failed to send Discord message",
                error
            )
        );
    }

    public static void shutdown() {
        if (jda == null) {
            return;
        }

        DiscordBridgeMC.LOGGER.info("Shutting down Discord bot...");

        jda.shutdown();
        jda = null;
    }

    public static JDA getJDA() {
        return jda;
    }

    public static boolean isRunning() {
        return jda != null;
    }
}
