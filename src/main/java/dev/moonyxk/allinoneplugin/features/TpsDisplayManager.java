package dev.moonyxk.allinoneplugin.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class TpsDisplayManager implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey enabledKey;
    private final NamespacedKey modeKey;
    private final Map<UUID, Player> activePlayers = new HashMap<>();
    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private BukkitTask updateTask;

    public TpsDisplayManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabledKey = new NamespacedKey(plugin, "tabtps-enabled");
        this.modeKey = new NamespacedKey(plugin, "tabtps-mode");
    }

    public boolean isEnabledFor(Player player) {
        return player.getPersistentDataContainer().getOrDefault(
                enabledKey, PersistentDataType.BOOLEAN, false);
    }

    public TpsDisplayMode modeFor(Player player) {
        String mode = player.getPersistentDataContainer().get(modeKey, PersistentDataType.STRING);
        if (mode == null) {
            return TpsDisplayMode.TAB;
        }
        return switch (mode) {
            case "ACTION_BAR" -> TpsDisplayMode.ACTION_BAR;
            case "BOSS_BAR" -> TpsDisplayMode.BOSS_BAR;
            default -> TpsDisplayMode.TAB;
        };
    }

    public boolean toggleFor(Player player) {
        boolean enabled = !isEnabledFor(player);
        player.getPersistentDataContainer().set(enabledKey, PersistentDataType.BOOLEAN, enabled);
        if (enabled) {
            startDisplay(player);
        } else {
            stopDisplay(player);
        }
        return enabled;
    }

    public TpsDisplayMode cycleModeFor(Player player) {
        TpsDisplayMode currentMode = modeFor(player);
        boolean enabled = isEnabledFor(player);
        if (enabled) {
            clearDisplay(player, currentMode);
        }

        TpsDisplayMode nextMode = currentMode.next();
        player.getPersistentDataContainer().set(modeKey, PersistentDataType.STRING, nextMode.name());
        if (enabled) {
            startDisplay(player);
        }
        return nextMode;
    }

    public void restoreOnlinePlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (isEnabledFor(player)) {
                startDisplay(player);
            }
        }
    }

    public void shutdown() {
        for (Player player : new ArrayList<>(activePlayers.values())) {
            clearDisplay(player, modeFor(player));
        }
        activePlayers.clear();
        bossBars.clear();
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isEnabledFor(player)) {
            startDisplay(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (activePlayers.containsKey(player.getUniqueId())) {
            stopDisplay(player);
        }
    }

    private void startDisplay(Player player) {
        activePlayers.put(player.getUniqueId(), player);
        if (updateTask == null) {
            updateTask = plugin.getServer().getScheduler().runTaskTimer(
                    plugin, this::updateActivePlayers, 20L, 20L);
        }
        updateDisplay(player, currentTps(), currentMspt());
    }

    private void stopDisplay(Player player) {
        activePlayers.remove(player.getUniqueId());
        clearDisplay(player, modeFor(player));
        if (activePlayers.isEmpty() && updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    private void updateActivePlayers() {
        double tps = currentTps();
        double mspt = currentMspt();
        for (Player player : activePlayers.values()) {
            updateDisplay(player, tps, mspt);
        }
    }

    private void updateDisplay(Player player, double tps, double mspt) {
        TpsDisplayMode mode = modeFor(player);
        boolean includePing = mode != TpsDisplayMode.TAB;
        Component display = createDisplay(player, tps, mspt, includePing);
        switch (mode) {
            case TAB -> player.sendPlayerListHeaderAndFooter(Component.empty(), display);
            case ACTION_BAR -> player.sendActionBar(display);
            case BOSS_BAR -> updateBossBar(player, display, mspt);
        }
    }

    private void updateBossBar(Player player, Component display, double mspt) {
        UUID playerId = player.getUniqueId();
        float progress = (float) clamp(mspt / 50.0, 0.0, 1.0);
        BossBar.Color color = mspt <= 25.0
                ? BossBar.Color.GREEN
                : mspt <= 40.0 ? BossBar.Color.YELLOW : BossBar.Color.RED;
        BossBar bossBar = bossBars.get(playerId);
        if (bossBar == null) {
            bossBar = BossBar.bossBar(display, progress, color, BossBar.Overlay.PROGRESS);
            bossBars.put(playerId, bossBar);
            player.showBossBar(bossBar);
        } else {
            bossBar.name(display);
            bossBar.progress(progress);
            bossBar.color(color);
        }
    }

    private void clearDisplay(Player player, TpsDisplayMode mode) {
        switch (mode) {
            case TAB -> player.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
            case ACTION_BAR -> player.sendActionBar(Component.empty());
            case BOSS_BAR -> {
                BossBar bossBar = bossBars.remove(player.getUniqueId());
                if (bossBar != null) {
                    player.hideBossBar(bossBar);
                }
            }
        }
    }

    private double currentTps() {
        double[] recentTps = plugin.getServer().getTPS();
        return recentTps.length == 0 ? 0.0 : clamp(recentTps[0], 0.0, 20.0);
    }

    private double currentMspt() {
        double mspt = plugin.getServer().getAverageTickTime();
        return Double.isFinite(mspt) ? Math.max(0.0, mspt) : 0.0;
    }

    private static Component createDisplay(Player player, double tps, double mspt, boolean includePing) {
        Component display = Component.text("TPS: ", NamedTextColor.GRAY)
                .append(Component.text(format(tps), tpsColor(tps)))
                .append(Component.text(" | MSPT: ", NamedTextColor.GRAY))
                .append(Component.text(format(mspt), msptColor(mspt)))
                .append(Component.text(" ms", NamedTextColor.GRAY));
        if (includePing) {
            display = display.append(Component.text(" | Ping: ", NamedTextColor.GRAY))
                    .append(Component.text(Math.max(0, player.getPing()) + " ms", NamedTextColor.AQUA));
        }
        return display;
    }

    private static NamedTextColor tpsColor(double tps) {
        if (tps >= 18.5) {
            return NamedTextColor.GREEN;
        }
        return tps > 15.0 ? NamedTextColor.YELLOW : NamedTextColor.RED;
    }

    private static NamedTextColor msptColor(double mspt) {
        if (mspt <= 25.0) {
            return NamedTextColor.GREEN;
        }
        return mspt <= 40.0 ? NamedTextColor.YELLOW : NamedTextColor.RED;
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Double.isFinite(value) ? Math.max(minimum, Math.min(maximum, value)) : minimum;
    }
}
