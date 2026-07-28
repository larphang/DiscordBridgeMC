package id.guglioisstup.discordbridgemc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import id.guglioisstup.discordbridgemc.DiscordBridgeMC;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("discordbridgemc.json");

    private static Config config;

    private ConfigManager() {
    }

    public static void load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (Files.notExists(CONFIG_PATH)) {
                config = new Config();
                save();

                DiscordBridgeMC.LOGGER.info("Created default config at {}", CONFIG_PATH);
                return;
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                config = GSON.fromJson(reader, Config.class);
            }

            if (config == null) {
                config = new Config();
            }

            DiscordBridgeMC.LOGGER.info("Loaded config.");
        } catch (IOException e) {
            DiscordBridgeMC.LOGGER.error("Failed to load config!", e);
            config = new Config();
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            DiscordBridgeMC.LOGGER.error("Failed to save config!", e);
        }
    }

    public static Config get() {
        return config;
    }
}
