package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import mx.towers.pato14.utils.stats.StatType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.stream.Collectors;


public class GameTeams {
    private final String name;
    private String playersAmount;
    private final List<ITeam> teams;

    public GameTeams(GameInstance gameInstance) {
        this.name = gameInstance.getInternalName();
        this.teams = new LinkedList<>();
        this.playersAmount = null;
        for (TeamColor teamColor : TeamColor.getMatchTeams(gameInstance.getNumberOfTeams())) {
            this.teams.add(new Team(teamColor, Utils.getColor(teamColor.getColor()) + gameInstance.getConfig(ConfigType.CONFIG)
                    .getString("teams.prefixes." + teamColor.name().toLowerCase()) + " ", this,
                    Utils.getConfIntDefaultsIfNull(gameInstance.getConfig(ConfigType.GAME_SETTINGS), "points.livesBedwarsMode")));
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
        for (ITeam team : teams)
            if (Objects.equals(team.getTeamColor(), teamColor))
                return team;
        return null;
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
            sb.append(t.getTeamColor().getColor()).append("&l");
            if (getGame().getGameInstance().getRules().get(Rule.BEDWARS_STYLE))
                sb.append(t.getLives());
            else
                sb.append(t.getPoints());
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
        Prefixes.setPrefix(player.getName(), ChatColor.GRAY.toString());
        player.closeInventory();
    }

    public void updatePrefixes() {
        teams.forEach(ITeam::updatePrefix);
    }

    public void win(TeamColor team) {
        GameInstance gameInstance = getGame().getGameInstance();
        gameInstance.getGame().getFinish().fatality(team);
        gameInstance.getGame().setGameState(GameState.FINISH);
    }

    public void loseRespawn(TeamColor team) {
        GameInstance gameInstance = getGame().getGameInstance();
        String title = Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("scorePoint.title.noRespawnTitle"));
        for (Player pl : this.getTeam(team).getOnlinePlayers()) {
            pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 1.f);
            if (gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("scorePoint.title.enabled"))
                ReflectionMethods.sendTitle(pl, title, "", 0, 50, 20);
            else
                pl.sendMessage(title);
        }
    }

    public Set<ITeam> checkWin() {
        return this.teams.stream().filter(o -> this.checkWin(o.getTeamColor())).collect(Collectors.toSet());
    }

    public boolean checkWin(TeamColor team) {
        int pointsToWin = Utils.getConfIntDefaultsIfNull(this.getGame().getGameInstance().getConfig(ConfigType.GAME_SETTINGS), "points.pointsToWin");
        return this.getTeam(team).getPoints() >= pointsToWin ||
                this.teams.stream().filter(o -> !o.isEliminated()).count() == 1;
    }

    public boolean checkNoLives(TeamColor team) {
        return this.getTeam(team).getLives() <= 0;
    }

    public void scorePoint(Player player, ITeam teamScoredOn) {
        GameInstance gameInstance = getGame().getGameInstance();
        boolean bedwarsStyle = gameInstance.getRules().get(Rule.BEDWARS_STYLE);
        ITeam team = gameInstance.getGame().getTeams().getTeamByPlayer(player.getName());
        player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.SPAWN.getPath(team.getTeamColor()))), PlayerTeleportEvent.TeleportCause.COMMAND);
        gameInstance.getScoreUpdates().updateScoreboardAll(false, gameInstance.getWorld().getPlayers());
        getGame().getStats().addOne(player.getName(), StatType.POINTS);
        gameInstance.getVault().giveReward(player, RewardsEnum.POINT);
        gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString(bedwarsStyle ? "scorePoint.pointBedwarsStyle" : "scorePoint.point")
                        .replace("{Player}", player.getName())
                        .replace("{Color}", team.getTeamColor().getColor())
                        .replace("{Team}", team.getTeamColor().getName(gameInstance))
                        .replace("{ColorTeamScored}", teamScoredOn.getTeamColor().getColor())
                        .replace("{TeamScored}", teamScoredOn.getTeamColor().getName(gameInstance)),
                true);
        if (!bedwarsStyle) {
            team.scorePoint();
            if (this.checkWin(team.getTeamColor()) || gameInstance.getGame().getGameState() == GameState.EXTRA_TIME)
                this.win(team.getTeamColor());
        } else {
            teamScoredOn.gotScored();
            if (checkNoLives(teamScoredOn.getTeamColor()))
                this.loseRespawn(teamScoredOn.getTeamColor());
        }
        for (Player p : getGame().getPlayers()) {
            if (team.containsPlayer(p.getName())) {
                p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 2.0f);
            } else if (!bedwarsStyle || (teamScoredOn.containsPlayer(p.getName()) && teamScoredOn.getLives() > 0)) {
                p.playSound(p.getLocation(), Sound.GHAST_SCREAM2, 1.0f, 1.1f);
            } else if (teamScoredOn.containsPlayer(p.getName()) && teamScoredOn.getLives() <= 0) {
                p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1.0f, 1.0f);
            }
        }
    }
}