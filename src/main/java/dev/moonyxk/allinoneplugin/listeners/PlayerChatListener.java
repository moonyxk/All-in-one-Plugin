package dev.moonyxk.allinoneplugin.listeners;

import dev.moonyxk.allinoneplugin.AllInOnePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final AllInOnePlugin plugin;

    public PlayerChatListener(AllInOnePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();
        event.getPlayer().sendMessage("§aDu hast geschrieben: §f" + message);
        plugin.getLogger().info(() -> playerName + " schrieb: " + message);
    }
}