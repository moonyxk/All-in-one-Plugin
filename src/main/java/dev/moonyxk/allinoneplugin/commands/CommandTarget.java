package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class CommandTarget {

    private CommandTarget() {
    }

    static Player findPlayer(CommandSender sender, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§cVerwendung: /" + label + " <Spieler>");
            return null;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
        }
        return target;
    }
}
