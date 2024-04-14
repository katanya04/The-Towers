package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;


public class GameTeams {
    private final String name;
    private String playersAmount;
    private final List<ITeam> teams;

    public GameTeams(GameInstance gameInstance) {
        this.name = gameInstance.getInternalName();
        this.teams = new LinkedList<>();
        this.playersAmount = null;
        for (TeamColor teamColor : TeamColor.getMatchTeams(gameInstance.getNumberOfTeams())) {
            this.teams.add(new TeamScorebord(teamColor, Utils.getColor(teamColor.getColor()) + gameInstance.getConfig(ConfigType.CONFIG)
                    .getString("teams.prefixes." + teamColor.name().toLowerCase()) + " ", this));
        }
    }

    public ITeam getTeamByPlayer(String p) {
        for (ITeam team : teams) {
            if (team.containsPlayer(p))
                return team;
        }
        return null;
    }
    public ITeam getTeam(TeamColor teamColor) {
        return teamColor.ordinal() >= this.teams.size() ? null : this.teams.get(teamColor.ordinal());
    }
    public TeamColor getTeamColorByPlayer(String p) {
        ITeam team = getTeamByPlayer(p);
        return team == null ? null : team.getTeamColor();
    }

    public int getLowestTeamPlayers(ITeam originTeam) {
        int toret = Integer.MAX_VALUE;
        for (ITeam team : teams) {
            int players = team.equals(originTeam) ? team.getNumPlayers() - 1 : team.getNumPlayers();
            if (players < toret)
                toret = team.getNumPlayers();
        }
        return toret;
    }

    public List<ITeam> getTeams() {
        return this.teams;
    }

    public String scores() {
        StringBuilder sb = new StringBuilder();
        Iterator<ITeam> teamIterator = this.teams.listIterator();
        while (teamIterator.hasNext()) {
            ITeam t = teamIterator.next();
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
        teams.forEach(ITeam::reset);
        this.playersAmount = null;
    }

    public List<ITeam> getWinningTeams() {
        List<ITeam> toret = new LinkedList<>();
        teams.sort(Comparator.reverseOrder());
        for (ITeam team : teams) {
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
            ITeam team = teams.get(i);
            sb.append(team.getTeamColor().getColor()).append(team.getNumAlivePlayers());
            if (i != teams.size() - 1) sb.append("&r vs ");
        }
        this.playersAmount = Utils.getColor(sb.toString());
    }

    public void joinSpectator(Player player) {
        ITeam currentTeam = getTeamByPlayer(player.getName());
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(Utils.getColor(AmazingTowers.getGameInstance(name).getConfig(ConfigType.MESSAGES)
                .getString("enterSpectatorMode").replace("%newLine%", "\n")));
        if (currentTeam != null)
            currentTeam.removePlayer(player.getName());
        Prefixes.setPrefix(player, ChatColor.GRAY.toString());
        player.closeInventory();
    }

    public void updatePrefixes() {
        teams.forEach(ITeam::updatePrefix);
    }
}