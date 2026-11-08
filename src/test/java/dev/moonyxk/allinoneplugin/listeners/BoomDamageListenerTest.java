package dev.moonyxk.allinoneplugin.listeners;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.WorldCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoomDamageListenerTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void createsVisualOnlyEffectWhenDamageIsDisabled() {
        BoomDamageListener listener = new BoomDamageListener();
        List<Particle> particles = new ArrayList<>();
        List<Sound> sounds = new ArrayList<>();
        List<Location> explosions = new ArrayList<>();
        WorldMock world = new WorldMock(new WorldCreator("damage-test")) {
            @Override
            public boolean createExplosion(double x, double y, double z, float power,
                    boolean setFire, boolean breakBlocks) {
                explosions.add(new Location(this, x, y, z));
                return true;
            }

            @Override
            public void spawnParticle(Particle particle, Location location, int count) {
                particles.add(particle);
            }

            @Override
            public void playSound(Location location, Sound sound, float volume, float pitch) {
                sounds.add(sound);
            }
        };
        server.addWorld(world);
        Location location = new Location(world, 0, 70, 0);

        assertTrue(listener.createExplosion(world, location, 3.0F, false));
        assertEquals(List.of(Particle.EXPLOSION), particles);
        assertEquals(List.of(Sound.ENTITY_GENERIC_EXPLODE), sounds);
        assertTrue(explosions.isEmpty());

        assertTrue(listener.createExplosion(world, location, 3.0F, true));
        assertEquals(1, explosions.size());
    }
}
