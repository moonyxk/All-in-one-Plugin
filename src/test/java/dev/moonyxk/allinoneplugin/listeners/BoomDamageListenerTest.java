package dev.moonyxk.allinoneplugin.listeners;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.event.entity.EntityDamageEvent;
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
    void suppressesOnlyExplosionDamageDuringProtectedExplosions() {
        BoomDamageListener listener = new BoomDamageListener();
        List<Boolean> suppressed = new ArrayList<>();
        WorldMock world = new WorldMock(new WorldCreator("damage-test")) {
            @Override
            public boolean createExplosion(double x, double y, double z, float power,
                    boolean setFire, boolean breakBlocks) {
                suppressed.add(listener.shouldSuppressExplosionDamage(
                        EntityDamageEvent.DamageCause.ENTITY_EXPLOSION));
                suppressed.add(listener.shouldSuppressExplosionDamage(
                        EntityDamageEvent.DamageCause.BLOCK_EXPLOSION));
                suppressed.add(listener.shouldSuppressExplosionDamage(
                        EntityDamageEvent.DamageCause.CUSTOM));
                return true;
            }
        };
        server.addWorld(world);
        Location location = new Location(world, 0, 70, 0);

        assertTrue(listener.createExplosion(world, location, 3.0F, false));
        assertEquals(List.of(true, true, false), suppressed);
        assertFalse(listener.shouldSuppressExplosionDamage(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION));

        suppressed.clear();
        assertTrue(listener.createExplosion(world, location, 3.0F, true));
        assertEquals(List.of(false, false, false), suppressed);
    }
}
