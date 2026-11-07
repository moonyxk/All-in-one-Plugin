package dev.moonyxk.allinoneplugin;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.SigmaMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.Creeper;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        for (String name : new String[] {"heal", "feed", "fly", "kick", "boom", "sigma", "chestpunch"}) {
            assertNotNull(plugin.getCommand(name), "Command not registered: " + name);
        }
        for (String name : new String[] {"test", "balance", "pay", "shop"}) {
            assertNull(plugin.getCommand(name), "Command should be removed: " + name);
        }
    }

    @Test
    void healFeedAndFlyAffectTheSelectedPlayer() {
        PlayerMock moderator = server.addPlayer("Moderator");
        moderator.setOp(true);
        PlayerMock target = server.addPlayer("Target");
        double maximumHealth = target.getHealth();
        moderator.setHealth(7);
        moderator.setFoodLevel(6);
        target.setHealth(maximumHealth / 2);
        target.setFoodLevel(5);

        assertTrue(moderator.performCommand("allinoneplugin:heal Target"));
        assertEquals(maximumHealth, target.getHealth());
        assertEquals(20, target.getFoodLevel());
        assertEquals(7, moderator.getHealth());
        assertEquals(6, moderator.getFoodLevel());

        target.setFoodLevel(5);
        assertTrue(moderator.performCommand("allinoneplugin:feed Target"));
        assertEquals(20, target.getFoodLevel());
        assertEquals(6, moderator.getFoodLevel());

        assertFalse(target.getAllowFlight());
        assertTrue(moderator.performCommand("allinoneplugin:fly Target"));
        assertTrue(target.getAllowFlight());
        assertFalse(moderator.getAllowFlight());
        assertTrue(moderator.performCommand("allinoneplugin:fly Target"));
        assertFalse(target.getAllowFlight());

        assertTrue(moderator.performCommand("allinoneplugin:heal Moderator"));
        assertEquals(maximumHealth, moderator.getHealth());
    }

    @Test
    void boomExplodesAtTheSelectedPlayer() {
        PlayerMock moderator = server.addPlayer("Moderator");
        moderator.setOp(true);
        PlayerMock target = server.addPlayer("Target");
        List<Location> explosions = new ArrayList<>();
        AtomicBoolean explosionAllowed = new AtomicBoolean(true);
        WorldMock world = new WorldMock(new WorldCreator("explosion-test")) {
            @Override
            public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
                assertEquals(3.0f, power);
                assertFalse(setFire);
                assertFalse(breakBlocks);
                explosions.add(new Location(this, x, y, z));
                return explosionAllowed.get();
            }
        };
        server.addWorld(world);
        assertTrue(target.teleport(new Location(world, 12, 70, -8)));
        assertTrue(moderator.teleport(new Location(world, -12, 70, 8)));

        assertTrue(moderator.performCommand("allinoneplugin:boom Target"));
        assertEquals(1, explosions.size());
        assertEquals(target.getLocation(), explosions.get(0));

        clearMessages(moderator);
        clearMessages(target);
        explosionAllowed.set(false);
        assertTrue(moderator.performCommand("allinoneplugin:boom Target"));
        assertEquals(Component.text("Die Explosion bei Target konnte nicht ausgelöst werden.", NamedTextColor.RED),
                moderator.nextComponentMessage());
        assertNull(target.nextComponentMessage());
    }

    @Test
    void targetedCommandsRequireAnOnlinePlayer() {
        PlayerMock moderator = server.addPlayer("Moderator");
        moderator.setOp(true);
        PlayerMock target = server.addPlayer("Target");
        target.setHealth(10);
        target.setFoodLevel(5);
        clearMessages(moderator);

        assertFalse(moderator.performCommand("allinoneplugin:kick Target"));
        assertEquals(Component.text("/kick <Spieler> <Grund>"), moderator.nextComponentMessage());

        for (String name : new String[] {"heal", "feed", "fly", "boom"}) {
            assertFalse(moderator.performCommand("allinoneplugin:" + name));
            assertEquals(Component.text("/" + name + " <Spieler>"),
                    moderator.nextComponentMessage());
            assertTrue(moderator.performCommand("allinoneplugin:" + name + " Offline"));
            assertEquals(Component.text("Spieler nicht gefunden.", NamedTextColor.RED), moderator.nextComponentMessage());
        }

        assertFalse(moderator.performCommand("allinoneplugin:heal Target Extra"));
        assertEquals(Component.text("/heal <Spieler>"),
                moderator.nextComponentMessage());
        assertEquals(10, target.getHealth());
        assertEquals(5, target.getFoodLevel());
        assertFalse(target.getAllowFlight());
    }

    @Test
    void consoleCanTargetAPlayer() {
        PlayerMock target = server.addPlayer("Target");
        target.setFoodLevel(5);

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:feed Target"));
        assertEquals(20, target.getFoodLevel());
    }

    @Test
    void moderatorPermissionProtectsAllCommands() {
        PlayerMock sender = server.addPlayer("Moderator");
        PlayerMock target = server.addPlayer("Target");
        target.setHealth(10);
        target.setFoodLevel(5);

        for (String name : new String[] {"heal", "feed", "fly", "kick", "boom"}) {
            assertEquals("allinoneplugin.mod", plugin.getCommand(name).getPermission());
        }
        assertEquals(PermissionDefault.OP, server.getPluginManager().getPermission("allinoneplugin.mod").getDefault());

        for (String command : new String[] {"heal Target", "feed Target", "fly Target", "kick Target Test", "boom Target"}) {
            assertTrue(sender.performCommand("allinoneplugin:" + command));
        }
        assertEquals(10, target.getHealth());
        assertEquals(5, target.getFoodLevel());
        assertFalse(target.getAllowFlight());
        assertTrue(target.isOnline());

        sender.addAttachment(plugin, "allinoneplugin.mod", true);
        assertTrue(sender.performCommand("allinoneplugin:feed Target"));
        assertEquals(20, target.getFoodLevel());
        assertTrue(sender.performCommand("allinoneplugin:kick Target Test"));
        assertFalse(target.isOnline());

        PlayerMock operator = server.addPlayer("Operator");
        operator.setOp(true);
        PlayerMock otherTarget = server.addPlayer("OtherTarget");
        assertTrue(operator.performCommand("allinoneplugin:kick OtherTarget Test"));
        assertFalse(otherTarget.isOnline());
    }

    @Test
    void sigmaMenuTogglesFeatures() {
        PlayerMock admin = server.addPlayer("Admin");
        admin.setOp(true);
        assertTrue(plugin.getConfig().getBoolean("features.no-creeper-grief"));

        assertTrue(admin.performCommand("allinoneplugin:sigma"));
        InventoryView view = admin.getOpenInventory();
        assertInstanceOf(SigmaMenu.class, view.getTopInventory().getHolder(false));
        assertEquals(Material.CREEPER_HEAD, view.getTopInventory().getItem(Feature.NO_CREEPER_GRIEF.ordinal()).getType());

        InventoryClickEvent click = admin.simulateInventoryClick(view, Feature.NO_CREEPER_GRIEF.ordinal());
        assertTrue(click.isCancelled());
        assertFalse(plugin.getConfig().getBoolean("features.no-creeper-grief"));
        assertTrue(plugin.getConfig().getBoolean("features.chest-punch"));

        admin.simulateInventoryClick(view, Feature.NO_CREEPER_GRIEF.ordinal());
        assertTrue(plugin.getConfig().getBoolean("features.no-creeper-grief"));

        PlayerMock player = server.addPlayer("Player");
        assertTrue(player.performCommand("allinoneplugin:sigma"));
        assertFalse(player.getOpenInventory().getTopInventory().getHolder(false) instanceof SigmaMenu);

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:sigma chest-punch off"));
        assertFalse(plugin.getConfig().getBoolean("features.chest-punch"));
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:sigma chest-punch"));
        assertTrue(plugin.getConfig().getBoolean("features.chest-punch"));
    }

    @Test
    void noCreeperGriefCanBeToggled() {
        WorldMock world = server.addSimpleWorld("creeper-test");
        Creeper creeper = world.spawn(new Location(world, 0, 70, 0), Creeper.class);

        ExplosionPrimeEvent event = new ExplosionPrimeEvent(creeper, 3.0F, false);
        server.getPluginManager().callEvent(event);
        assertEquals(-3.0F, event.getRadius());

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:sigma no-creeper-grief off"));
        event = new ExplosionPrimeEvent(creeper, 3.0F, false);
        server.getPluginManager().callEvent(event);
        assertEquals(3.0F, event.getRadius());
    }

    @Test
    void chestPunchStoresHeldItemAndCanBeToggled() {
        WorldMock world = server.addSimpleWorld("chest-test");
        Block chest = world.getBlockAt(0, 70, 0);
        chest.setType(Material.CHEST);
        PlayerMock player = server.addPlayer("Player");

        player.getInventory().setItemInMainHand(new ItemStack(Material.DIRT, 16));
        punch(player, chest);
        assertTrue(player.getInventory().getItemInMainHand().isEmpty());
        assertTrue(((Container) chest.getState()).getInventory().contains(Material.DIRT, 16));

        assertTrue(player.performCommand("allinoneplugin:chestpunch"));
        player.getInventory().setItemInMainHand(new ItemStack(Material.STONE));
        punch(player, chest);
        assertEquals(Material.STONE, player.getInventory().getItemInMainHand().getType());

        assertTrue(player.performCommand("allinoneplugin:chestpunch"));
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:sigma chest-punch off"));
        punch(player, chest);
        assertEquals(Material.STONE, player.getInventory().getItemInMainHand().getType());

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:sigma chest-punch on"));
        player.getInventory().setItemInMainHand(new ItemStack(Material.IRON_AXE));
        punch(player, chest);
        assertEquals(Material.IRON_AXE, player.getInventory().getItemInMainHand().getType());
    }

    private void punch(PlayerMock player, Block block) {
        server.getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK,
                player.getInventory().getItemInMainHand(), block, BlockFace.UP, EquipmentSlot.HAND));
    }

    private static void clearMessages(PlayerMock player) {
        while (player.nextComponentMessage() != null) {
            // Discard join and previous command messages before checking feedback.
        }
    }
}
