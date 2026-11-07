package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.moonyxk.allinoneplugin.AllInOnePlugin;

public class PayCommand implements CommandExecutor {

    private final AllInOnePlugin plugin;

    public PayCommand(AllInOnePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 2) {
                player.sendMessage("§cVerwendung: /pay <Spieler> <Betrag>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cSpieler nicht gefunden.");
                return true;
            }

            try {
                double amount = Double.parseDouble(args[1]);
                if (amount <= 0) {
                    player.sendMessage("§cDer Betrag muss größer als 0 sein.");
                    return true;
                }

                if (plugin.getEconomyManager().withdraw(player.getUniqueId(), amount)) {
                    plugin.getEconomyManager().deposit(target.getUniqueId(), amount);
                    player.sendMessage("§aDu hast §e" + amount + " Coins §aan " + target.getName() + " gesendet.");
                    target.sendMessage("§aDu hast §e" + amount + " Coins §avon §e" + player.getName() + " §aerhalten.");
                } else {
                    player.sendMessage("§cDu hast nicht genug Coins.");
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cBitte gib eine gültige Zahl ein.");
            }
        } else {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
        }
        return true;
    }
}