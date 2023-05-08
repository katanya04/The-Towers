package mx.towers.pato14.utils.enums;

import java.util.Arrays;

public enum TeamColor {
    RED(true, (short) 14),
    BLUE(true, (short) 11),
    GREEN(true, (short) 13),
    YELLOW(true, (short) 4),
    WHITE(true, (short) 0),
    BLACK(true, (short) 15),
    ORANGE(true, (short) 1),
    PINK(true, (short) 6),
    SPECTATOR(false, (short) 8);

    private final boolean matchTeam;
    private final short woolColor;
    TeamColor(boolean matchTeam, short color) {
        this.matchTeam = matchTeam;
        this.woolColor = color;
    }
    public short getWoolColor() {
        return woolColor;
    }
    public boolean isMatchTeam() {
        return matchTeam;
    }
    public static TeamColor[] getMatchTeams(int numberOfTeams) {
        return (TeamColor[]) Arrays.stream(TeamColor.values()).filter(o -> o.matchTeam).limit(numberOfTeams).toArray();
    }
    public static TeamColor[] getTeams(int numberOfTeams) {
        return (TeamColor[]) Arrays.stream(TeamColor.values()).limit(numberOfTeams).toArray();
    }
}


