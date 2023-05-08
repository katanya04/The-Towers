package mx.towers.pato14.game.team;

import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.entity.Player;

import java.util.*;


public class GameTeams {
    private final Map<TeamColor, Team> teams;

    public GameTeams(Game game) {
        this.teams = new HashMap<>();
        for (TeamColor teamColor : TeamColor.getMatchTeams(game.getNumberOfTeams())) {
            String teamName = teamColor.name().toLowerCase();
            Team currentTeam = new Team(teamName);
            currentTeam.setPrefix(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("Options.team." + teamName + ".prefix"));

            teams.put(teamColor, currentTeam);
        }
    }

    public void removePlayer(Player player) {
        for (Team team : teams.values()) {
            if (team.containsPlayer(player.getName())) {
                team.removePlayer(player);
                return;
            }
        }
    }

    public boolean containsTeamPlayer(String namePlayer) {
        for (Team team : teams.values()) {
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
        return this.teams.get(teamColor);
    }

    public Team getTeamByPlayer(Player p) {
        for (Map.Entry<TeamColor, Team> team: this.teams.entrySet()) {
            if (team.getValue().containsPlayer(p.getName()))
                return team.getValue();
        }
        return null;
    }

    public TeamColor getTeamColorByPlayer(Player p) {
        for (Map.Entry<TeamColor, Team> team: this.teams.entrySet()) {
            if (team.getValue().containsPlayer(p.getName()))
                return team.getKey();
        }
        return null;
    }

    public int getLowestTeamPlayers() {
        int toret = Integer.MAX_VALUE;
        for (Map.Entry<TeamColor, Team> team: this.teams.entrySet()) {
            if (team.getValue().getSizePlayers() < toret)
                toret = team.getValue().getSizePlayers();
        }
        return toret;
    }

    public Map<TeamColor, Team> getTeams() {
        return this.teams;
    }

}


