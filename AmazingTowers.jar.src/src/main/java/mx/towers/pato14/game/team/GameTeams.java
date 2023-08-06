package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;


public class GameTeams {
    private final List<Team> teams;
    private final Game game;
    private final ItemStack spectator;

    public GameTeams(Game game) {
        this.game = game;
        this.teams = new LinkedList<>();
        this.spectator = TeamColor.SPECTATOR.getTeamItem(game.getGameInstance());
        for (TeamColor teamColor : TeamColor.getMatchTeams(game.getGameInstance().getNumberOfTeams())) {
            String teamName = teamColor.name().toLowerCase();
            Team currentTeam = new Team(teamName, game.getGameInstance());
            currentTeam.setPrefix(teamColor.getColor() + game.getGameInstance().getConfig(ConfigType.CONFIG).getString("teams.prefixes." + teamName) + " ");
            teams.add(currentTeam);
        }
    }

    public void removePlayer(HumanEntity player) {
        for (Team team : teams) {
            if (team.containsPlayer(player.getName())) {
                team.removePlayer(player);
                return;
            }
        }
    }

    public boolean containsTeamPlayer(String namePlayer) {
        for (Team team : teams) {
            if (team.containsPlayer(namePlayer)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsTeamPlayer(Player player) {
        return containsTeamPlayer(player.getName());
    }

    public Team getTeam(TeamColor teamColor) {
        return this.teams.get(teamColor.ordinal());
    }
    public Team getTeamByPlayer(String p) {
        for (Team team : teams) {
            if (team.containsPlayer(p))
                return team;
        }
        return null;
    }
    public Team getTeamByPlayerIncludingOffline(String p) {
        for (Team team : teams) {
            if (team.containsPlayer(p) || team.containsOffline(p))
                return team;
        }
        return null;
    }
    public Team getTeamByPlayer(HumanEntity p) {
        return getTeamByPlayer(p.getName());
    }
    public TeamColor getTeamColorByPlayer(String p) {
        Team team = getTeamByPlayer(p);
        return team == null ? null : team.getTeamColor();
    }
    public TeamColor getTeamColorByPlayer(HumanEntity p) {
        return getTeamColorByPlayer(p.getName());
    }

    public int getLowestTeamPlayers() {
        int toret = Integer.MAX_VALUE;
        for (Team team : teams) {
            if (team.getSizePlayers() < toret)
                toret = team.getSizePlayers();
        }
        return toret;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public String scores() {
        StringBuilder sb = new StringBuilder();
        Iterator<Team> teamIterator = this.teams.listIterator();
        while (teamIterator.hasNext()) {
            Team t = teamIterator.next();
            sb.append(t.getTeamColor().getColor()).append("&l").append(t.getPoints());
            if (teamIterator.hasNext())
                sb.append("&r - ");
        }
        return sb.toString();
    }

    public Game getGame() {
        return game;
    }

    public ItemStack[] getLobbyItems() {
        ItemStack[] toret = new ItemStack[getTeams().size() + 1];
        for (int i = 0; i < getTeams().size(); i++) {
            toret[i] = getTeams().get(i).getLobbyItem();
        }
        toret[toret.length - 1] = spectator;
        return toret;
    }

    public Team getTeamFromLobbyItem(ItemStack itemStack) {
        for (Team team : teams)
            if (team.getLobbyItem().equals(itemStack))
                return team;
        return null;
    }
}


