package dev.moonyxk.allinoneplugin.features;

import dev.moonyxk.allinoneplugin.listeners.ChestPunchListener;
import dev.moonyxk.allinoneplugin.messages.ConfiguredMessages;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerMenuListener implements Listener {

    private final JavaPlugin plugin;
    private final ChestPunchListener chestPunch;
    private final TpsDisplayManager tpsDisplayManager;

    public PlayerMenuListener(
            JavaPlugin plugin,
            ChestPunchListener chestPunch,
            TpsDisplayManager tpsDisplayManager) {
        this.plugin = plugin;
        this.chestPunch = chestPunch;
        this.tpsDisplayManager = tpsDisplayManager;
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

        if (event.getSlot() == PlayerMenu.TPS_DISPLAY_SLOT) {
            if (event.getClick() == ClickType.LEFT) {
                boolean enabled = tpsDisplayManager.toggleFor(player);
                render(menu, player);
                String key = enabled ? "personal-tps-enabled" : "personal-tps-disabled";
                String fallback = enabled
                        ? "&7Die TPS-Anzeige ist jetzt &aan&7."
                        : "&7Die TPS-Anzeige ist jetzt &caus&7.";
                player.sendMessage(ConfiguredMessages.component(
                        plugin, "messages." + key, fallback, Map.of()));
            } else if (event.getClick() == ClickType.RIGHT) {
                TpsDisplayMode mode = tpsDisplayManager.cycleModeFor(player);
                render(menu, player);
                player.sendMessage(ConfiguredMessages.component(
                        plugin, "messages.personal-tps-mode",
                        "&7Die TPS-Anzeige nutzt jetzt den Modus &b%mode%&7.",
                        Map.of("mode", mode.label())));
            }
        } else if (event.getSlot() == PlayerMenu.CHEST_PUNCH_SLOT) {
            boolean enabled = chestPunch.toggleFor(player);
            render(menu, player);
            String key = enabled ? "personal-chest-punch-enabled" : "personal-chest-punch-disabled";
            String fallback = enabled
                    ? "&7ChestPunch ist für dich jetzt &aan&7."
                    : "&7ChestPunch ist für dich jetzt &caus&7.";
            player.sendMessage(ConfiguredMessages.component(
                    plugin, "messages." + key, fallback, Map.of()));
        } else if (event.getSlot() == PlayerMenu.SOUND_SLOT) {
            boolean enabled = chestPunch.toggleSoundFor(player);
            render(menu, player);
            String key = enabled ? "personal-sound-enabled" : "personal-sound-disabled";
            String fallback = enabled
                    ? "&7Der ChestPunch-Sound ist für dich jetzt &aan&7."
                    : "&7Der ChestPunch-Sound ist für dich jetzt &caus&7.";
            player.sendMessage(ConfiguredMessages.component(
                    plugin, "messages." + key, fallback, Map.of()));
        }
    }

    private void render(PlayerMenu menu, Player player) {
        menu.render(chestPunch.isEnabledFor(player), chestPunch.isSoundEnabledFor(player),
                tpsDisplayManager.isEnabledFor(player), tpsDisplayManager.modeFor(player));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof PlayerMenu) {
            event.setCancelled(true);
        }
    }
}
