package xyz.niliees.allinoneplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.niliees.allinoneplugin.AllInOnePlugin;

public class PlayerJoinListener implements Listener {

    private final AllInOnePlugin plugin;

    public PlayerJoinListener(AllInOnePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String customMessage = plugin.getConfig().getString("join-message");
        if (customMessage != null && !customMessage.isEmpty()) {
            player.sendMessage(customMessage.replace("%player%", player.getName()));
        } else {
            player.sendMessage("§eWillkommen auf dem Server, " + player.getName() + "!");
        }

        // Nachricht für alle Spieler (Broadcast)
        Bukkit.broadcastMessage("§6" + player.getName() + " hat den Server betreten!");
    }
}