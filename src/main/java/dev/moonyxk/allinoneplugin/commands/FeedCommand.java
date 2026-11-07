package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = CommandTarget.findPlayer(sender, label, args);
        if (target == null) {
            return true;
        }

        target.setFoodLevel(20);
        target.sendMessage("§aDein Hunger wurde aufgefüllt!");
        if (!sender.equals(target)) {
            sender.sendMessage("§aDer Hunger von " + target.getName() + " wurde aufgefüllt!");
        }
        return true;
    }
}