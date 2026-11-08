package dev.moonyxk.allinoneplugin.commands;

import dev.moonyxk.allinoneplugin.listeners.BoomDamageListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JumpscareCommand implements CommandExecutor {

    private final BoomDamageListener damageListener;

    public JumpscareCommand(BoomDamageListener damageListener) {
        this.damageListener = damageListener;
    }

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
        if (loc == null || loc.getWorld() == null) {
            sender.sendMessage("§cDer Standort von " + target.getName() + " konnte nicht ermittelt werden.");
            return true;
        }

        World world = loc.getWorld();
        if (!damageListener.createExplosion(world, loc, 3.0f, false)) {
            sender.sendMessage("§cDer Jumpscare bei " + target.getName() + " konnte nicht ausgelöst werden.");
            return true;
        }
        target.sendMessage("§cBOOM!");
        if (!sender.equals(target)) {
            sender.sendMessage("§aJumpscare bei " + target.getName() + " ausgelöst.");
        }
        return true;
    }
}
