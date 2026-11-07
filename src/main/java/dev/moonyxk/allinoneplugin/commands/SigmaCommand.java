package dev.moonyxk.allinoneplugin.commands;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import dev.moonyxk.allinoneplugin.features.PlayerMenu;
import dev.moonyxk.allinoneplugin.features.SigmaMenu;
import dev.moonyxk.allinoneplugin.listeners.ChestPunchListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class SigmaCommand implements TabExecutor {

    public static final String PERMISSION = "allinoneplugin.sigma";

    private final FeatureManager features;
    private final ChestPunchListener chestPunch;

    public SigmaCommand(FeatureManager features, ChestPunchListener chestPunch) {
        this.features = features;
        this.chestPunch = chestPunch;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player && !player.hasPermission(PERMISSION)) {
            if (args.length == 0) {
                player.openInventory(new PlayerMenu(player.getUniqueId(),
                        chestPunch.isEnabledFor(player), chestPunch.isSoundEnabledFor(player)).getInventory());
            } else {
                player.sendMessage(Component.text("Dafür benötigst du die Berechtigung "
                        + PERMISSION + ".", NamedTextColor.RED));
            }
            return true;
        }

        if (args.length == 0) {
            if (sender instanceof Player player) {
                player.openInventory(new SigmaMenu(features).getInventory());
            } else {
                for (Feature feature : Feature.values()) {
                    sender.sendMessage(stateMessage(feature, features.isEnabled(feature)));
                }
            }
            return true;
        }
        if (args.length > 2) {
            return false;
        }

        Feature feature = Feature.byId(args[0]);
        if (feature == null) {
            sender.sendMessage(Component.text("Unbekannte Funktion: " + args[0], NamedTextColor.RED));
            return true;
        }

        boolean enabled;
        if (args.length == 1) {
            enabled = features.toggle(feature);
        } else {
            switch (args[1].toLowerCase(Locale.ROOT)) {
                case "on" -> enabled = true;
                case "off" -> enabled = false;
                default -> {
                    return false;
                }
            }
            features.setEnabled(feature, enabled);
        }
        sender.sendMessage(stateMessage(feature, enabled));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player player && !player.hasPermission(PERMISSION)) {
            return List.of();
        }
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            for (Feature feature : Feature.values()) {
                options.add(feature.id());
            }
        } else if (args.length == 2) {
            options.add("on");
            options.add("off");
        }
        String prefix = args.length == 0 ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
        options.removeIf(option -> !option.startsWith(prefix));
        return options;
    }

    public static Component stateMessage(Feature feature, boolean enabled) {
        return Component.text(feature.displayName() + " ist jetzt ", NamedTextColor.GRAY)
                .append(enabled
                        ? Component.text("aktiviert", NamedTextColor.GREEN)
                        : Component.text("deaktiviert", NamedTextColor.RED))
                .append(Component.text(".", NamedTextColor.GRAY));
    }
}
