package mx.towers.pato14.game.scoreboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.entity.Player;

public class ScoreUpdate {
    private final String title;
    private final String date;
    private final TowersWorldInstance instance;

    public ScoreUpdate(TowersWorldInstance instance) {
        this.instance = instance;
        this.title = instance.getConfig(ConfigType.SCOREBOARD).getString("scoreboard.name").replace("&", "§");
        this.date = (new SimpleDateFormat(instance.getConfig(ConfigType.SCOREBOARD).getString("scoreboard.formatDate"))).format(Calendar.getInstance().getTime());
    }

    public void createScoreboard(Player player) {
        ScoreHelper helper = ScoreHelper.createScore(player);
        helper.setTitle(this.title);
        getScores(helper, player);
    }

    public void updateScoreboardAll() {
        for (Player player : this.instance.getWorld().getPlayers()) {
            updateScoreboard(player);
        }
    }

    public void updateScoreboard(Player player) {
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper helper = ScoreHelper.getByPlayer(player);
            getScores(helper, player);
        }
    }

    private void getScores(ScoreHelper helper, Player player) {
        if (this.instance instanceof GameInstance) {
            GameInstance gameInstance = (GameInstance) instance;
            if (gameInstance.getGame().getGameState().equals(GameState.LOBBY)) {
                List<String> l = gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.lobby.scores");
                int i = l.size();
                for (String st : l) {
                    helper.setSlot(i, AmazingTowers.getColor(st)
                            .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                            .replace("%date%", this.date));
                    i--;
                }
            } else if (gameInstance.getGame().getGameState().equals(GameState.PREGAME)) {
                List<String> l = gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.pre-game.scores");
                int i = l.size();
                for (String st : l) {
                    helper.setSlot(i, AmazingTowers.getColor(st)
                            .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                            .replace("%date%", this.date)
                            .replace("%seconds%", String.valueOf(gameInstance.getGame().getStart().getIntSeconds())));
                    i--;
                }
            } else {
                int currentTeam = 0;
                List<Team> teams = gameInstance.getGame().getTeams().getTeams();
                List<String> l = teams.size() < 5 ? gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.gameFourTeamsOrLess.scores") :
                        gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.gameUpToEightTeams.scores");
                int i = 15;
                for (String st : l) {
                    String text = AmazingTowers.getColor(st)
                            .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                            .replace("%date%", this.date)
                            .replace("%maxPointsWin%", String.valueOf(gameInstance.getConfig(ConfigType.CONFIG).getInt("options.pointsToWin")))
                            .replace("%player_kills%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.KILLS)))
                            .replace("%player_points%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.POINTS)))
                            .replace("%player_deaths%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.DEATHS)))
                            .replace("%refill_time%", Utils.intTimeToString(gameInstance.getGame().getRefill().getTimeRegeneration()));
                    if (st.contains("%team_points%")) {
                        if (currentTeam < teams.size()) {
                            String pointsText;
                            if (!gameInstance.getRules().get(Rule.BEDWARS_STYLE))
                                pointsText = String.valueOf(teams.get(currentTeam).getPoints());
                            else {
                                if (teams.get(currentTeam).respawnPlayers())
                                    pointsText = teams.get(currentTeam).getPoints() + " &4❤";
                                else {
                                    if (teams.get(currentTeam).getSizeOnlinePlayers() == 1)
                                        pointsText = teams.get(currentTeam).getSizeOnlinePlayers() + " " + gameInstance.getConfig(ConfigType.SCOREBOARD).getString("player");
                                    else
                                        pointsText = teams.get(currentTeam).getSizeOnlinePlayers() + " " + gameInstance.getConfig(ConfigType.SCOREBOARD).getString("players");
                                }
                            }
                            text = AmazingTowers.getColor(text
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
                    helper.setSlot(i, "");
                    i--;
                }
            }
        } else if (this.instance instanceof LobbyInstance) {
            List<String> l = instance.getConfig(ConfigType.SCOREBOARD).getStringList("scoreboard.lobby.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(instance.getNumPlayers() + AmazingTowers.getGameInstances().values().stream().map(TowersWorldInstance::getNumPlayers).reduce(0, Integer::sum)))
                        .replace("%date%", this.date));
                i--;
            }
        }
    }
}


