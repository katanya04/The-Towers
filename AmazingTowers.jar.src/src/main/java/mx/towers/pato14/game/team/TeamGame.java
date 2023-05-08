package mx.towers.pato14.game.team;

import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;

import java.util.*;


public class TeamGame {
    private final Map<mx.towers.pato14.utils.enums.Team, Team> teams;
    private final int numberOfTeams;

    public TeamGame(Game game) {
        this.teams = new HashMap<>();
        numberOfTeams = game.getNumberOfTeams();
        for (mx.towers.pato14.utils.enums.Team team : mx.towers.pato14.utils.enums.Team.getMatchTeams(game.getNumberOfTeams())) {
            String teamName = team.name().toLowerCase();
            Team currentTeam = new Team(teamName);
            currentTeam.setPrefix(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("Options.team." + teamName + ".prefix"));

            teams.put(team, currentTeam);
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

    public Team getTeam(mx.towers.pato14.utils.enums.Team team) {
        return this.teams.get(team);
    }

    public Team getTeamByPlayer(Player p) {
        for (mx.towers.pato14.utils.enums.Team team : mx.towers.pato14.utils.enums.Team.getMatchTeams(numberOfTeams)) {
            if (teams.get(team).containsPlayer(p.getName()))
                return teams.get(team);
        }
        return null;
    }

    public int getLowestTeamPlayers() {
        int toret = Integer.MAX_VALUE;
        for (mx.towers.pato14.utils.enums.Team team : mx.towers.pato14.utils.enums.Team.getMatchTeams(numberOfTeams)) {
            if (teams.get(team).getSizePlayers() < toret)
                toret = teams.get(team).getSizePlayers();
        }
        return toret;
    }

}


