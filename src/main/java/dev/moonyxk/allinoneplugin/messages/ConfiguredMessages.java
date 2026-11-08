package dev.moonyxk.allinoneplugin.messages;

import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfiguredMessages {

    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    private ConfiguredMessages() {
    }

    public static Component component(
            JavaPlugin plugin, String path, String defaultMessage, Map<String, String> placeholders) {
        String message = plugin.getConfig().getString(path, defaultMessage);
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replace("%" + placeholder.getKey() + "%", placeholder.getValue());
        }
        return AMPERSAND_SERIALIZER.deserialize(message);
    }
}
