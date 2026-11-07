package dev.moonyxk.allinoneplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import dev.moonyxk.allinoneplugin.commands.BoomCommand;
import dev.moonyxk.allinoneplugin.commands.ChestPunchCommand;
import dev.moonyxk.allinoneplugin.commands.FeedCommand;
import dev.moonyxk.allinoneplugin.commands.FlyCommand;
import dev.moonyxk.allinoneplugin.commands.HealCommand;
import dev.moonyxk.allinoneplugin.commands.KickCommand;
import dev.moonyxk.allinoneplugin.commands.MenuCommand;
import dev.moonyxk.allinoneplugin.commands.SigmaCommand;
import dev.moonyxk.allinoneplugin.features.FeatureManager;
import dev.moonyxk.allinoneplugin.features.PlayerMenuListener;
import dev.moonyxk.allinoneplugin.features.SigmaMenuListener;
import dev.moonyxk.allinoneplugin.listeners.BlockBreakListener;
import dev.moonyxk.allinoneplugin.listeners.BoomDamageListener;
import dev.moonyxk.allinoneplugin.listeners.ChestPunchListener;
import dev.moonyxk.allinoneplugin.listeners.CreeperExplosionListener;
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
        BoomDamageListener boomDamageListener = new BoomDamageListener();
        registerCommand("boom", new BoomCommand(boomDamageListener));

        // Umschaltbare Funktionen
        FeatureManager features = new FeatureManager(this);
        ChestPunchListener chestPunchListener = new ChestPunchListener(this, features);
        registerCommand("sigma", new SigmaCommand(features));
        registerCommand("menu", new MenuCommand(chestPunchListener));
        registerCommand("chestpunch", new ChestPunchCommand(features, chestPunchListener));

        // Event-Listener registrieren
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(boomDamageListener, this);
        Bukkit.getPluginManager().registerEvents(new SigmaMenuListener(features), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMenuListener(chestPunchListener), this);
        Bukkit.getPluginManager().registerEvents(chestPunchListener, this);
        Bukkit.getPluginManager().registerEvents(new CreeperExplosionListener(features), this);
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