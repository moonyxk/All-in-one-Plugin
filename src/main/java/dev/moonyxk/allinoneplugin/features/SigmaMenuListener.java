package dev.moonyxk.allinoneplugin.features;

import dev.moonyxk.allinoneplugin.commands.SigmaCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class SigmaMenuListener implements Listener {

    private final FeatureManager features;

    public SigmaMenuListener(FeatureManager features) {
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

        boolean enabled = features.toggle(feature);
        menu.render();
        player.sendMessage(SigmaCommand.stateMessage(feature, enabled));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof SigmaMenu) {
            event.setCancelled(true);
        }
    }
}
