package id.guglioisstup.discordbridgemc.discord;

import id.guglioisstup.discordbridgemc.DiscordBridgeMC;
import id.guglioisstup.discordbridgemc.monitor.SystemMonitor;
import id.guglioisstup.discordbridgemc.monitor.TpsMonitor;
import id.guglioisstup.discordbridgemc.monitor.UptimeMonitor;
import id.guglioisstup.discordbridgemc.config.ConfigManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import id.guglioisstup.discordbridgemc.database.dao.PlayerDao;
import id.guglioisstup.discordbridgemc.database.model.PlayerData;
import id.guglioisstup.discordbridgemc.database.model.TopCategory;
import id.guglioisstup.discordbridgemc.monitor.TimeFormatter;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.dv8tion.jda.api.Permission;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import java.util.List;

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
           case "playtime":
                PlayerData player;

                if (event.getOption("player") != null) {
                    String username = event.getOption("player").getAsString();
                    player = PlayerDao.getByUsername(username);
                } else {
                    player = PlayerDao.getByDiscord(
                        event.getUser().getId()
                    );
                }

                if (player == null) {
                    event.reply(
                        "Player not found. Make sure your Discord account is linked."
                    ).queue();
                    break;
                }

                event.reply(
                    player.getUsername()
                    + "'s playtime: "
                    + TimeFormatter.format(player.getPlaytime())
                ).queue();

                break;
            case "lastseen":
                String target = event.getOption("player").getAsString();

                if (DiscordBridgeMC.SERVER != null) {
                    var onlinePlayer = DiscordBridgeMC.SERVER.getPlayerList().getPlayerByName(target);

                    if (onlinePlayer != null) {
                        event.reply(onlinePlayer.getName().getString() + " is currently online.").queue();

                        break;
                    }
                }

                PlayerData seen = PlayerDao.getByUsername(target);

                if (seen == null) {
                    event.reply("Player not found.").queue();
                    break;
                }

                long ago = (System.currentTimeMillis() - seen.getLastSeen()) / 1000;

                event.reply(
                    seen.getUsername()
                    + " was last seen "
                    + TimeFormatter.format(ago)
                    + " ago."
                ).queue();

                break;
            case "top":
                String categoryName =event.getOption("category").getAsString().toUpperCase();

                TopCategory category;
                try {
                    category = TopCategory.valueOf(categoryName);
                } catch(Exception e) {
                    event.reply(
                        "Invalid category.\n" +
                        "Available:\n" +
                        "`playtime`\n" +
                        "`player_kills`\n" +
                        "`mob_kills`\n" +
                        "`deaths`\n" +
                        "`blocks_broken`\n" +
                        "`blocks_placed`\n" 
                    ).queue();

                    break;
                }

                List<PlayerData> players2 = PlayerDao.getTop(category, 10);

                StringBuilder message = new StringBuilder();

                message.append("Top ")
                    .append(category.display)
                    .append("\n\n");

                int rank = 1;

                for(PlayerData p : players2) {
                    long value = switch(category) {
                        case PLAYTIME -> p.getPlaytime();
                        case PLAYER_KILLS -> p.getPlayerKills();
                        case MOB_KILLS -> p.getMobKills();
                        case DEATHS -> p.getDeaths();
                        case BLOCKS_BROKEN -> p.getBlocksBroken();
                        case BLOCKS_PLACED -> p.getBlocksPlaced();
                    };

                    message.append(rank++)
                        .append(". ")
                        .append(p.getUsername())
                        .append(" - ")
                        .append(value)
                        .append("\n");
                }

                event.reply(message.toString()).queue();

                break;
            case "unlink":
                String discordId = event.getUser().getId();
                PlayerData linkedPlayer = PlayerDao.getByDiscord(discordId);

                if (linkedPlayer == null) {
                    event.reply("Your Discord account is not linked to a Minecraft account.").queue();

                    break;
                }

                PlayerDao.unlinkDiscord(
                    linkedPlayer.getUuid()
                );

                event.reply("Successfully unlinked Minecraft account: "+ linkedPlayer.getUsername()).queue();

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

    private void sendDiscordMessageToMinecraft(
        MessageReceivedEvent event,
        String message
    ) {
        if (DiscordBridgeMC.SERVER == null) {
            return;
        }

        DiscordBridgeMC.SERVER.execute(() -> {

            MutableComponent mcMessage = Component.empty()
                .append(
                    Component.literal("[DISCORD]")
                        .withStyle(
                            Style.EMPTY
                                .withBold(true)
                                .withColor(
                                    TextColor.fromRgb(0x5865F2)
                                )
                        )
                )
                .append(Component.literal(" "))
                .append(
                    Component.literal(
                        event.getAuthor().getName()
                    )
                )
                .append(Component.literal(": "))
                .append(
                    Component.literal(message)
                );

            DiscordBridgeMC.SERVER
                .getPlayerList()
                .broadcastSystemMessage(
                    mcMessage,
                    false
                );
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (event.isFromType(net.dv8tion.jda.api.entities.channel.ChannelType.PRIVATE)) {

            String message = event.getMessage().getContentRaw().trim();

            if (message.toUpperCase().startsWith("..link ")) {
                String code = message.substring(7).trim().toUpperCase();

                boolean success = id.guglioisstup.discordbridgemc.link.LinkService.redeemCode(
                    code,
                    event.getAuthor().getId()
                );

                if (success) {
                    event.getChannel().sendMessage("Your Minecraft account has been linked").queue();
                } else {
                    event.getChannel().sendMessage("Invalid or expired link code.").queue();
                }

                return;
            }

            boolean success = id.guglioisstup.discordbridgemc.link.LinkService.redeemCode(
                message.toUpperCase(),
                event.getAuthor().getId()
            );

            if (success) {
                event.getChannel().sendMessage("Your Minecraft account has been linked").queue();
            }

            return;
        }

        String configuredChannel = ConfigManager.get().discord.channelId;

        if (!event.getChannel().getId().equals(configuredChannel)) {
            return;
        }

        String message = event.getMessage().getContentRaw();

        if (message.equals("..updateSlashCommands")) {
            if (event.getMember() == null || !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
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

        if (event.getMessage().getMessageReference() != null) {
            event.getMessage()
                .getMessageReference()
                .resolve()
                .queue(reply -> {
                    String replyContent = reply.getContentRaw();

                    if (replyContent.length() > 100) {
                        replyContent = replyContent.substring(0, 100) + "...";
                    }

                    discordMessage.insert(
                        0,
                        "↪ Replying to: " + replyContent + "\n"
                    );

                    sendDiscordMessageToMinecraft(
                        event,
                        discordMessage.toString()
                    );
                });

        } else {
            sendDiscordMessageToMinecraft(
                event,
                discordMessage.toString()
            );
        }
    }
}
