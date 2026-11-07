package dev.moonyxk.allinoneplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cVerwendung: /kick <spieler> <grund>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            String reason = String.join(" ", args).substring(args[0].length()).trim();
            target.kickPlayer("§cDu wurdest vom Server gekickt: " + reason);
            sender.sendMessage("§aSpieler " + target.getName() + " wurde gekickt.");
        } else {
            sender.sendMessage("§cSpieler nicht gefunden.");
        }
        return true;
    }
}