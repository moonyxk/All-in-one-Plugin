package dev.moonyxk.allinoneplugin.commands;

import dev.moonyxk.allinoneplugin.features.PlayerMenu;
import dev.moonyxk.allinoneplugin.listeners.ChestPunchListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {

    private final ChestPunchListener chestPunch;

    public MenuCommand(ChestPunchListener chestPunch) {
        this.chestPunch = chestPunch;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Nur Spieler können ihr Menü öffnen.", NamedTextColor.RED));
            return true;
        }
        if (args.length != 0) {
            return false;
        }

        player.openInventory(new PlayerMenu(player.getUniqueId(),
                chestPunch.isEnabledFor(player), chestPunch.isSoundEnabledFor(player)).getInventory());
        return true;
    }
}
