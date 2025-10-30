package nl.dutchcoding.economylite.manager;

import nl.dutchcoding.economylite.EconomyLite;
import nl.dutchcoding.economylite.database.DatabaseManager;

import java.util.UUID;

public class EconomyManager {

    private final EconomyLite plugin;
    private final DatabaseManager databaseManager;

    public EconomyManager(EconomyLite plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    public double getBalance(UUID uuid) {
        return databaseManager.getBalance(uuid);
    }

    public void setBalance(UUID uuid, double balance) {
        databaseManager.setBalance(uuid, balance);
    }

    public boolean hasBalance(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void removeBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) - amount);
    }

    public boolean transferBalance(UUID from, UUID to, double amount) {
        if (!hasBalance(from, amount)) {
            return false;
        }

        removeBalance(from, amount);
        addBalance(to, amount);
        return true;
    }
}
