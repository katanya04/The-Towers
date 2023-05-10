package mx.towers.pato14.utils.enums;

public enum Rule {
    GRIEF(false),
    PROTECT_POINT(true),
    EMERALD(true),
    REDSTONE(true),
    COAL(true),
    LAPISLAZULI(true),
    BALANCED_TEAMS(true),
    BOW(true),
    IRON_ARMOR(true),
    WATER(true),
    POTS_AND_APPLE(true),
    ENDERPEARL(true),
    TNT(true),
    ENCHANTS(true);
    private final boolean defaultState;

    Rule(boolean defaultState) {
        this.defaultState = defaultState;
    }
    public boolean getCurrentState() {
        return this.defaultState;
    }
}


