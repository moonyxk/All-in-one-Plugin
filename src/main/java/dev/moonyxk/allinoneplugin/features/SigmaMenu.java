package dev.moonyxk.allinoneplugin.features;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class SigmaMenu implements InventoryHolder {

    public static final Component TITLE = Component.text("Sigma-Menü", NamedTextColor.DARK_PURPLE);

    private final FeatureManager features;
    private final Inventory inventory;

    public SigmaMenu(FeatureManager features) {
        this.features = features;
        int rows = Math.max(1, (Feature.values().length + 8) / 9);
        this.inventory = Bukkit.createInventory(this, rows * 9, TITLE);
        render();
    }

    public Feature featureAt(int slot) {
        Feature[] all = Feature.values();
        return slot >= 0 && slot < all.length ? all[slot] : null;
    }

    public void render() {
        for (Feature feature : Feature.values()) {
            inventory.setItem(feature.ordinal(), createIcon(feature, features));
        }
    }

    private static ItemStack createIcon(Feature feature, FeatureManager features) {
        boolean enabled = features.isEnabled(feature);
        NamedTextColor stateColor = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
        ItemStack item = new ItemStack(feature.icon());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(plain(feature.displayName(), stateColor).decorate(TextDecoration.BOLD));
        List<Component> lore = new ArrayList<>(List.of(
                plain(feature.description(), NamedTextColor.GRAY),
                Component.empty(),
                plain("Status: ", NamedTextColor.GRAY)
                        .append(plain(enabled ? "Aktiviert" : "Deaktiviert", stateColor)),
                plain("Linksklick zum Umschalten", NamedTextColor.YELLOW)));
        if (feature == Feature.NO_CREEPER_GRIEF) {
            boolean damage = features.isCreeperDamageEnabled();
            lore.add(plain("Creeper-Schaden: ", NamedTextColor.GRAY)
                    .append(plain(damage ? "An" : "Aus",
                            damage ? NamedTextColor.GREEN : NamedTextColor.RED)));
            lore.add(plain("Rechtsklick: Schaden umschalten", NamedTextColor.YELLOW));
        }
        meta.lore(lore);
        meta.setEnchantmentGlintOverride(enabled);
        item.setItemMeta(meta);
        return item;
    }

    private static Component plain(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
