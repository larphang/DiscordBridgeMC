package id.guglioisstup.discordbridgemc.database.dao;

import id.guglioisstup.discordbridgemc.database.Database;

import java.sql.*;

public final class LinkCodeDao {
    private LinkCodeDao() {
    }

    public static void createCode(String code, String uuid) {
        String sql = """
            INSERT INTO link_codes(code, uuid, expires)
            VALUES (?, ?, ?)
        """;

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, code);
            statement.setString(2, uuid);

            statement.setLong(3, System.currentTimeMillis() + (10 * 60 * 1000));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPlayerUuid(String code) {
        String sql = """
            SELECT uuid
            FROM link_codes
            WHERE code = ?
            AND expires > ?
        """;

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, code);
            statement.setLong(2, System.currentTimeMillis());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getString("uuid");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    public static void deleteCode(String code) {
        String sql = """
            DELETE FROM link_codes
            WHERE code = ?
        """;

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, code);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteExpired() {
        String sql = """
            DELETE FROM link_codes
            WHERE expires < ?
        """;

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, System.currentTimeMillis());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deletePlayerCodes(String uuid) {
        String sql = """
            DELETE FROM link_codes
            WHERE uuid = ?
        """;

        try (
            Connection connection = Database.connection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}