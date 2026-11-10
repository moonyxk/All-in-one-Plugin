package dev.moonyxk.allinoneplugin.commands;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import dev.moonyxk.allinoneplugin.features.PlayerMenu;
import dev.moonyxk.allinoneplugin.features.SigmaMenu;
import dev.moonyxk.allinoneplugin.features.TpsDisplayManager;
import dev.moonyxk.allinoneplugin.listeners.ChestPunchListener;
import dev.moonyxk.allinoneplugin.messages.ConfiguredMessages;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SigmaCommand implements TabExecutor {

    public static final String PERMISSION = "allinoneplugin.sigma";

    private final JavaPlugin plugin;
    private final FeatureManager features;
    private final ChestPunchListener chestPunch;
    private final TpsDisplayManager tpsDisplayManager;

    public SigmaCommand(
            JavaPlugin plugin,
            FeatureManager features,
            ChestPunchListener chestPunch,
            TpsDisplayManager tpsDisplayManager) {
        this.plugin = plugin;
        this.features = features;
        this.chestPunch = chestPunch;
        this.tpsDisplayManager = tpsDisplayManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (handleUnauthorizedPlayer(sender, args)) {
            return true;
        }

        if (args.length == 0) {
            showOverview(sender);
            return true;
        }
        if (args.length > 2) {
            return false;
        }
        return toggleFeature(sender, args);
    }

    private boolean handleUnauthorizedPlayer(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player) || player.hasPermission(PERMISSION)) {
            return false;
        }
        if (args.length == 0) {
            player.openInventory(new PlayerMenu(player.getUniqueId(),
                    chestPunch.isEnabledFor(player), chestPunch.isSoundEnabledFor(player),
                    tpsDisplayManager.isEnabledFor(player), tpsDisplayManager.modeFor(player)).getInventory());
        } else {
            player.sendMessage(Component.text("Dafür benötigst du die Berechtigung "
                    + PERMISSION + ".", NamedTextColor.RED));
        }
        return true;
    }

    private void showOverview(CommandSender sender) {
        if (sender instanceof Player player) {
            player.openInventory(new SigmaMenu(features).getInventory());
            return;
        }
        for (Feature feature : Feature.values()) {
            if (feature.commandToggleable()) {
                sender.sendMessage(stateMessage(plugin, feature, features.isEnabled(feature)));
            }
        }
    }

    private boolean toggleFeature(CommandSender sender, String[] args) {
        Feature feature = Feature.byId(args[0]);
        if (feature == null || !feature.commandToggleable()) {
            sender.sendMessage(Component.text("Unbekannte Funktion: " + args[0], NamedTextColor.RED));
            return true;
        }

        if (args.length == 1) {
            boolean enabled = features.toggle(feature);
            sender.sendMessage(stateMessage(plugin, feature, enabled));
            return true;
        }

        Boolean enabled = parseEnabled(args[1]);
        if (enabled == null) {
            return false;
        }
        features.setEnabled(feature, enabled);
        sender.sendMessage(stateMessage(plugin, feature, enabled));
        return true;
    }

    private static Boolean parseEnabled(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "on" -> true;
            case "off" -> false;
            default -> null;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player player && !player.hasPermission(PERMISSION)) {
            return List.of();
        }
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            for (Feature feature : Feature.values()) {
                if (feature.commandToggleable()) {
                    options.add(feature.id());
                }
            }
        } else if (args.length == 2) {
            options.add("on");
            options.add("off");
        }
        String prefix = args.length == 0 ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
        options.removeIf(option -> !option.startsWith(prefix));
        return options;
    }

    public static Component stateMessage(JavaPlugin plugin, Feature feature, boolean enabled) {
        String key = enabled ? "feature-enabled" : "feature-disabled";
        String fallback = enabled
                ? "&7%feature% ist jetzt &aaktiviert&7."
                : "&7%feature% ist jetzt &cdeaktiviert&7.";
        return ConfiguredMessages.component(plugin, "messages." + key, fallback,
                Map.of("feature", feature.displayName()));
    }
}
