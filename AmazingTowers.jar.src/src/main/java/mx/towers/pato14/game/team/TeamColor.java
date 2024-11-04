package mx.towers.pato14.game.team;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Color;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TeamColor {
    RED(true, (short) 14, "&c"),
    BLUE(true, (short) 11, "&9"),
    GREEN(true, (short) 5, "&a"),
    YELLOW(true, (short) 4, "&e"),
    WHITE(true, (short) 0, "&f"),
    BLACK(true, (short) 15, "&0"),
    ORANGE(true, (short) 1, "&6"),
    PURPLE(true, (short) 10, "&d"),
    SPECTATOR(false, (short) 8, "&7");
    private final static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE, Color.BLACK, Color.ORANGE, Color.PURPLE};
    private final boolean matchTeam;
    private final short woolColor;
    private final String color;
    TeamColor(boolean matchTeam, short woolColor, String color) {
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
    public String getColor() {
        return this.color;
    }
    public static List<TeamColor> getMatchTeams(int numberOfTeams) {
        return Arrays.stream(TeamColor.values()).filter(o -> o.matchTeam).limit(numberOfTeams).collect(Collectors.toList());
    }
    public Color getColorEnum() {
        if (this.ordinal() < colors.length - 1)
            return colors[this.ordinal()];
        else
            return null;
    }

    public String getName(GameInstance gameInstance) {
        return gameInstance.getConfig(ConfigType.CONFIG).getString("teams.teamNames." + this.name().toLowerCase());
    }

    public static boolean isTeamColor(String teamColor) {
        try {
            TeamColor.valueOf(teamColor.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}