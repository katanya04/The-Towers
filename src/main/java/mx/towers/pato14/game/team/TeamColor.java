package mx.towers.pato14.game.team;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.items.Items;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TeamColor {
    BLUE(true, (short) 11, "&9"),
    RED(true, (short) 14, "&4"),
    GREEN(true, (short) 5, "&a"),
    YELLOW(true, (short) 4, "&e"),
    WHITE(true, (short) 0, "&f"),
    BLACK(true, (short) 15, "&0"),
    ORANGE(true, (short) 1, "&6"),
    PURPLE(true, (short) 10, "&d"),
    SPECTATOR(false, (short) 8, "&7");
    private final static Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.WHITE, Color.BLACK, Color.ORANGE, Color.PURPLE};
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
    public static List<TeamColor> getTeams(int numberOfTeams) {
        return Arrays.stream(TeamColor.values()).limit(numberOfTeams).collect(Collectors.toList());
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
        return toret.isEmpty() ? toret : toret.substring(0, 1).toUpperCase() + toret.substring(1);
    }

    public static boolean isTeamColor(String teamColor) {
        try {
            TeamColor.valueOf(teamColor.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static void createAllActionItems() {
        for (TeamColor team : TeamColor.values())
            createActionItem(team);
    }

    private static void createActionItem(TeamColor team) {
        new ActionItem<Player>(player -> team.getTeamItem(AmazingTowers.getGameInstance(player)),
                event -> {
                    GameInstance game = AmazingTowers.getGameInstance(event.getPlayer());
                    if (game == null || game.getGame() == null || game.getGame().getGameState() == GameState.EXTRA_TIME
                            || game.getGame().getGameState() == GameState.FINISH)
                        return;
                    if (team == SPECTATOR)
                        game.getGame().getTeams().joinSpectator(event.getPlayer());
                    else {
                        if (!game.getRules().get(Rule.CAPTAINS) || game.getGame().getGameState().matchIsBeingPlayed)
                            game.getGame().getTeams().getTeam(team).changeTeam(event.getPlayer());
                        else
                            Utils.sendMessage(game.getConfig(ConfigType.MESSAGES).getString("cantJoinInCaptainsMode"),
                                    MessageType.ERROR, event.getPlayer());
                    }
                },
                "JoinTeam." + team);
    }

    private ItemStack getTeamItem(GameInstance gameInstance) {
        ITeam team = gameInstance.getGame().getTeams().getTeam(this);
        if (team == null && this.isMatchTeam())
            return new ItemStack(Material.AIR);
        ItemStack toret = new ItemStack(Material.WOOL, 1, this.woolColor);
        ItemMeta itemMeta = toret.getItemMeta();
        if (this.matchTeam) {
            itemMeta.setDisplayName(Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                    .getString("lobbyItems.menuItems.joinTeam").replace("%team_color%", this.color)
                    .replace("%team_name%", this.getName(gameInstance))));
            itemMeta.setLore(team.getOnlinePlayers().stream().map(o -> "§r§7- " + o.getDisplayName()).collect(Collectors.toList()));
        } else if (this == SPECTATOR)
            itemMeta.setDisplayName(Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                    .getString("lobbyItems.menuItems.spectator")));

        toret.setItemMeta(itemMeta);
        return toret;
    }

    public static ItemStack[] getTeamItems() {
        ItemStack[] toret = new ItemStack[TeamColor.values().length];
        int i = 0;
        for (TeamColor team : TeamColor.values())
            toret[i++] = Items.getByName("JoinTeam." + team);
        return toret;
    }
}