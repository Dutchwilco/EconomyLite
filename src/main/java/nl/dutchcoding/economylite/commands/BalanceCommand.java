package nl.dutchcoding.economylite.commands;

import nl.dutchcoding.economylite.EconomyLite;
import nl.dutchcoding.economylite.manager.EconomyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final EconomyLite plugin;
    private final EconomyManager economyManager;

    public BalanceCommand(EconomyLite plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        double balance = economyManager.getBalance(player.getUniqueId());

        String message = plugin.getMessage("balance")
                .replace("{balance}", String.format("%.2f", balance))
                .replace("{symbol}", plugin.getCurrencySymbol());

        player.sendMessage(message);
        return true;
    }
}
