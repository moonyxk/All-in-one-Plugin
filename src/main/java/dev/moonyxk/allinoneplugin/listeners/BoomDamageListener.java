package dev.moonyxk.allinoneplugin.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

public class BoomDamageListener {

    public boolean createExplosion(World world, Location location, float power, boolean damage) {
        if (!damage) {
            world.spawnParticle(Particle.EXPLOSION, location, 1);
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            return true;
        }

        return world.createExplosion(location.getX(), location.getY(), location.getZ(), power, false, false);
    }
}
