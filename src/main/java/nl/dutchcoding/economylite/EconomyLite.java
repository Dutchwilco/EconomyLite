package nl.dutchcoding.economylite;

import nl.dutchcoding.economylite.commands.BalanceCommand;
import nl.dutchcoding.economylite.commands.PayCommand;
import nl.dutchcoding.economylite.database.DatabaseManager;
import nl.dutchcoding.economylite.manager.EconomyManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyLite extends JavaPlugin {

    private DatabaseManager databaseManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        economyManager = new EconomyManager(this, databaseManager);

        getCommand("balance").setExecutor(new BalanceCommand(this, economyManager));
        getCommand("pay").setExecutor(new PayCommand(this, economyManager));

        getLogger().info("EconomyLite has been enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("EconomyLite has been disabled!");
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public String getCurrencySymbol() {
        return getConfig().getString("currency-symbol", "$");
    }

    public String getMessage(String key) {
        return getConfig().getString("messages." + key, "&cMessage not found: " + key)
                .replace("&", "§");
    }
}
