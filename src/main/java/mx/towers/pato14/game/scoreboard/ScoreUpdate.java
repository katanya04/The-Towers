package mx.towers.pato14.game.scoreboard;

import java.text.SimpleDateFormat;
import java.util.*;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreUpdate {
    private final String title;
    private final String date;

    public ScoreUpdate(TowersWorldInstance instance) {
        this.title = instance.getConfig(ConfigType.SCOREBOARD).getString("scoreboard.name").replace("&", "§");
        this.date = (new SimpleDateFormat(instance.getConfig(ConfigType.SCOREBOARD).getString("scoreboard.formatDate"))).format(Calendar.getInstance().getTime());
    }

    public void createScoreboard(Player player, boolean setScore) {
        if (ScoreHelper.hasScore(player))
            return;
        ScoreHelper helper = ScoreHelper.createScore(player);
        helper.setTitle(this.title);
        if (setScore)
            setScores(helper, player);
    }

    public void updateScoreboardAll(boolean runTaskLater, Collection<Player> players) {
        if (runTaskLater) {
            (new BukkitRunnable() {
                @Override
                public void run() {
                    updateScoreboardAll(false, players);
                }
            }).runTaskLater(AmazingTowers.getPlugin(), 1L);
        } else {
            for (Player player : players)
                updateScoreboard(player);
        }
    }

    public void updateScoreboard(Player player) {
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper helper = ScoreHelper.getByPlayer(player);
            setScores(helper, player);
        }
    }

    private void setScores(ScoreHelper helper, Player player) {
        TowersWorldInstance towersWorldInstance = AmazingTowers.getInstance(player.getWorld());
        if (towersWorldInstance instanceof GameInstance) {
            GameInstance gameInstance = (GameInstance) towersWorldInstance;
            if (gameInstance.getGame() == null)
                return;
            if (gameInstance.getGame().getGameState().equals(GameState.LOBBY) || gameInstance.getGame().getGameState().equals(GameState.CAPTAINS_CHOOSE)) {
                List<String> l = gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.lobby.scores");
                int i = l.size();
                for (String st : l) {
                    helper.setSlot(i, Utils.getColor(st)
                            .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                            .replace("%date%", this.date)
                            .replace("%instance_name%", gameInstance.getConfig(ConfigType.CONFIG).getString("name", "The Towers")));
                    i--;
                }
            } else if (gameInstance.getGame().getGameState().equals(GameState.PREGAME)) {
                List<String> l = gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.pre-game.scores");
                int i = l.size();
                for (String st : l) {
                    helper.setSlot(i, Utils.getColor(st)
                            .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                            .replace("%date%", this.date)
                            .replace("%seconds%", String.valueOf(gameInstance.getGame().getStart().getIntSeconds()))
                            .replace("%instance_name%", gameInstance.getConfig(ConfigType.CONFIG).getString("name", "The Towers")));
                    i--;
                }
            } else {
                int currentTeam = 0;
                List<ITeam> teams = gameInstance.getGame().getTeams().getTeams();
                List<String> l = teams.size() < 5 ? gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.gameFourTeamsOrLess.scores") :
                        gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.gameUpToEightTeams.scores");
                int i = 15 - (teams.size() < 5 ? 4 - teams.size() : 8 - teams.size());
                for (String st : l) {
                    String text = Utils.getColor(st)
                            .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                            .replace("%date%", this.date)
                            .replace("%maxPointsWin%", gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("points.pointsToWin"))
                            .replace("%player_kills%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.KILLS)))
                            .replace("%player_points%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.POINTS)))
                            .replace("%player_deaths%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.DEATHS)))
                            .replace("%refill_time%", Utils.intTimeToString(gameInstance.getGame().getRefill().getTimeRegeneration()))
                            .replace("%instance_name%", gameInstance.getConfig(ConfigType.CONFIG).getString("name", "The Towers"))
                            .replace("%players_amount%", gameInstance.getGame().getTeams().getPlayersAmount());
                    if (st.contains("%team_points%")) {
                        if (currentTeam < teams.size()) {
                            String pointsText;
                            if (!gameInstance.getRules().get(Rule.BEDWARS_STYLE))
                                pointsText = String.valueOf(teams.get(currentTeam).getPoints());
                            else {
                                if (teams.get(currentTeam).doPlayersRespawn())
                                    pointsText = teams.get(currentTeam).getLives() + " &4❤";
                                else {
                                    if (teams.get(currentTeam).getNumAlivePlayers() == 1)
                                        pointsText = teams.get(currentTeam).getNumAlivePlayers() + " " + gameInstance.getConfig(ConfigType.SCOREBOARD).getString("player");
                                    else
                                        pointsText = teams.get(currentTeam).getNumAlivePlayers() + " " + gameInstance.getConfig(ConfigType.SCOREBOARD).getString("players");
                                }
                            }
                            text = Utils.getColor(text
                                    .replace("%team_color%", String.valueOf(teams.get(currentTeam).getTeamColor().getColor()))
                                    .replace("%team_points%", pointsText)
                                    .replace("%first_letter%", String.valueOf(teams.get(currentTeam).getTeamColor().name().charAt(0)))
                                    .replace("%team_name%", String.valueOf(teams.get(currentTeam).getTeamColor().getNameFirstCapitalized(gameInstance))));
                            currentTeam++;
                        } else
                            continue;
                    }
                    helper.setSlot(i, text);
                    i--;
                }
                while (i > 0) {
                    helper.removeSlot(i);
                    i--;
                }
            }
        } else if (towersWorldInstance instanceof LobbyInstance) {
            List<String> l = towersWorldInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.lobby.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, Utils.getColor(st)
                        .replace("%online_players%", String.valueOf(Arrays.stream(AmazingTowers.getGameInstances()).filter(o -> o.getGame() != null)
                                .map(TowersWorldInstance::getNumPlayers).reduce(towersWorldInstance.getNumPlayers(), Integer::sum)))
                        .replace("%date%", this.date));
                i--;
            }
        }
    }
}


