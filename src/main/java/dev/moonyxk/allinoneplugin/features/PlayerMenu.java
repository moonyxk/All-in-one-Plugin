package dev.moonyxk.allinoneplugin.features;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class PlayerMenu implements InventoryHolder {

    public static final Component TITLE = Component.text("Persönliche Einstellungen", NamedTextColor.DARK_GREEN);
    public static final int CHEST_PUNCH_SLOT = 3;
    public static final int SOUND_SLOT = 5;
    public static final int TPS_DISPLAY_SLOT = 7;

    private final UUID ownerId;
    private final Inventory inventory;

    public PlayerMenu(
            UUID ownerId,
            boolean chestPunchEnabled,
            boolean soundEnabled,
            boolean tpsDisplayEnabled,
            TpsDisplayMode tpsDisplayMode) {
        this.ownerId = ownerId;
        this.inventory = Bukkit.createInventory(this, 9, TITLE);
        render(chestPunchEnabled, soundEnabled, tpsDisplayEnabled, tpsDisplayMode);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void render(
            boolean chestPunchEnabled,
            boolean soundEnabled,
            boolean tpsDisplayEnabled,
            TpsDisplayMode tpsDisplayMode) {
        inventory.setItem(CHEST_PUNCH_SLOT, createIcon(
                chestPunchEnabled ? Material.CHEST : Material.GRAY_DYE,
                "ChestPunch", chestPunchEnabled,
                "ChestPunch nur für dich ein- oder ausschalten."));
        inventory.setItem(SOUND_SLOT, createIcon(
                soundEnabled ? Material.NOTE_BLOCK : Material.GRAY_DYE,
                "ChestPunch-Sound", soundEnabled,
                "Nur den ChestPunch-Sound für dich ein- oder ausschalten."));
        inventory.setItem(TPS_DISPLAY_SLOT, createTpsDisplayIcon(tpsDisplayEnabled, tpsDisplayMode));
    }

    private static ItemStack createTpsDisplayIcon(boolean enabled, TpsDisplayMode mode) {
        NamedTextColor stateColor = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
        ItemStack item = new ItemStack(enabled ? Material.CLOCK : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("TPS-Anzeige", stateColor)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Zeigt TPS und MSPT an; Actionbar und Bossbar zusätzlich Ping.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("Status: ", NamedTextColor.GRAY)
                        .append(Component.text(enabled ? "An" : "Aus", stateColor))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Modus: ", NamedTextColor.GRAY)
                        .append(Component.text(mode.label(), NamedTextColor.AQUA))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Linksklick: Anzeige umschalten", NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Rechtsklick: Modus wechseln", NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false)));
        meta.setEnchantmentGlintOverride(enabled);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createIcon(Material material, String name, boolean enabled, String description) {
        NamedTextColor stateColor = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name, stateColor)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(new ArrayList<>(List.of(
                Component.text(description, NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("Status: ", NamedTextColor.GRAY)
                        .append(Component.text(enabled ? "An" : "Aus", stateColor))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Klicken zum Umschalten", NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false))));
        meta.setEnchantmentGlintOverride(enabled);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
