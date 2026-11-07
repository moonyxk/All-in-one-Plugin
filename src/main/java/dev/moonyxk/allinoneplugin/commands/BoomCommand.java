package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BoomCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = CommandTarget.findPlayer(sender, label, args);
        if (target == null) {
            return true;
        }

        Location loc = target.getLocation();
        World world = target.getWorld();
        if (!world.createExplosion(loc.getX(), loc.getY(), loc.getZ(), 3.0f, false, false)) {
            sender.sendMessage("§cDie Explosion bei " + target.getName() + " konnte nicht ausgelöst werden.");
            return true;
        }
        target.sendMessage("§cBOOM!");
        if (!sender.equals(target)) {
            sender.sendMessage("§aExplosion bei " + target.getName() + " ausgelöst.");
        }
        return true;
    }
}