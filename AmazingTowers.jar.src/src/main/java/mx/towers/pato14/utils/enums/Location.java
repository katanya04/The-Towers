package mx.towers.pato14.utils.enums;

public enum Location {
    LOBBY("LOCATIONS.GENERAL.LOBBY"), //coords
    SPAWN("LOCATIONS.{TEAMCOLOR}.SPAWN"), //coords
    GENERATOR("LOCATIONS.GENERAL.GENERATORS"), //type, amount, coords
    POOL("LOCATIONS.{TEAMCOLOR}.POOL"), //1, 2
    PROTECTED("LOCATIONS.PROTECTED"), //List [1, 2]
    MAP_BORDER("LOCATIONS.GENERAL.MAP_BORDER"), //1, 2
    CHEST_PROTECT("LOCATIONS.{TEAMCOLOR}.CHEST_PROTECT"), //List [1, 2]
    POOL_ROOM("LOCATIONS.{TEAMCOLOR}.POOL_ROOM"); //1, 2
    private final String path;
    Location(String path) {
        this.path = path;
    }
    public String getPath(TeamColor teamColor) {
        return teamColor == null ? path : path.replace("{TEAMCOLOR}", teamColor.getName());
    }

    public String getPath() {
        return path;
    }

}
