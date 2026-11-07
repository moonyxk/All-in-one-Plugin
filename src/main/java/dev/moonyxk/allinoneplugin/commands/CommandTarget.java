package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class CommandTarget {

    private CommandTarget() {
    }

    static Player findPlayer(CommandSender sender, String name) {
        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
        }
        return target;
    }
}
