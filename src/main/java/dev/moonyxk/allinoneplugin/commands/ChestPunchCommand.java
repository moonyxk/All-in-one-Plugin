package dev.moonyxk.allinoneplugin.commands;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import dev.moonyxk.allinoneplugin.listeners.ChestPunchListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChestPunchCommand implements CommandExecutor {

    private final FeatureManager features;
    private final ChestPunchListener listener;

    public ChestPunchCommand(FeatureManager features, ChestPunchListener listener) {
        this.features = features;
        this.listener = listener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Nur Spieler können ChestPunch umschalten.", NamedTextColor.RED));
            return true;
        }
        if (!features.isEnabled(Feature.CHEST_PUNCH)) {
            player.sendMessage(Component.text("ChestPunch ist auf diesem Server deaktiviert.", NamedTextColor.RED));
            return true;
        }

        boolean enabled = listener.toggleFor(player);
        player.sendMessage(Component.text("ChestPunch wurde für dich ", NamedTextColor.GRAY)
                .append(enabled
                        ? Component.text("aktiviert", NamedTextColor.GREEN)
                        : Component.text("deaktiviert", NamedTextColor.RED))
                .append(Component.text(".", NamedTextColor.GRAY)));
        return true;
    }
}
