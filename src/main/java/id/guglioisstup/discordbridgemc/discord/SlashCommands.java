package id.guglioisstup.discordbridgemc.discord;

import id.guglioisstup.discordbridgemc.DiscordBridgeMC;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public final class SlashCommands {
    private SlashCommands() {
    }

    public static void register(JDA jda) {
        DiscordBridgeMC.LOGGER.info("Registering Discord slash commands...");

        jda.updateCommands()
            .addCommands(
                Commands.slash("memory", "Shows JVM memory usage"),
                Commands.slash("tps", "Shows current server TPS"),
                Commands.slash("players", "Shows online players"),
                Commands.slash("uptime", "Shows server uptime"),
                Commands.slash("mspt", "Shows current server MSPT"),
                Commands.slash("gc", "Requests a garbage collection"),
                Commands.slash("chunks", "Shows loaded chunk count"),
                Commands.slash("seed", "Shows world seed"),
                Commands.slash("reload", "Reloads the mod configuration"),
                Commands.slash("playtime", "Shows player playtime")
                    .addOption(
                        OptionType.STRING,
                        "player", "Player name",
                        false
                    ),
                Commands.slash("lastseen", "Shows when a player was last online")
                .addOption(
                    OptionType.STRING,
                    "player",
                    "Player name",
                    true
                ),
                Commands.slash("top", "Shows server statistics rankings")
                    .addOption(
                        OptionType.STRING,
                        "category",
                        "Statistic category",
                        true
                    ),
                Commands.slash("unlink", "Unlinks your Discord account")
            )
            .queue(
                success -> DiscordBridgeMC.LOGGER.info("Slash commands registered."),
                error -> DiscordBridgeMC.LOGGER.error("Failed to register slash commands.", error)
            );
    }
}
