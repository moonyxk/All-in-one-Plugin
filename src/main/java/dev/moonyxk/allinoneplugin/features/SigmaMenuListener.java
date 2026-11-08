package dev.moonyxk.allinoneplugin.features;

import dev.moonyxk.allinoneplugin.commands.SigmaCommand;
import dev.moonyxk.allinoneplugin.messages.ConfiguredMessages;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SigmaMenuListener implements Listener {

    private final JavaPlugin plugin;
    private final FeatureManager features;

    public SigmaMenuListener(JavaPlugin plugin, FeatureManager features) {
        this.plugin = plugin;
        this.features = features;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder(false) instanceof SigmaMenu menu)) {
            return;
        }
        event.setCancelled(true);
        if (event.getClickedInventory() != menu.getInventory()
                || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Feature feature = menu.featureAt(event.getSlot());
        if (feature == null) {
            return;
        }
        if (!player.hasPermission(SigmaCommand.PERMISSION)) {
            player.closeInventory();
            return;
        }

        if (feature == Feature.NO_CREEPER_GRIEF && event.isRightClick()) {
            boolean damageEnabled = features.toggleCreeperDamage();
            menu.render();
            String key = damageEnabled ? "creeper-damage-enabled" : "creeper-damage-disabled";
            String fallback = damageEnabled
                    ? "&7Creeper-Schaden ist jetzt &aaktiviert&7."
                    : "&7Creeper-Schaden ist jetzt &cdeaktiviert&7.";
            player.sendMessage(ConfiguredMessages.component(
                    plugin, "messages." + key, fallback, Map.of()));
            return;
        }

        boolean enabled = features.toggle(feature);
        menu.render();
        player.sendMessage(SigmaCommand.stateMessage(plugin, feature, enabled));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof SigmaMenu) {
            event.setCancelled(true);
        }
    }
}
