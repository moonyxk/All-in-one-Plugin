package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.moonyxk.allinoneplugin.listeners.BoomDamageListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BoomCommand implements CommandExecutor {

    private final BoomDamageListener damageListener;

    public BoomCommand(BoomDamageListener damageListener) {
        this.damageListener = damageListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args.length > 2) {
            return false;
        }

        boolean damage = true;
        if (args.length == 2) {
            if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                sender.sendMessage(Component.text("Bitte gib für damage true oder false an.", NamedTextColor.RED));
                return true;
            }
            damage = Boolean.parseBoolean(args[1]);
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

        if (!damageListener.createExplosion(world, loc, 3.0f, damage)) {
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