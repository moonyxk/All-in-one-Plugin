package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = CommandTarget.findPlayer(sender, label, args);
        if (target == null) {
            return true;
        }

        boolean isFlying = target.getAllowFlight();
        target.setAllowFlight(!isFlying);
        String state = isFlying ? "§cdeaktiviert" : "§aaktiviert";
        target.sendMessage("§aFliegen " + state + "!");
        if (!sender.equals(target)) {
            sender.sendMessage("§aFliegen für " + target.getName() + " " + state + "!");
        }
        return true;
    }
}