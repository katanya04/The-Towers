package mx.towers.pato14.utils.enums;

public enum Location {
    LOBBY("LOCATIONS.GENERAL.LOBBY", LocationType.POINT, false, false), //coords
    SPAWN("LOCATIONS.{TEAMCOLOR}.SPAWN", LocationType.POINT, false, true), //coords
    GENERATOR("LOCATIONS.GENERAL.GENERATORS", LocationType.GENERATOR, true, false), //type, amount, coords
    POOL("LOCATIONS.{TEAMCOLOR}.POOL", LocationType.AREA, false, true), //1, 2
    PROTECTED("LOCATIONS.PROTECTED", LocationType.AREA, true, false), //List [1, 2]
    MAP_BORDER("LOCATIONS.GENERAL.MAP_BORDER", LocationType.AREA, false, false), //1, 2
    CHEST_PROTECT("LOCATIONS.{TEAMCOLOR}.CHEST_PROTECT", LocationType.AREA, true, true), //List [1, 2]
    POOL_ROOM("LOCATIONS.{TEAMCOLOR}.POOL_ROOM", LocationType.AREA, false, true); //1, 2
    private final String path;
    private final LocationType locationType;
    private final boolean isList;
    private final boolean needsTeamColor;
    Location(String path, LocationType locationType, boolean isList, boolean needsTeamColor) {
        this.path = path;
        this.locationType = locationType;
        this.isList = isList;
        this.needsTeamColor = needsTeamColor;
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

    public boolean needsTeamColor() {
        return needsTeamColor;
    }
}
