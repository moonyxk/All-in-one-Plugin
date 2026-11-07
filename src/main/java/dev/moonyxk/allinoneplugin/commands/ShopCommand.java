package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.moonyxk.allinoneplugin.AllInOnePlugin;

public class ShopCommand implements CommandExecutor {

    private final AllInOnePlugin plugin;

    public ShopCommand(AllInOnePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 2) {
                player.sendMessage("§cVerwendung: /shop <item> <price>");
                return true;
            }

            Material material = Material.matchMaterial(args[0].toUpperCase());
            if (material == null) {
                player.sendMessage("§cUngültiges Item!");
                return true;
            }

            try {
                double price = Double.parseDouble(args[1]);
                if (plugin.getEconomyManager().withdraw(player.getUniqueId(), price)) {
                    player.getInventory().addItem(new ItemStack(material, 1));
                    player.sendMessage("§aDu hast 1x §e" + material.name() + " §afür §e" + price + " Coins §agekauft.");
                } else {
                    player.sendMessage("§cDu hast nicht genug Coins.");
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cBitte gib einen gültigen Preis ein.");
            }
        } else {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
        }
        return true;
    }
}