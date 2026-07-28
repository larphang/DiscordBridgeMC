package id.guglioisstup.discordbridgemc.discord;

import id.guglioisstup.discordbridgemc.DiscordBridgeMC;
import id.guglioisstup.discordbridgemc.monitor.SystemMonitor;
import id.guglioisstup.discordbridgemc.monitor.TpsMonitor;
import id.guglioisstup.discordbridgemc.monitor.UptimeMonitor;
import id.guglioisstup.discordbridgemc.config.ConfigManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.dv8tion.jda.api.Permission;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {

            case "memory":
                event.reply("Memory Usage: " + SystemMonitor.getMemoryStatus()).queue();
                break;
            case "tps":
                event.reply("Current TPS: " + TpsMonitor.getTPS()).queue();
                break;
            case "players":
                if (DiscordBridgeMC.SERVER == null) {
                    event.reply("Server is offline.").queue();
                    break;
                }

                int count = DiscordBridgeMC.SERVER.getPlayerList().getPlayerCount();

                String players = DiscordBridgeMC.SERVER.getPlayerList().getPlayers().stream()
                    .map(player -> {
                        String name = player.getName().getString();
                        return "• " + name + (name.startsWith(".") ? " [BEDROCK] (loser)" : "");
                    })
                    .reduce("", (a, b) -> a + "\n" + b);

                event.reply("Players Online: " + count + "\n" + (count > 0 ? players : "No players online.")).queue();

                break;
            case "uptime":
                event.reply("Server Uptime: " + UptimeMonitor.getFormatted()).queue();
                break;
            case "mspt":
                event.reply("MSPT: " + TpsMonitor.getMSPT() + "ms").queue();
                break;
            case "gc":
                if (event.getMember() == null ||
                    !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

                    event.reply("You do not have permission to use this command.")
                            .setEphemeral(true)
                            .queue();

                    break;
                }

                long before = Runtime.getRuntime().freeMemory();

                System.gc();

                long after = Runtime.getRuntime().freeMemory();
                long freed = (after - before) / 1024 / 1024;

                event.reply("Garbage collection requested.\n" + "Memory change: " + freed + " MB").queue();

                break;
            case "chunks":
                if (DiscordBridgeMC.SERVER == null) {
                    event.reply("Server offline").queue();
                    break;
                }

                int chunks = 0;

                for (ServerLevel level : DiscordBridgeMC.SERVER.getAllLevels()) {
                    chunks += level.getChunkSource().getLoadedChunksCount();
                }

                event.reply("Loaded chunks: " + chunks).queue();
                break;
           case "seed":
                if (DiscordBridgeMC.SERVER == null) {
                    event.reply("Server offline").queue();
                    break;
                }

                long seed = DiscordBridgeMC.SERVER.overworld()
                        .getSeed();

                event.reply("Seed: `" + seed + "`").queue();
                break;
            case "reload":
                ConfigManager.load();

                event.reply("DiscordBridge config reloaded.").queue();
                break;
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String configuredChannel = ConfigManager.get().discord.channelId;

        if (!event.getChannel().getId().equals(configuredChannel)) {
            return;
        }

        String message = event.getMessage().getContentRaw();

        if (message.equals("..updateSlashCommands")) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                return;
            }

            DiscordBridgeMC.LOGGER.info("Updating slash commands. Requested by {}", event.getAuthor().getName());

            SlashCommands.register(event.getJDA());

            event.getChannel().sendMessage("Updated commands.").queue();

            return;
        }

        if (DiscordBridgeMC.SERVER == null) {
            return;
        }

        StringBuilder discordMessage = new StringBuilder(message);

        if (!event.getMessage().getAttachments().isEmpty()) {
            discordMessage.append(" [attachment]");
        }

        DiscordBridgeMC.SERVER.execute(() -> {
            MutableComponent mcMessage = Component.empty()
                    .append(Component.literal("[DISCORD]")
                        .withStyle(Style.EMPTY
                            .withBold(true)
                            .withColor(TextColor.fromRgb(0x5865F2))
                        )
                    )
                    .append(Component.literal(" "))
                    .append(Component.literal(event.getAuthor().getName()))
                    .append(Component.literal(": "))
                    .append(Component.literal(discordMessage.toString()));

            DiscordBridgeMC.SERVER.getPlayerList().broadcastSystemMessage(mcMessage, false);
        });
    }
}
