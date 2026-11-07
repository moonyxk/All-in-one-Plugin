package dev.moonyxk.allinoneplugin;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import dev.moonyxk.allinoneplugin.commands.*;
import dev.moonyxk.allinoneplugin.listeners.BlockBreakListener;
import dev.moonyxk.allinoneplugin.listeners.PlayerChatListener;
import dev.moonyxk.allinoneplugin.listeners.PlayerJoinListener;
import dev.moonyxk.allinoneplugin.economy.EconomyManager;

public class AllInOnePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin aktivieren
        getLogger().info("AllInOnePlugin wurde erfolgreich aktiviert!");

        // Konfiguration laden/generieren
        saveDefaultConfig();

        getCommand("heal").setExecutor(new HealCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("test").setExecutor(new TestCommand());
        getCommand("boom").setExecutor(new BoomCommand());
        getCommand("shop").setExecutor(new ShopCommand(this));
        // Befehle und Events registrieren
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));

        // Event-Listener registrieren
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);

        // EconomyManager initialisieren
        economyManager = new EconomyManager();
    }

    @Override
    public void onDisable() {
        // Plugin deaktivieren
        getLogger().info("AllInOnePlugin wird deaktiviert...");
    }
        private EconomyManager economyManager;

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

}