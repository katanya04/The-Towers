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
    private boolean booleano;

    Rule(boolean booleano) {
        this.booleano = booleano;
    }

    public boolean getCurrentState() {
        return this.booleano;
    }
    public void setCurrentState(boolean bool) {
        this.booleano = bool;
    }
}


