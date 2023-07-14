package mx.towers.pato14.utils.enums;

public enum Location {
    LOBBY("LOCATIONS.GENERAL.LOBBY", LocationType.POINT, false), //coords
    SPAWN("LOCATIONS.{TEAMCOLOR}.SPAWN", LocationType.POINT, false), //coords
    GENERATOR("LOCATIONS.GENERAL.GENERATORS", LocationType.GENERATOR, true), //type, amount, coords
    POOL("LOCATIONS.{TEAMCOLOR}.POOL", LocationType.AREA, false), //1, 2
    PROTECTED("LOCATIONS.PROTECTED", LocationType.AREA, true), //List [1, 2]
    MAP_BORDER("LOCATIONS.GENERAL.MAP_BORDER", LocationType.AREA, false), //1, 2
    CHEST_PROTECT("LOCATIONS.{TEAMCOLOR}.CHEST_PROTECT", LocationType.AREA, true), //List [1, 2]
    POOL_ROOM("LOCATIONS.{TEAMCOLOR}.POOL_ROOM", LocationType.AREA, false); //1, 2
    private final String path;
    private final LocationType locationType;
    private final boolean isList;
    Location(String path, LocationType locationType, boolean isList) {
        this.path = path;
        this.locationType = locationType;
        this.isList = isList;
    }
    public String getPath(TeamColor teamColor) {
        return teamColor == null ? path : path.replace("{TEAMCOLOR}", teamColor.getName());
    }

    public String getPath() {
        return path;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public boolean isList() {
        return isList;
    }
}
