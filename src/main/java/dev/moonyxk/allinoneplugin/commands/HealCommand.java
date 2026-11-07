package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }

        Player target = CommandTarget.findPlayer(sender, args[0]);
        if (target == null) {
            return true;
        }

        AttributeInstance maxHealth = target.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) {
            sender.sendMessage("§cDie maximale Gesundheit von " + target.getName() + " konnte nicht ermittelt werden.");
            return true;
        }

        target.setHealth(maxHealth.getValue());
        target.setFoodLevel(20);
        target.sendMessage("§aDu wurdest geheilt!");
        if (!sender.equals(target)) {
            sender.sendMessage("§a" + target.getName() + " wurde geheilt!");
        }
        return true;
    }
}