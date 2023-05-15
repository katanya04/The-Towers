package mx.towers.pato14.game.team;

import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.entity.Player;

import java.util.*;


public class GameTeams {
    private final List<Team> teams;
    private final Game game;

    public GameTeams(Game game) {
        this.game = game;
        this.teams = new LinkedList<>();
        for (TeamColor teamColor : TeamColor.getMatchTeams(game.getNumberOfTeams())) {
            String teamName = teamColor.name().toLowerCase();
            Team currentTeam = new Team(teamName);
            currentTeam.setPrefix(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("Options.team." + teamName + ".prefix"));
            teams.add(currentTeam);
        }
    }

    public void removePlayer(Player player) {
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
    public Team getTeamByPlayer(Player p) {
        return getTeamByPlayer(p.getName());
    }
    public TeamColor getTeamColorByPlayer(String p) {
        Team team = getTeamByPlayer(p);
        return team == null ? null : team.getTeamColor();
    }
    public TeamColor getTeamColorByPlayer(Player p) {
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
        for (Team t : this.teams) {
            sb.append("&").append(t.getTeamColor().getColor()).append("&l").append(t.getPoints());
        }
        return sb.toString();
    }

    public Game getGame() {
        return game;
    }
}


