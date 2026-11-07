package dev.moonyxk.allinoneplugin.features;

import org.bukkit.plugin.java.JavaPlugin;

public class FeatureManager {

    private final JavaPlugin plugin;

    public FeatureManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled(Feature feature) {
        return plugin.getConfig().getBoolean(path(feature), feature.defaultEnabled());
    }

    public void setEnabled(Feature feature, boolean enabled) {
        plugin.getConfig().set(path(feature), enabled);
        plugin.saveConfig();
    }

    public boolean toggle(Feature feature) {
        boolean enabled = !isEnabled(feature);
        setEnabled(feature, enabled);
        return enabled;
    }

    public boolean isCreeperDamageEnabled() {
        return plugin.getConfig().getBoolean("features.no-creeper-grief-damage", true);
    }

    public boolean toggleCreeperDamage() {
        boolean enabled = !isCreeperDamageEnabled();
        plugin.getConfig().set("features.no-creeper-grief-damage", enabled);
        plugin.saveConfig();
        return enabled;
    }

    private static String path(Feature feature) {
        return "features." + feature.id();
    }
}
