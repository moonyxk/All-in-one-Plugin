package dev.moonyxk.allinoneplugin.listeners;

import dev.moonyxk.allinoneplugin.AllInOnePlugin;
import dev.moonyxk.allinoneplugin.features.Feature;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final AllInOnePlugin plugin;
    private final FeatureManager features;

    public PlayerJoinListener(AllInOnePlugin plugin, FeatureManager features) {
        this.plugin = plugin;
        this.features = features;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore() && features.isEnabled(Feature.FIRST_JOIN_RECIPES)) {
            boolean commandSucceeded = Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "recipe give " + player.getName() + " *");
            if (!commandSucceeded) {
                plugin.getLogger().warning("Alle Rezepte konnten " + player.getName()
                        + " beim ersten Beitritt nicht gegeben werden.");
            }
        }

        String customMessage = plugin.getConfig().getString("join-message");
        if (customMessage != null && !customMessage.isEmpty()) {
            player.sendMessage(customMessage.replace("%player%", player.getName()));
        } else {
            player.sendMessage("§eWillkommen auf dem Server, " + player.getName() + "!");
        }

        // Nachricht für alle Spieler (Broadcast)
        Bukkit.broadcast(Component.text(player.getName() + " hat den Server betreten!", NamedTextColor.GOLD));
    }
}