package mx.towers.pato14.utils.enums;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TeamColor {
    RED(true, (short) 14, "&c"),
    BLUE(true, (short) 11, "&9"),
    GREEN(true, (short) 13, "&a"),
    YELLOW(true, (short) 4, "&e"),
    WHITE(true, (short) 0, "&f"),
    BLACK(true, (short) 15, "&0"),
    ORANGE(true, (short) 1, "&6"),
    PURPLE(true, (short) 10, "&d"),
    SPECTATOR(false, (short) 8, "&7");

    private final boolean matchTeam;
    private final short woolColor;
    private final String color;
    private final static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE, Color.BLACK, Color.ORANGE, Color.PURPLE};
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
    public static List<TeamColor> getTeams(int numberOfTeams) {
        return Arrays.stream(TeamColor.values()).limit(numberOfTeams).collect(Collectors.toList());
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

    public String getName(GameInstance gameInstance) {
        return gameInstance.getConfig(ConfigType.CONFIG).getString("teams.teamNames." + this.name().toLowerCase());
    }

    public String getNameFirstCapitalized(GameInstance gameInstance) {
        String toret = getName(gameInstance);
        return toret.length() == 0 ? toret : toret.substring(0, 1).toUpperCase() + toret.substring(1);
    }

    public static boolean isTeamColor(String teamColor) {
        try {
            TeamColor.valueOf(teamColor.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public ItemStack getTeamItem(GameInstance gameInstance) {
        ItemStack toret = new ItemStack(Material.WOOL, 1, this.woolColor);
        ItemMeta itemMeta = toret.getItemMeta();
        if (this.matchTeam)
            itemMeta.setDisplayName(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                    .getString("lobbyItems.menuItems.joinTeam").replace("%team_color%", this.color)
                    .replace("%team_name%", this.getName(gameInstance))));
        else if (this == SPECTATOR)
            itemMeta.setDisplayName(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                    .getString("lobbyItems.menuItems.spectator")));

        toret.setItemMeta(itemMeta);
        return toret;
    }
}


