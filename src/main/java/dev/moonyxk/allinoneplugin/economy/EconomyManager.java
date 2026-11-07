package dev.moonyxk.allinoneplugin.economy;

import java.util.HashMap;
import java.util.UUID;

public class EconomyManager {

    private final HashMap<UUID, Double> balances;

    public EconomyManager() {
        this.balances = new HashMap<>();
    }

    public double getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0.0);
    }

    public void deposit(UUID playerId, double amount) {
        balances.put(playerId, getBalance(playerId) + amount);
    }

    public boolean withdraw(UUID playerId, double amount) {
        double currentBalance = getBalance(playerId);
        if (currentBalance >= amount) {
            balances.put(playerId, currentBalance - amount);
            return true;
        }
        return false;
    }
}