package mx.towers.pato14.utils.enums;

import java.util.Arrays;

public enum Team {
    RED(true, (short) 14), BLUE(true, (short) 11), SPECTATOR(false, (short) 8);
    private final boolean matchTeam;
    private final short woolColor;
    Team(boolean matchTeam, short color) {
        this.matchTeam = matchTeam;
        this.woolColor = color;
    }
    public short getWoolColor() {
        return woolColor;
    }
    public boolean isMatchTeam() {
        return matchTeam;
    }
    public static Team[] getMatchTeams(int numberOfTeams) {
        return (Team[]) Arrays.stream(Team.values()).filter(o -> o.matchTeam).limit(numberOfTeams).toArray();
    }
    public static Team[] getTeams(int numberOfTeams) {
        return (Team[]) Arrays.stream(Team.values()).limit(numberOfTeams).toArray();
    }
}


