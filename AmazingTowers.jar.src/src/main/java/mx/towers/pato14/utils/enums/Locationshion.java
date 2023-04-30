package mx.towers.pato14.utils.enums;

public enum Locationshion {
    LOBBY("LOCATIONS.LOBBY", Boolean.valueOf(true)),
    RED_SPAWN("LOCATIONS.RED_SPAWN", Boolean.valueOf(true)),
    BLUE_SPAWN("LOCATIONS.BLUE_SPAWN", Boolean.valueOf(true)),
    IRON_GENERATOR("LOCATIONS.GENERATOR.IRON", Boolean.valueOf(true)),
    XPBOTTLES_GENERATOR("LOCATIONS.GENERATOR.XPBOTTLES", Boolean.valueOf(true)),
    LAPISLAZULI_GENERATOR("LOCATIONS.GENERATOR.LAPISLAZULI", Boolean.valueOf(true)),
    POOL_RED_1("LOCATIONS.POOLS.RED.1", Boolean.valueOf(true)),
    POOL_RED_2("LOCATIONS.POOLS.RED.2", Boolean.valueOf(true)),
    POOL_BLUE_1("LOCATIONS.POOLS.BLUE.1", Boolean.valueOf(true)),
    POOL_BLUE_2("LOCATIONS.POOLS.BLUE.2", Boolean.valueOf(true)),
    LOBBY_PROTECT_1("LOCATIONS.PROTECT.LOBBY.1", Boolean.valueOf(true)),
    LOBBY_PROTECT_2("LOCATIONS.PROTECT.LOBBY.2", Boolean.valueOf(true)),
    SPAWNRED_PROTECT_1("LOCATIONS.PROTECT.RED.1", Boolean.valueOf(true)),
    SPAWNRED_PROTECT_2("LOCATIONS.PROTECT.RED.2", Boolean.valueOf(true)),
    SPAWNBLUE_PROTECT_1("LOCATIONS.PROTECT.BLUE.1", Boolean.valueOf(true)),
    SPAWNBLUE_PROTECT_2("LOCATIONS.PROTECT.BLUE.2", Boolean.valueOf(true)),
    BORDER_1("LOCATIONS.PROTECT.BORDER.1", Boolean.valueOf(true)),
    BORDER_2("LOCATIONS.PROTECT.BORDER.2", Boolean.valueOf(true)),
    CHESTPROTECTBLUE1("LOCATIONS.PROTECT.BLUE.CHEST.1", Boolean.valueOf(true)),
    CHESTPROTECTBLUE2("LOCATIONS.PROTECT.BLUE.CHEST.2", Boolean.valueOf(true)),
    CHESTPROTECTRED1("LOCATIONS.PROTECT.RED.CHEST.1", Boolean.valueOf(true)),
    CHESTPROTECTRED2("LOCATIONS.PROTECT.RED.CHEST.2", Boolean.valueOf(true)),
    BRIDGERED1("LOCATIONS.PROTECT.REDBRIDGE.1", Boolean.valueOf(true)),
    BRIDGERED2("LOCATIONS.PROTECT.REDBRIDGE.2", Boolean.valueOf(true)),
    BRIDGEBLUE1("LOCATIONS.PROTECT.BLUEBRIDGE.1", Boolean.valueOf(true)),
    BRIDGEBLUE2("LOCATIONS.PROTECT.BLUEBRIDGE.2", Boolean.valueOf(true)),
    CHESTROOM1RED1("LOCATIONS.PROTECT.REDCHESTROOM1.1", Boolean.valueOf(true)),
    CHESTROOM1RED2("LOCATIONS.PROTECT.REDCHESTROOM1.2", Boolean.valueOf(true)),
    CHESTROOM2RED1("LOCATIONS.PROTECT.REDCHESTROOM2.1", Boolean.valueOf(true)),
    CHESTROOM2RED2("LOCATIONS.PROTECT.REDCHESTROOM2.2", Boolean.valueOf(true)),
    CHESTROOM1BLUE1("LOCATIONS.PROTECT.BLUECHESTROOM1.1", Boolean.valueOf(true)),
    CHESTROOM1BLUE2("LOCATIONS.PROTECT.BLUECHESTROOM1.2", Boolean.valueOf(true)),
    CHESTROOM2BLUE1("LOCATIONS.PROTECT.BLUECHESTROOM2.1", Boolean.valueOf(true)),
    CHESTROOM2BLUE2("LOCATIONS.PROTECT.BLUECHESTROOM2.2", Boolean.valueOf(true)),
    POINTBLUE1("LOCATIONS.PROTECT.BLUEPOINT.1", Boolean.valueOf(true)),
    POINTBLUE2("LOCATIONS.PROTECT.BLUEPOINT.2", Boolean.valueOf(true)),
    POINTRED1("LOCATIONS.PROTECT.REDPOINT.1", Boolean.valueOf(true)),
    POINTRED2("LOCATIONS.PROTECT.REDPOINT.2", Boolean.valueOf(true));
    private String string;
    private Boolean booleano;

    Locationshion(String string, Boolean booleano) {
        this.string = string;
        this.booleano = booleano;
    }

    public String getLocationString() {
        return this.string;
    }

    public boolean getLocationObligatory() {
        return this.booleano.booleanValue();
    }
}


