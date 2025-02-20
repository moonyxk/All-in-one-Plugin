package xyz.niliees.allinoneplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.niliees.allinoneplugin.AllInOnePlugin;

public class BalanceCommand implements CommandExecutor {

    private final AllInOnePlugin plugin;

    public BalanceCommand(AllInOnePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
            player.sendMessage("§aDein Kontostand: §e" + balance + " Coins");
        } else {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
        }
        return true;
    }
}