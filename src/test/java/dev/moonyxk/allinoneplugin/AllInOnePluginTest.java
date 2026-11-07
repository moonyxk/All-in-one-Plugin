package dev.moonyxk.allinoneplugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllInOnePluginTest {

    private ServerMock server;
    private AllInOnePlugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(AllInOnePlugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void enablesAndRegistersCommands() {
        assertTrue(plugin.isEnabled());
        for (String name : new String[] {
                "heal", "feed", "fly", "kick", "test", "boom", "shop", "balance", "pay"
        }) {
            assertNotNull(plugin.getCommand(name), "Command not registered: " + name);
        }
    }

    @Test
    void healFeedAndFlyWork() {
        PlayerMock player = server.addPlayer("Player");
        double maximumHealth = player.getHealth();
        player.setHealth(maximumHealth / 2);
        player.setFoodLevel(5);

        assertTrue(player.performCommand("allinoneplugin:heal"));
        assertEquals(maximumHealth, player.getHealth());
        assertEquals(20, player.getFoodLevel());

        player.setFoodLevel(5);
        assertTrue(player.performCommand("allinoneplugin:feed"));
        assertEquals(20, player.getFoodLevel());

        assertFalse(player.getAllowFlight());
        assertTrue(player.performCommand("allinoneplugin:fly"));
        assertTrue(player.getAllowFlight());
        assertTrue(player.performCommand("allinoneplugin:fly"));
        assertFalse(player.getAllowFlight());
    }

    @Test
    void kickRequiresPermission() {
        PlayerMock sender = server.addPlayer("Moderator");
        PlayerMock target = server.addPlayer("Target");

        assertTrue(sender.performCommand("allinoneplugin:kick Target Test"));
        assertTrue(target.isOnline());

        sender.setOp(true);
        assertTrue(sender.performCommand("allinoneplugin:kick Target Test"));
        assertFalse(target.isOnline());
    }
}
