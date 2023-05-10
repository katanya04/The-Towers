package mx.towers.pato14.utils.enums;

import org.bukkit.Color;

import java.util.Arrays;

public enum TeamColor {
    RED(true, (short) 14, '4'),
    BLUE(true, (short) 11, '1'),
    GREEN(true, (short) 13, '2'),
    YELLOW(true, (short) 4, 'e'),
    WHITE(true, (short) 0, 'f'),
    BLACK(true, (short) 15, '0'),
    ORANGE(true, (short) 1, '6'),
    PURPLE(true, (short) 10, '5'),
    SPECTATOR(false, (short) 8, '7');

    private final boolean matchTeam;
    private final short woolColor;
    private final char color;
    private final static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE, Color.BLACK, Color.ORANGE, Color.PURPLE};
    TeamColor(boolean matchTeam, short woolColor, char color) {
        this.matchTeam = matchTeam;
        this.woolColor = woolColor;
        this.color = color;
    }
    public short getWoolColor() {
        return woolColor;
    }
    public boolean isMatchTeam() {
        return matchTeam;
    }
    public char getColor() {
        return this.color;
    }
    public static TeamColor[] getMatchTeams(int numberOfTeams) {
        return (TeamColor[]) Arrays.stream(TeamColor.values()).filter(o -> o.matchTeam).limit(numberOfTeams).toArray();
    }
    public static TeamColor[] getTeams(int numberOfTeams) {
        return (TeamColor[]) Arrays.stream(TeamColor.values()).limit(numberOfTeams).toArray();
    }
    public String firstCapitalized() {
        return this.toString().toLowerCase().replace(this.toString().toLowerCase().charAt(0), this.toString().charAt(0));
    }
    public Color getColorEnum() {
        if (this.ordinal() < colors.length - 1)
            return colors[this.ordinal()];
        else
            return null;
    }
}


