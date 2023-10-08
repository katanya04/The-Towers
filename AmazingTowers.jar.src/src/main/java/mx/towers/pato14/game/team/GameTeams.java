package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.PlayerState;
import mx.towers.pato14.utils.enums.TeamColor;

import java.util.*;


public class GameTeams {
    private final List<Team> teams;
    private final String name;

    public GameTeams(GameInstance gameInstance) {
        this.name = gameInstance.getName();
        this.teams = new LinkedList<>();
        for (TeamColor teamColor : TeamColor.getMatchTeams(gameInstance.getNumberOfTeams())) {
            Team currentTeam = new Team(teamColor, gameInstance);
            currentTeam.setPrefix(teamColor.getColor() + gameInstance.getConfig(ConfigType.CONFIG)
                    .getString("teams.prefixes." + teamColor.name().toLowerCase()) + " ");
            teams.add(currentTeam);
        }
    }

    public Team getTeamByPlayer(String p) {
        for (Team team : teams) {
            if (team.containsPlayer(p))
                return team;
        }
        return null;
    }
    public boolean containsNoRespawnPlayer(String playerName) {
        return getTeamByPlayer(playerName) != null && getTeamByPlayer(playerName).getPlayerState(playerName) == PlayerState.NO_RESPAWN;
    }
    public Team getTeam(TeamColor teamColor) {
        return this.teams.get(teamColor.ordinal());
    }
    public TeamColor getTeamColorByPlayer(String p) {
        Team team = getTeamByPlayer(p);
        return team == null ? null : team.getTeamColor();
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
        return AmazingTowers.getGameInstance(name).getGame();
    }

    public void reset() {
        teams.forEach(Team::reset);
    }
}


