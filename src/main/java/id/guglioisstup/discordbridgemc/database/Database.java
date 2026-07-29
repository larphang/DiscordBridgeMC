package id.guglioisstup.discordbridgemc.database;

import java.sql.Connection;
import java.sql.SQLException;

public final class Database {
    private Database() {}

    public static Connection connection() throws SQLException {
        return DatabaseManager.getConnection();
    }
}
