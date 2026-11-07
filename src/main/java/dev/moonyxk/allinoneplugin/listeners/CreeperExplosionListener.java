package dev.moonyxk.allinoneplugin.listeners;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class CreeperExplosionListener implements Listener {

    private final FeatureManager features;

    public CreeperExplosionListener(FeatureManager features) {
        this.features = features;
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (!features.isEnabled(Feature.NO_CREEPER_GRIEF)) {
            return;
        }
        if (event.getEntity() instanceof Creeper creeper) {
            // A negative radius stops the explosion from breaking blocks.
            event.setRadius(creeper.isPowered() ? -6.0F : -3.0F);
        }
    }
}
