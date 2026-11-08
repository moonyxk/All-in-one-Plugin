package dev.moonyxk.allinoneplugin.commands;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import dev.moonyxk.allinoneplugin.listeners.ChestPunchListener;
import dev.moonyxk.allinoneplugin.messages.ConfiguredMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestPunchCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FeatureManager features;
    private final ChestPunchListener listener;

    public ChestPunchCommand(JavaPlugin plugin, FeatureManager features, ChestPunchListener listener) {
        this.plugin = plugin;
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
            player.sendMessage(ConfiguredMessages.component(plugin,
                    "messages.chest-punch-server-disabled",
                    "&cChestPunch ist auf diesem Server deaktiviert.", Map.of()));
            return true;
        }

        boolean enabled = listener.toggleFor(player);
        String key = enabled ? "chest-punch-command-enabled" : "chest-punch-command-disabled";
        String fallback = enabled
                ? "&7ChestPunch wurde für dich &aaktiviert&7."
                : "&7ChestPunch wurde für dich &cdeaktiviert&7.";
        player.sendMessage(ConfiguredMessages.component(plugin, "messages." + key, fallback, Map.of()));
        return true;
    }
}
