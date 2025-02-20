package xyz.niliees.allinoneplugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();
        event.getPlayer().sendMessage("§aDu hast geschrieben: §f" + message);
        System.out.println(playerName + " schrieb: " + message);
    }
}