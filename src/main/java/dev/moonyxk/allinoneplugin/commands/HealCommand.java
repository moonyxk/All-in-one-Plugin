package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = CommandTarget.findPlayer(sender, label, args);
        if (target == null) {
            return true;
        }

        target.setHealth(target.getMaxHealth());
        target.setFoodLevel(20);
        target.sendMessage("§aDu wurdest geheilt!");
        if (!sender.equals(target)) {
            sender.sendMessage("§a" + target.getName() + " wurde geheilt!");
        }
        return true;
    }
}