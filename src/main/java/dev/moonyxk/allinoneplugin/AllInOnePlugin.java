package dev.moonyxk.allinoneplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import dev.moonyxk.allinoneplugin.commands.BoomCommand;
import dev.moonyxk.allinoneplugin.commands.FeedCommand;
import dev.moonyxk.allinoneplugin.commands.FlyCommand;
import dev.moonyxk.allinoneplugin.commands.HealCommand;
import dev.moonyxk.allinoneplugin.commands.KickCommand;
import dev.moonyxk.allinoneplugin.listeners.BlockBreakListener;
import dev.moonyxk.allinoneplugin.listeners.PlayerChatListener;
import dev.moonyxk.allinoneplugin.listeners.PlayerJoinListener;

public class AllInOnePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin aktivieren
        getLogger().info("AllInOnePlugin wurde erfolgreich aktiviert!");

        // Konfiguration laden/generieren
        saveDefaultConfig();

        registerCommand("heal", new HealCommand());
        registerCommand("feed", new FeedCommand());
        registerCommand("fly", new FlyCommand());
        registerCommand("kick", new KickCommand());
        registerCommand("boom", new BoomCommand());

        // Event-Listener registrieren
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(this), this);
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            throw new IllegalStateException("Command '" + name + "' is not defined in plugin.yml");
        }
        command.setExecutor(executor);
    }

    @Override
    public void onDisable() {
        // Plugin deaktivieren
        getLogger().info("AllInOnePlugin wird deaktiviert...");
    }
}