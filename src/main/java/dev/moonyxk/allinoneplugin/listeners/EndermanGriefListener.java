package dev.moonyxk.allinoneplugin.listeners;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EndermanGriefListener implements Listener {

    private final FeatureManager features;

    public EndermanGriefListener(FeatureManager features) {
        this.features = features;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEndermanChangeBlock(EntityChangeBlockEvent event) {
        if (features.isEnabled(Feature.NO_ENDERMAN_GRIEF)
                && event.getEntityType() == EntityType.ENDERMAN
                && !event.getBlock().getType().isAir()
                && event.getTo().isAir()) {
            event.setCancelled(true);
        }
    }
}
