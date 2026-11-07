package dev.moonyxk.allinoneplugin.listeners;

import dev.moonyxk.allinoneplugin.AllInOnePlugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {

    private final AllInOnePlugin plugin;

    public PlayerChatListener(AllInOnePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component message = event.message();
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.sendMessage(Component.text("Du hast geschrieben: ", NamedTextColor.GREEN)
                    .append(message.color(NamedTextColor.WHITE)));
            plugin.getLogger().info(() -> player.getName() + " schrieb: " + plainMessage);
        });
    }
}