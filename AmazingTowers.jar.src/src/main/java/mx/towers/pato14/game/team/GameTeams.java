package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.PlayerState;
import mx.towers.pato14.utils.enums.TeamColor;

import java.util.*;


public class GameTeams {
    private final List<Team> teams;
    private final String name;
    private String playersAmount;

    public GameTeams(GameInstance gameInstance) {
        this.name = gameInstance.getInternalName();
        this.teams = new LinkedList<>();
        this.playersAmount = null;
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

    public int getLowestTeamPlayers(Team originTeam) {
        int toret = Integer.MAX_VALUE;
        for (Team team : teams) {
            int players = team.equals(originTeam) ? team.getSizePlayers() - 1 : team.getSizePlayers();
            if (players < toret)
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

    public List<Team> getWinningTeams() {
        List<Team> toret = new LinkedList<>();
        teams.sort(Comparator.reverseOrder());
        for (Team team : teams) {
            if (team.compareTo(teams.get(0)) == 0)
                toret.add(team);
            else
                break;
        }
        return toret;
    }

    public String getPlayersAmount() {
        if (this.playersAmount == null)
            updatePlayersAmount();
        return this.playersAmount;
    }

    public void updatePlayersAmount() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            sb.append(team.getTeamColor().getColor()).append(team.getSizeOnlinePlayers());
            if (i != teams.size() - 1) sb.append("&r vs ");
        }
        this.playersAmount = Utils.getColor(sb.toString());
    }
}


