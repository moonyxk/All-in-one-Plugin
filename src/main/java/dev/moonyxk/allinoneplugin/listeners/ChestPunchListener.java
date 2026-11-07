package dev.moonyxk.allinoneplugin.listeners;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ChestPunchListener implements Listener {

    public static final String PERMISSION = "allinoneplugin.chestpunch";

    private static final Set<Material> AXES = EnumSet.of(
            Material.WOODEN_AXE, Material.STONE_AXE, Material.COPPER_AXE, Material.GOLDEN_AXE,
            Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);
    private static final Set<Material> PICKAXES = EnumSet.of(
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.COPPER_PICKAXE, Material.GOLDEN_PICKAXE,
            Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE);
    private static final Set<Material> WOODEN_CONTAINERS = EnumSet.of(
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL);
    private static final Set<Material> STONE_CONTAINERS = EnumSet.of(
            Material.DROPPER, Material.DISPENSER, Material.HOPPER);

    private final FeatureManager features;
    private final NamespacedKey enabledKey;

    public ChestPunchListener(Plugin plugin, FeatureManager features) {
        this.features = features;
        this.enabledKey = new NamespacedKey(plugin, "chestpunch-enabled");
    }

    public boolean isEnabledFor(Player player) {
        return player.getPersistentDataContainer().getOrDefault(enabledKey, PersistentDataType.BOOLEAN, true);
    }

    public boolean toggleFor(Player player) {
        boolean enabled = !isEnabledFor(player);
        player.getPersistentDataContainer().set(enabledKey, PersistentDataType.BOOLEAN, enabled);
        return enabled;
    }

    @EventHandler
    public void onPunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND
                || block == null || player.isSneaking()
                || !features.isEnabled(Feature.CHEST_PUNCH)
                || !player.hasPermission(PERMISSION) || !isEnabledFor(player)) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        Material type = block.getType();
        boolean matchingContainer = (WOODEN_CONTAINERS.contains(type) && !AXES.contains(item.getType()))
                || (STONE_CONTAINERS.contains(type) && !PICKAXES.contains(item.getType()));
        if (item.isEmpty() || !matchingContainer
                || !(block.getState(false) instanceof InventoryHolder holder)) {
            return;
        }

        Inventory destination = holder.getInventory();
        if (destination.firstEmpty() == -1) {
            return;
        }

        destination.addItem(item);
        player.getInventory().setItemInMainHand(null);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
        event.setCancelled(true);
    }
}
