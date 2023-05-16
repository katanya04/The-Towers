package mx.towers.pato14.utils.enums;

public enum Location {
    LOBBY("LOCATIONS.GENERAL.LOBBY", false, false), //coords
    SPAWN("LOCATIONS.{TEAMCOLOR}.SPAWN", false, false), //coords
    GENERATOR("LOCATIONS.GENERAL.GENERATORS", false, false), //type, amount, coords
    POOL("LOCATIONS.{TEAMCOLOR}.POOL", true, false), //1, 2
    PROTECTED("LOCATIONS.PROTECTED", true, true), //List [1, 2]
    MAP_BORDER("LOCATIONS.GENERAL.MAP_BORDER", true, false), //1, 2
    CHEST_PROTECT("LOCATIONS.{TEAMCOLOR}.CHEST_PROTECT", true, true), //List [1, 2]
    POOL_ROOM("LOCATIONS.{TEAMCOLOR}.POOL_ROOM", true, false); //1, 2
    private final String path;
    private final boolean isArea; //Can be either an area or a point
    private final boolean isList;
    Location(String path, boolean isArea, boolean isList) {
        this.path = path;
        this.isArea = isArea;
        this.isList = isList;
    }
    public String getPath(TeamColor teamColor) {
        return teamColor == null ? path : path.replace("{TEAMCOLOR}", teamColor.getName());
    }

    public String getPath() {
        return path;
    }

    public boolean isArea(){
        return isArea;
    }

    public boolean isList() {
        return isList;
    }
}
