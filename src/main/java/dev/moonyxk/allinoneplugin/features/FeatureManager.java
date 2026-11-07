package dev.moonyxk.allinoneplugin.features;

import org.bukkit.plugin.java.JavaPlugin;

public class FeatureManager {

    private final JavaPlugin plugin;

    public FeatureManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled(Feature feature) {
        return plugin.getConfig().getBoolean(path(feature), true);
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

    private static String path(Feature feature) {
        return "features." + feature.id();
    }
}
