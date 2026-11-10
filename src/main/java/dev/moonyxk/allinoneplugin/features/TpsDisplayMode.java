package dev.moonyxk.allinoneplugin.features;

public enum TpsDisplayMode {
    TAB("Tabliste"),
    ACTION_BAR("Actionbar"),
    BOSS_BAR("Bossbar");

    private final String label;

    TpsDisplayMode(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public TpsDisplayMode next() {
        TpsDisplayMode[] modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }
}
