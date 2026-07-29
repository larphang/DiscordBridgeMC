package id.guglioisstup.discordbridgemc.database;

import id.guglioisstup.discordbridgemc.DiscordBridgeMC;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    private static final String URL ="jdbc:sqlite:config/discordbridgemc.db";

    private DatabaseManager() { }

    public static void initialize() {
        DiscordBridgeMC.LOGGER.info("Trying to connect to SQLite database");

        try {
            File configDir = new File("config");
            configDir.mkdirs();

            try (Connection connection = getConnection()) {
                createTables(connection);
            }

            DiscordBridgeMC.LOGGER.info("Connected to SQLite database.");
        } catch (SQLException e) {
            DiscordBridgeMC.LOGGER.error("Failed to initialize database.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS players (
                uuid TEXT PRIMARY KEY,
                username TEXT NOT NULL,
                discord_id TEXT,

                playtime INTEGER DEFAULT 0,

                player_kills INTEGER DEFAULT 0,
                mob_kills INTEGER DEFAULT 0,
                deaths INTEGER DEFAULT 0,

                blocks_placed INTEGER DEFAULT 0,
                blocks_broken INTEGER DEFAULT 0,

                session_start INTEGER,

                first_join INTEGER,
                last_seen INTEGER
            );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS link_codes (
                    code TEXT PRIMARY KEY,
                    uuid TEXT NOT NULL,
                    expires INTEGER NOT NULL
                );
            """);
        }
    }

    public static void close() { }
}