package nl.dutchcoding.economylite.database;

import nl.dutchcoding.economylite.EconomyLite;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class DatabaseManager {

    private final EconomyLite plugin;
    private Connection connection;

    public DatabaseManager(EconomyLite plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File databaseFile = new File(dataFolder, "economy.db");
            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

            connection = DriverManager.getConnection(url);

            String createTableSQL = "CREATE TABLE IF NOT EXISTS player_balances (" +
                    "uuid TEXT PRIMARY KEY," +
                    "balance REAL NOT NULL)";

            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }

            plugin.getLogger().info("Database initialized successfully!");

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public double getBalance(UUID uuid) {
        String query = "SELECT balance FROM player_balances WHERE uuid = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            } else {
                double startingBalance = plugin.getConfig().getDouble("starting-balance", 1000.0);
                setBalance(uuid, startingBalance);
                return startingBalance;
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get balance for " + uuid + ": " + e.getMessage());
            return 0.0;
        }
    }

    public void setBalance(UUID uuid, double balance) {
        String query = "INSERT OR REPLACE INTO player_balances (uuid, balance) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            statement.setDouble(2, balance);
            statement.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set balance for " + uuid + ": " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection closed!");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close database connection: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
