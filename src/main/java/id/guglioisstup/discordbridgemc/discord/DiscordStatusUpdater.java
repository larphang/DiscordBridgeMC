package id.guglioisstup.discordbridgemc.discord;

import id.guglioisstup.discordbridgemc.DiscordBridgeMC;
import id.guglioisstup.discordbridgemc.config.ConfigManager;
import id.guglioisstup.discordbridgemc.monitor.TpsMonitor;
import id.guglioisstup.discordbridgemc.monitor.UptimeMonitor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class DiscordStatusUpdater {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private DiscordStatusUpdater() {}

    public static void start() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!DiscordBot.isRunning()) {
                return;
            }

            update();
        }, 0, 5, TimeUnit.MINUTES);
    }

    private static void update() {
        var jda = DiscordBot.getJDA();

        String channelId = ConfigManager.get().discord.channelId;

        TextChannel channel = jda.getTextChannelById(channelId);

        if (channel == null) {
            return;
        }

        int players = 0;

        if (DiscordBridgeMC.SERVER != null) {
            players = DiscordBridgeMC.SERVER.getPlayerList().getPlayerCount();
        }

        String status = "Server is online!\n" +
                "Player Count: " + players + "\n" +
                "TPS: " + TpsMonitor.getTPS() + "\n" +
                "Uptime: " + UptimeMonitor.getFormatted();

        channel.getManager().setTopic(status).queue();
    }
}
