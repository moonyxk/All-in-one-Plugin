package xyz.niliees.allinoneplugin.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BoomCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = player.getLocation();
            World world = player.getWorld();

            // Explosion auslösen
            world.createExplosion(loc.getX(), loc.getY(), loc.getZ(), 3.0f, false, false);
            player.sendMessage("§cBOOM!");
        } else {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
        }
        return true;
    }
}