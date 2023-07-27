package mx.towers.pato14.utils.enums;

import mx.towers.pato14.GameInstance;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Location {
    LOBBY("LOCATIONS.GENERAL.LOBBY", LocationType.POINT, false, false, true), //coords
    SPAWN("LOCATIONS.{TEAMCOLOR}.SPAWN", LocationType.POINT, false, true, true), //coords
    GENERATOR("LOCATIONS.GENERAL.GENERATORS", LocationType.GENERATOR, true, false, false), //type, amount, coords
    POOL("LOCATIONS.{TEAMCOLOR}.POOL", LocationType.AREA, false, true, true), //1, 2
    PROTECTED("LOCATIONS.PROTECTED", LocationType.AREA, true, false, false), //List [1, 2]
    MAP_BORDER("LOCATIONS.GENERAL.MAP_BORDER", LocationType.AREA, false, false, false), //1, 2
    CHEST_PROTECT("LOCATIONS.{TEAMCOLOR}.CHEST_PROTECT", LocationType.AREA, false, true, false), //1, 2
    POOL_ROOM("LOCATIONS.{TEAMCOLOR}.POOL_ROOM", LocationType.AREA, false, true, false); //1, 2
    private final String path;
    private final LocationType locationType;
    private final boolean isList;
    private final boolean needsTeamColor;
    private final boolean isObligatory;
    Location(String path, LocationType locationType, boolean isList, boolean needsTeamColor, boolean isObligatory) {
        this.path = path;
        this.locationType = locationType;
        this.isList = isList;
        this.needsTeamColor = needsTeamColor;
        this.isObligatory = isObligatory;
    }
    public String getPath(TeamColor teamColor) {
        return teamColor == null ? path : path.replace("{TEAMCOLOR}", teamColor.name());
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

    public boolean isObligatory() {
        return isObligatory;
    }

    public static List<Location> getObligatoryLocations() {
        return Arrays.stream(Location.values()).filter(x -> x.isObligatory).collect(Collectors.toList());
    }

    public static List<Location> getTeamDependantLocations(boolean dependOnTeam) {
        return Arrays.stream(Location.values()).filter(x -> x.needsTeamColor == dependOnTeam).collect(Collectors.toList());
    }

    public boolean exists(GameInstance gameInstance, TeamColor teamColor) {
        if (getLocationType().equals(LocationType.POINT)) {
            String pos = gameInstance.getConfig(ConfigType.LOCATIONS).getString(this.getPath(teamColor));
            return pos != null && isValid(pos);
        } else if (getLocationType().equals(LocationType.AREA)) {
            List<String> pos = gameInstance.getConfig(ConfigType.LOCATIONS).getStringList(this.getPath(teamColor));
            try {
                String pos1 = pos.get(0);
                String pos2 = pos.get(1);
                return pos1 != null && pos2 != null && isValid(pos1) && isValid(pos2);
            } catch (IndexOutOfBoundsException exception) {
                return false;
            }
        } else
            return true;
    }

    public static boolean isValid(String st) {
        String[] i = st.split(";");
        try {
            Double.valueOf(i[1]);
            Double.valueOf(i[2]);
            Double.valueOf(i[3]);
            if (i.length >= 6) {
                Float.valueOf(i[4]);
                Float.valueOf(i[5]);
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
