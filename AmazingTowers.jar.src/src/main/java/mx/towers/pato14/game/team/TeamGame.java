package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;

import java.util.*;


public class TeamGame {
    private final Map<mx.towers.pato14.utils.enums.Team, Team> teams;

    public TeamGame(Game game) {
        this.teams = new HashMap<>();
        for (mx.towers.pato14.utils.enums.Team team : mx.towers.pato14.utils.enums.Team.values()) {
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

}


