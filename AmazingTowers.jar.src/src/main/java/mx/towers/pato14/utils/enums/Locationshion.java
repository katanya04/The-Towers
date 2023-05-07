package mx.towers.pato14.utils.enums;

public enum Locationshion {
    LOBBY("LOCATIONS.LOBBY", true),
    RED_SPAWN("LOCATIONS.RED_SPAWN", true),
    BLUE_SPAWN("LOCATIONS.BLUE_SPAWN", true),
    IRON_GENERATOR("LOCATIONS.GENERATOR.IRON", true),
    XPBOTTLES_GENERATOR("LOCATIONS.GENERATOR.XPBOTTLES", true),
    LAPISLAZULI_GENERATOR("LOCATIONS.GENERATOR.LAPISLAZULI", true),
    POOL_RED_1("LOCATIONS.POOLS.RED.1", true),
    POOL_RED_2("LOCATIONS.POOLS.RED.2", true),
    POOL_BLUE_1("LOCATIONS.POOLS.BLUE.1", true),
    POOL_BLUE_2("LOCATIONS.POOLS.BLUE.2", true),
    LOBBY_PROTECT_1("LOCATIONS.PROTECT.LOBBY.1", true),
    LOBBY_PROTECT_2("LOCATIONS.PROTECT.LOBBY.2", true),
    SPAWNRED_PROTECT_1("LOCATIONS.PROTECT.RED.1", true),
    SPAWNRED_PROTECT_2("LOCATIONS.PROTECT.RED.2", true),
    SPAWNBLUE_PROTECT_1("LOCATIONS.PROTECT.BLUE.1", true),
    SPAWNBLUE_PROTECT_2("LOCATIONS.PROTECT.BLUE.2", true),
    BORDER_1("LOCATIONS.PROTECT.BORDER.1", true),
    BORDER_2("LOCATIONS.PROTECT.BORDER.2", true),
    CHESTPROTECTBLUE1("LOCATIONS.PROTECT.BLUE.CHEST.1", true),
    CHESTPROTECTBLUE2("LOCATIONS.PROTECT.BLUE.CHEST.2", true),
    CHESTPROTECTRED1("LOCATIONS.PROTECT.RED.CHEST.1", true),
    CHESTPROTECTRED2("LOCATIONS.PROTECT.RED.CHEST.2", true),
    BRIDGERED1("LOCATIONS.PROTECT.REDBRIDGE.1", true),
    BRIDGERED2("LOCATIONS.PROTECT.REDBRIDGE.2", true),
    BRIDGEBLUE1("LOCATIONS.PROTECT.BLUEBRIDGE.1", true),
    BRIDGEBLUE2("LOCATIONS.PROTECT.BLUEBRIDGE.2", true),
    CHESTROOM1RED1("LOCATIONS.PROTECT.REDCHESTROOM1.1", true),
    CHESTROOM1RED2("LOCATIONS.PROTECT.REDCHESTROOM1.2", true),
    CHESTROOM2RED1("LOCATIONS.PROTECT.REDCHESTROOM2.1", true),
    CHESTROOM2RED2("LOCATIONS.PROTECT.REDCHESTROOM2.2", true),
    CHESTROOM1BLUE1("LOCATIONS.PROTECT.BLUECHESTROOM1.1", true),
    CHESTROOM1BLUE2("LOCATIONS.PROTECT.BLUECHESTROOM1.2", true),
    CHESTROOM2BLUE1("LOCATIONS.PROTECT.BLUECHESTROOM2.1", true),
    CHESTROOM2BLUE2("LOCATIONS.PROTECT.BLUECHESTROOM2.2", true),
    POINTBLUE1("LOCATIONS.PROTECT.BLUEPOINT.1", true),
    POINTBLUE2("LOCATIONS.PROTECT.BLUEPOINT.2", true),
    POINTRED1("LOCATIONS.PROTECT.REDPOINT.1", true),
    POINTRED2("LOCATIONS.PROTECT.REDPOINT.2", true);
    private final String string;
    private final Boolean booleano;

    Locationshion(String string, Boolean booleano) {
        this.string = string;
        this.booleano = booleano;
    }

    public String getLocationString() {
        return this.string;
    }

    public boolean getLocationObligatory() {
        return this.booleano;
    }
}


