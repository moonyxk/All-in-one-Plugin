package dev.moonyxk.allinoneplugin;

import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.PlayerMenu;
import dev.moonyxk.allinoneplugin.features.SigmaMenu;
import dev.moonyxk.allinoneplugin.commands.SigmaCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.ExplosionResult;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.Creeper;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
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
        for (String name : new String[] {"heal", "feed", "fly", "kick", "boom", "sigma", "menu", "chestpunch"}) {
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
        List<Boolean> damageCancelled = new ArrayList<>();
        AtomicBoolean explosionAllowed = new AtomicBoolean(true);
        WorldMock world = new WorldMock(new WorldCreator("explosion-test")) {
            @Override
            public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
                assertEquals(3.0f, power);
                assertFalse(setFire);
                assertFalse(breakBlocks);
                explosions.add(new Location(this, x, y, z));
                EntityDamageEvent damageEvent = new EntityDamageEvent(
                        target, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, 4.0);
                server.getPluginManager().callEvent(damageEvent);
                damageCancelled.add(damageEvent.isCancelled());
                return explosionAllowed.get();
            }
        };
        server.addWorld(world);
        assertTrue(target.teleport(new Location(world, 12, 70, -8)));
        assertTrue(moderator.teleport(new Location(world, -12, 70, 8)));
        clearMessages(moderator);
        clearMessages(target);

        assertTrue(moderator.performCommand("allinoneplugin:boom Target maybe"));
        assertEquals(Component.text("Bitte gib für damage true oder false an.", NamedTextColor.RED),
                moderator.nextComponentMessage());
        assertTrue(explosions.isEmpty());

        assertTrue(moderator.performCommand("allinoneplugin:boom Target"));
        assertEquals(1, explosions.size());
        assertEquals(target.getLocation(), explosions.get(0));
        assertFalse(damageCancelled.get(0));

        clearMessages(moderator);
        clearMessages(target);
        assertTrue(moderator.performCommand("allinoneplugin:boom Target false"));
        assertTrue(damageCancelled.get(1));
        assertEquals(2, explosions.size());

        clearMessages(moderator);
        clearMessages(target);
        assertTrue(moderator.performCommand("allinoneplugin:boom Target true"));
        assertFalse(damageCancelled.get(2));
        assertEquals(3, explosions.size());

        clearMessages(moderator);
        clearMessages(target);
        explosionAllowed.set(false);
        assertTrue(moderator.performCommand("allinoneplugin:boom Target"));
        assertEquals(Component.text("Die Explosion bei Target konnte nicht ausgelöst werden.", NamedTextColor.RED),
                moderator.nextComponentMessage());
        assertNull(target.nextComponentMessage());
        assertFalse(damageCancelled.get(3));
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
            String usage = name.equals("boom")
                    ? "/boom <Spieler> [true|false]"
                    : "/" + name + " <Spieler>";
            assertEquals(Component.text(usage),
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
        assertFalse(plugin.getConfig().getBoolean("features.first-join-recipes"));
        assertNull(plugin.getCommand("sigma").getPermission());
        assertEquals(PermissionDefault.OP, server.getPluginManager().getPermission(SigmaCommand.PERMISSION).getDefault());

        assertTrue(admin.performCommand("allinoneplugin:sigma"));
        InventoryView view = admin.getOpenInventory();
        assertInstanceOf(SigmaMenu.class, view.getTopInventory().getHolder(false));
        assertEquals(Material.CREEPER_HEAD, view.getTopInventory().getItem(Feature.NO_CREEPER_GRIEF.ordinal()).getType());
        assertEquals(Material.KNOWLEDGE_BOOK,
                view.getTopInventory().getItem(Feature.FIRST_JOIN_RECIPES.ordinal()).getType());

        InventoryClickEvent rightClick = new InventoryClickEvent(
                view, InventoryType.SlotType.CONTAINER, Feature.NO_CREEPER_GRIEF.ordinal(),
                ClickType.RIGHT, InventoryAction.PICKUP_ALL);
        server.getPluginManager().callEvent(rightClick);
        assertTrue(rightClick.isCancelled());
        assertFalse(plugin.getConfig().getBoolean("features.no-creeper-grief-damage"));

        rightClick = new InventoryClickEvent(
                view, InventoryType.SlotType.CONTAINER, Feature.NO_CREEPER_GRIEF.ordinal(),
                ClickType.RIGHT, InventoryAction.PICKUP_ALL);
        server.getPluginManager().callEvent(rightClick);
        assertTrue(plugin.getConfig().getBoolean("features.no-creeper-grief-damage"));

        InventoryClickEvent click = admin.simulateInventoryClick(view, Feature.NO_CREEPER_GRIEF.ordinal());
        assertTrue(click.isCancelled());
        assertFalse(plugin.getConfig().getBoolean("features.no-creeper-grief"));
        assertTrue(plugin.getConfig().getBoolean("features.chest-punch"));

        admin.simulateInventoryClick(view, Feature.NO_CREEPER_GRIEF.ordinal());
        assertTrue(plugin.getConfig().getBoolean("features.no-creeper-grief"));

        admin.simulateInventoryClick(view, Feature.FIRST_JOIN_RECIPES.ordinal());
        assertTrue(plugin.getConfig().getBoolean("features.first-join-recipes"));
        assertTrue(admin.performCommand("allinoneplugin:sigma first-join-recipes on"));
        assertTrue(plugin.getConfig().getBoolean("features.first-join-recipes"));
        admin.simulateInventoryClick(view, Feature.FIRST_JOIN_RECIPES.ordinal());
        assertFalse(plugin.getConfig().getBoolean("features.first-join-recipes"));

        PlayerMock player = server.addPlayer("Player");
        assertTrue(player.performCommand("allinoneplugin:sigma"));
        assertInstanceOf(PlayerMenu.class, player.getOpenInventory().getTopInventory().getHolder(false));
        assertTrue(player.performCommand("allinoneplugin:sigma chest-punch off"));
        assertTrue(plugin.getConfig().getBoolean("features.chest-punch"));

        PlayerMock permittedPlayer = server.addPlayer("PermittedPlayer");
        permittedPlayer.addAttachment(plugin, SigmaCommand.PERMISSION, true);
        assertTrue(permittedPlayer.performCommand("allinoneplugin:sigma"));
        assertInstanceOf(SigmaMenu.class, permittedPlayer.getOpenInventory().getTopInventory().getHolder(false));

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:sigma chest-punch off"));
        assertFalse(plugin.getConfig().getBoolean("features.chest-punch"));
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:sigma chest-punch"));
        assertTrue(plugin.getConfig().getBoolean("features.chest-punch"));
    }

    @Test
    void personalMenuChangesOnlyThePlayersChestPunchAndSoundSettings() {
        PlayerMock first = server.addPlayer("First");
        PlayerMock second = server.addPlayer("Second");

        assertTrue(first.performCommand("allinoneplugin:menu"));
        InventoryView firstView = first.getOpenInventory();
        PlayerMenu firstMenu = assertInstanceOf(
                PlayerMenu.class, firstView.getTopInventory().getHolder(false));
        assertEquals(Material.CHEST,
                firstView.getTopInventory().getItem(PlayerMenu.CHEST_PUNCH_SLOT).getType());
        assertEquals(Material.NOTE_BLOCK,
                firstView.getTopInventory().getItem(PlayerMenu.SOUND_SLOT).getType());

        assertTrue(second.performCommand("allinoneplugin:menu"));
        PlayerMenu secondMenu = assertInstanceOf(
                PlayerMenu.class, second.getOpenInventory().getTopInventory().getHolder(false));
        assertNotNull(firstMenu);
        assertNotNull(secondMenu);
        assertTrue(firstMenu.getInventory() != secondMenu.getInventory());

        first.simulateInventoryClick(firstView, PlayerMenu.CHEST_PUNCH_SLOT);
        assertEquals(Material.GRAY_DYE,
                firstView.getTopInventory().getItem(PlayerMenu.CHEST_PUNCH_SLOT).getType());
        assertEquals(Material.CHEST,
                second.getOpenInventory().getTopInventory().getItem(PlayerMenu.CHEST_PUNCH_SLOT).getType());

        first.simulateInventoryClick(firstView, PlayerMenu.SOUND_SLOT);
        assertEquals(Material.GRAY_DYE,
                firstView.getTopInventory().getItem(PlayerMenu.SOUND_SLOT).getType());
        assertEquals(Material.NOTE_BLOCK,
                second.getOpenInventory().getTopInventory().getItem(PlayerMenu.SOUND_SLOT).getType());
        assertTrue(plugin.getConfig().getBoolean("features.chest-punch"));

        WorldMock world = server.addSimpleWorld("personal-chest-test");
        Block chest = world.getBlockAt(0, 70, 0);
        chest.setType(Material.CHEST);
        first.getInventory().setItemInMainHand(new ItemStack(Material.DIRT));
        second.getInventory().setItemInMainHand(new ItemStack(Material.STONE));
        punch(first, chest);
        assertEquals(Material.DIRT, first.getInventory().getItemInMainHand().getType());
        punch(second, chest);
        assertTrue(second.getInventory().getItemInMainHand().isEmpty());

        first.simulateInventoryClick(firstView, PlayerMenu.CHEST_PUNCH_SLOT);
        first.getInventory().setItemInMainHand(new ItemStack(Material.DIRT));
        punch(first, chest);
        assertTrue(first.getInventory().getItemInMainHand().isEmpty());
    }

    @Test
    void noCreeperGriefCanBeToggled() {
        WorldMock world = server.addSimpleWorld("creeper-test");
        Creeper creeper = world.spawn(new Location(world, 0, 70, 0), Creeper.class);

        ExplosionPrimeEvent event = new ExplosionPrimeEvent(creeper, 3.0F, false);
        server.getPluginManager().callEvent(event);
        assertEquals(3.0F, event.getRadius());

        Block affectedBlock = world.getBlockAt(1, 70, 0);
        EntityExplodeEvent explodeEvent = new EntityExplodeEvent(
                creeper, creeper.getLocation(), new ArrayList<>(List.of(affectedBlock)),
                1.0F, ExplosionResult.DESTROY);
        server.getPluginManager().callEvent(explodeEvent);
        assertTrue(explodeEvent.blockList().isEmpty());
        assertEquals(0.0F, explodeEvent.getYield());

        plugin.getConfig().set("features.no-creeper-grief-damage", false);
        event = new ExplosionPrimeEvent(creeper, 3.0F, false);
        server.getPluginManager().callEvent(event);
        assertEquals(-3.0F, event.getRadius());

        plugin.getConfig().set("features.no-creeper-grief-damage", true);
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "allinoneplugin:sigma no-creeper-grief off"));
        event = new ExplosionPrimeEvent(creeper, 3.0F, false);
        server.getPluginManager().callEvent(event);
        assertEquals(3.0F, event.getRadius());

        explodeEvent = new EntityExplodeEvent(
                creeper, creeper.getLocation(), new ArrayList<>(List.of(affectedBlock)),
                1.0F, ExplosionResult.DESTROY);
        server.getPluginManager().callEvent(explodeEvent);
        assertEquals(1, explodeEvent.blockList().size());
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
