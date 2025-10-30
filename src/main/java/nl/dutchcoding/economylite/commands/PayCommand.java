package nl.dutchcoding.economylite.commands;

import nl.dutchcoding.economylite.EconomyLite;
import nl.dutchcoding.economylite.manager.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final EconomyLite plugin;
    private final EconomyManager economyManager;

    public PayCommand(EconomyLite plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /pay <player> <amount>");
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(plugin.getMessage("player-not-found"));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("cannot-pay-self"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage(plugin.getMessage("invalid-amount"));
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-amount"));
            return true;
        }

        if (!economyManager.hasBalance(player.getUniqueId(), amount)) {
            player.sendMessage(plugin.getMessage("insufficient-funds"));
            return true;
        }

        economyManager.transferBalance(player.getUniqueId(), target.getUniqueId(), amount);

        String paySuccess = plugin.getMessage("pay-success")
                .replace("{amount}", String.format("%.2f", amount))
                .replace("{player}", target.getName())
                .replace("{symbol}", plugin.getCurrencySymbol());

        String payReceived = plugin.getMessage("pay-received")
                .replace("{amount}", String.format("%.2f", amount))
                .replace("{player}", player.getName())
                .replace("{symbol}", plugin.getCurrencySymbol());

        player.sendMessage(paySuccess);
        target.sendMessage(payReceived);

        return true;
    }
}
