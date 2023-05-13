package mx.towers.pato14.utils.enums;

public enum Location {
    LOBBY,
    SPAWN,
    GENERATOR,
    POOL,
    ANTIGRIEF_ALL_TEAMS,
    ANTIGRIEF_OTHER_TEAMS,
    MAP_BORDER,
    CHEST_PROTECT;
    public String getLocationString(TeamColor teamColor) {
        StringBuilder sb = new StringBuilder();
        sb.append("LOCATIONS");
        if (this.ordinal() < 3)
            sb.append("GENERAL");
        else
            sb.append(teamColor);
        sb.append(this);
        return sb.toString();
    }

}
