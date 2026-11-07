package dev.moonyxk.allinoneplugin.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class BoomDamageListener implements Listener {

    private boolean suppressExplosionDamage;

    public boolean createExplosion(World world, Location location, float power, boolean damage) {
        if (damage) {
            return world.createExplosion(location.getX(), location.getY(), location.getZ(), power, false, false);
        }

        suppressExplosionDamage = true;
        try {
            return world.createExplosion(location.getX(), location.getY(), location.getZ(), power, false, false);
        } finally {
            suppressExplosionDamage = false;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (shouldSuppressExplosionDamage(event.getCause())) {
            event.setCancelled(true);
        }
    }

    boolean shouldSuppressExplosionDamage(EntityDamageEvent.DamageCause cause) {
        return suppressExplosionDamage
                && (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
    }
}
