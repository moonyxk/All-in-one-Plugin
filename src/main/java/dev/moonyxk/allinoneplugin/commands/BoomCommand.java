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
        if (args.length != 1) {
            return false;
        }

        Player target = CommandTarget.findPlayer(sender, args[0]);
        if (target == null) {
            return true;
        }

        Location loc = target.getLocation();
        if (loc == null) {
            sender.sendMessage("§cDer Standort von " + target.getName() + " konnte nicht ermittelt werden.");
            return true;
        }

        World world = loc.getWorld();
        if (world == null) {
            sender.sendMessage("§cDer Standort von " + target.getName() + " konnte nicht ermittelt werden.");
            return true;
        }

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