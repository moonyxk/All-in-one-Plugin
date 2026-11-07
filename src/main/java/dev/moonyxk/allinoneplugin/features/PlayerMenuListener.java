package dev.moonyxk.allinoneplugin.features;

import dev.moonyxk.allinoneplugin.listeners.ChestPunchListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class PlayerMenuListener implements Listener {

    private final ChestPunchListener chestPunch;

    public PlayerMenuListener(ChestPunchListener chestPunch) {
        this.chestPunch = chestPunch;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder(false) instanceof PlayerMenu menu)) {
            return;
        }
        event.setCancelled(true);
        if (event.getClickedInventory() != menu.getInventory()
                || !(event.getWhoClicked() instanceof Player player)
                || !menu.getOwnerId().equals(player.getUniqueId())) {
            return;
        }

        if (event.getSlot() == PlayerMenu.CHEST_PUNCH_SLOT) {
            boolean enabled = chestPunch.toggleFor(player);
            menu.render(enabled, chestPunch.isSoundEnabledFor(player));
            player.sendMessage(Component.text("ChestPunch ist für dich jetzt ", NamedTextColor.GRAY)
                    .append(Component.text(enabled ? "an" : "aus",
                            enabled ? NamedTextColor.GREEN : NamedTextColor.RED))
                    .append(Component.text(".", NamedTextColor.GRAY)));
        } else if (event.getSlot() == PlayerMenu.SOUND_SLOT) {
            boolean enabled = chestPunch.toggleSoundFor(player);
            menu.render(chestPunch.isEnabledFor(player), enabled);
            player.sendMessage(Component.text("Der ChestPunch-Sound ist für dich jetzt ", NamedTextColor.GRAY)
                    .append(Component.text(enabled ? "an" : "aus",
                            enabled ? NamedTextColor.GREEN : NamedTextColor.RED))
                    .append(Component.text(".", NamedTextColor.GRAY)));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof PlayerMenu) {
            event.setCancelled(true);
        }
    }
}
