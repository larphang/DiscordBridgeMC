package id.guglioisstup.discordbridgemc;

import id.guglioisstup.discordbridgemc.commands.LinkCommand;
import id.guglioisstup.discordbridgemc.commands.UnlinkCommand;
import id.guglioisstup.discordbridgemc.config.ConfigManager;
import id.guglioisstup.discordbridgemc.database.DatabaseManager;
import id.guglioisstup.discordbridgemc.discord.DiscordBot;
import id.guglioisstup.discordbridgemc.discord.DiscordStatusUpdater;
import id.guglioisstup.discordbridgemc.events.BroadcastEvents;
import id.guglioisstup.discordbridgemc.events.ChatEvents;
import id.guglioisstup.discordbridgemc.events.PlayerEvents;
import id.guglioisstup.discordbridgemc.events.StatsEvents;
import id.guglioisstup.discordbridgemc.monitor.TpsMonitor;
import id.guglioisstup.discordbridgemc.monitor.UptimeMonitor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DiscordBridgeMC implements ModInitializer {
    public static final String MOD_ID = "discordbridgemc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing DiscordBridgeMC...");

        ConfigManager.load();

        DatabaseManager.initialize();

        ChatEvents.register();
        BroadcastEvents.register();
        PlayerEvents.register();
        StatsEvents.register();

        TpsMonitor.register();

        CommandRegistrationCallback.EVENT.register(
			(dispatcher, registryAccess, environment) -> {
				LinkCommand.register(dispatcher);
				UnlinkCommand.register(dispatcher);
			}
		);
		
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;

            LOGGER.info("Minecraft server started.");

            UptimeMonitor.start();
            DiscordBot.start();

            DiscordStatusUpdater.start();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("Stopping DiscordBridgeMC...");

            DiscordBot.shutdown();
            DatabaseManager.close();

            SERVER = null;
        });

        LOGGER.info("DiscordBridgeMC initialized.");
    }


    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}