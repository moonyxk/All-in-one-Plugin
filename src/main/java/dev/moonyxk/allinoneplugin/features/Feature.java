package dev.moonyxk.allinoneplugin.features;

import java.util.Locale;
import org.bukkit.Material;

public enum Feature {
    CHEST_PUNCH("chest-punch", "ChestPunch", Material.CHEST,
            "Gegenstände per Linksklick in Truhen, Fässer, Spender, Werfer und Trichter legen."),
    NO_CREEPER_GRIEF("no-creeper-grief", "NoCreeperGrief", Material.CREEPER_HEAD,
            "Creeper-Explosionen zerstören keine Blöcke; Schaden ist separat einstellbar."),
    FIRST_JOIN_RECIPES("first-join-recipes", "Alle Rezepte beim ersten Beitritt", Material.KNOWLEDGE_BOOK,
            "Gibt neuen Spielern beim ersten Beitritt alle Rezepte.", false, false);

    private final String id;
    private final String displayName;
    private final Material icon;
    private final String description;
    private final boolean commandToggleable;
    private final boolean defaultEnabled;

    Feature(String id, String displayName, Material icon, String description) {
        this(id, displayName, icon, description, true, true);
    }

    Feature(String id, String displayName, Material icon, String description, boolean commandToggleable) {
        this(id, displayName, icon, description, commandToggleable, true);
    }

    Feature(String id, String displayName, Material icon, String description,
            boolean commandToggleable, boolean defaultEnabled) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
        this.commandToggleable = commandToggleable;
        this.defaultEnabled = defaultEnabled;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Material icon() {
        return icon;
    }

    public String description() {
        return description;
    }

    public boolean commandToggleable() {
        return commandToggleable;
    }

    public boolean defaultEnabled() {
        return defaultEnabled;
    }

    public static Feature byId(String id) {
        String normalized = id.toLowerCase(Locale.ROOT);
        for (Feature feature : values()) {
            if (feature.id.equals(normalized)) {
                return feature;
            }
        }
        return null;
    }
}
