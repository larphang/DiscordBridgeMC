package id.guglioisstup.discordbridgemc.database.dao;

import id.guglioisstup.discordbridgemc.database.Database;
import id.guglioisstup.discordbridgemc.database.model.PlayerData;
import id.guglioisstup.discordbridgemc.database.model.TopCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PlayerDao {
    private PlayerDao() {
    }

    public static boolean exists(UUID uuid) {
        String sql = "SELECT 1 FROM players WHERE uuid = ?";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void create(UUID uuid, String username) {
        String sql = """
            INSERT OR IGNORE INTO players
            (
                uuid,
                username,
                first_join,
                last_seen,
                playtime,
                player_kills,
                mob_kills,
                deaths,
                blocks_placed,
                blocks_broken,
                session_start
            )
            VALUES (?, ?, ?, ?, 0, 0, 0, 0, 0, 0, 0)
        """;

        long now = System.currentTimeMillis();

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, uuid.toString());
            statement.setString(2, username);
            statement.setLong(3, now);
            statement.setLong(4, now);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerData get(UUID uuid) {
        String sql = "SELECT * FROM players WHERE uuid = ?";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return readPlayer(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PlayerData> getAll() {
        List<PlayerData> players = new ArrayList<>();

        String sql = "SELECT * FROM players";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()
        ) {
            while (rs.next()) {
                players.add(readPlayer(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }

    public static void addStat(UUID uuid, String stat, long amount) {
        String sql = """
            UPDATE players
            SET %s = %s + ?
            WHERE uuid = ?
        """.formatted(stat, stat);

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, amount);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PlayerData> getTop(TopCategory category, int limit) {
        List<PlayerData> players = new ArrayList<>();

        String column = switch (category) {
            case PLAYTIME -> "playtime";
            case PLAYER_KILLS -> "player_kills";
            case MOB_KILLS -> "mob_kills";
            case DEATHS -> "deaths";
            case BLOCKS_BROKEN -> "blocks_broken";
            case BLOCKS_PLACED -> "blocks_placed";
        };

        String sql = """
            SELECT *
            FROM players
            ORDER BY %s DESC
            LIMIT ?
        """.formatted(column);
        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    players.add(readPlayer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }

    public static PlayerData getByDiscord(String discordId) {
        String sql = "SELECT * FROM players WHERE discord_id = ?";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, discordId);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return readPlayer(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerData getByUsername(String username) {
        String sql = """
            SELECT *
            FROM players
            WHERE username = ?
        """;

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return readPlayer(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDiscordId(UUID uuid) {
        String sql = "SELECT discord_id FROM players WHERE uuid = ?";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("discord_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static List<PlayerData> getTopPlaytime(int limit) {
        List<PlayerData> players = new ArrayList<>();

        String sql = """
            SELECT *
            FROM players
            ORDER BY playtime DESC
            LIMIT ?
        """;

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    players.add(readPlayer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }

    public static void linkDiscord(UUID uuid, String discordId) {
        updateString(uuid, "discord_id", discordId);
    }

    public static void unlinkDiscord(UUID uuid) {
        updateString(uuid, "discord_id", null);
    }

    public static void updateUsername(UUID uuid, String username) {
        updateString(uuid, "username", username);
    }

    public static void updateLastSeen(UUID uuid) {
        updateLong(uuid, "last_seen", System.currentTimeMillis());
    }

    public static void addPlaytime(UUID uuid, long seconds) {
        String sql = """
            UPDATE players
            SET playtime = playtime + ?
            WHERE uuid = ?
        """;

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, seconds);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateSessionStart(UUID uuid, long time) {
        updateLong(uuid, "session_start", time);
    }

    public static long getSessionStart(UUID uuid) {
        String sql = "SELECT session_start FROM players WHERE uuid = ?";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("session_start");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public static void delete(UUID uuid) {
        String sql = "DELETE FROM players WHERE uuid = ?";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getPlaytime(UUID uuid) {
        try (Connection conn = Database.connection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT playtime FROM players WHERE uuid = ?"
            )) {

            stmt.setString(1, uuid.toString());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("playtime");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static PlayerData readPlayer(ResultSet rs) throws SQLException {
        UUID uuid = UUID.fromString(
            rs.getString("uuid")
        );

        PlayerData player = new PlayerData(uuid);

        player.setUsername(rs.getString("username"));
        player.setDiscordId(rs.getString("discord_id"));
        player.setPlaytime(rs.getLong("playtime"));
        player.setPlayerKills(rs.getLong("player_kills"));
        player.setMobKills(rs.getLong("mob_kills"));
        player.setDeaths(rs.getLong("deaths"));
        player.setBlocksPlaced(rs.getLong("blocks_placed"));
        player.setBlocksBroken(rs.getLong("blocks_broken"));
        player.setFirstJoin(rs.getLong("first_join"));
        player.setLastSeen(rs.getLong("last_seen"));

        return player;
    }

    private static void updateString(UUID uuid, String column, String value) {
        String sql = "UPDATE players SET " + column + " = ? WHERE uuid = ?";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, value);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateLong(UUID uuid, String column, long value) {
        String sql = "UPDATE players SET " + column + " = ? WHERE uuid = ?";

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, value);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
