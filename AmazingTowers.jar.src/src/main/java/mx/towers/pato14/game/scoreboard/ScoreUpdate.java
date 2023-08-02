package mx.towers.pato14.game.scoreboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.StatType;
import mx.towers.pato14.utils.cofresillos.RefilleadoGalloConTenis;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.entity.Player;

public class ScoreUpdate {
    private final RefilleadoGalloConTenis refill;
    private final String title;
    private final String date;
    private final GameInstance gameInstance;

    public ScoreUpdate(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        this.refill = new RefilleadoGalloConTenis(gameInstance);
        this.title = gameInstance.getConfig(ConfigType.SCOREBOARD).getString("Scoreboard.name").replace("&", "§");
        this.date = (new SimpleDateFormat(gameInstance.getConfig(ConfigType.SCOREBOARD).getString("Scoreboard.formatDate"))).format(Calendar.getInstance().getTime());
    }

    public void createScoreboard(Player player) {
        ScoreHelper helper = ScoreHelper.createScore(player);
        helper.setTitle(this.title);
        getScores(helper, player);
    }

    public void updateScoreboardAll() {
        for (Player player : this.gameInstance.getGame().getPlayers()) {
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
        if (gameInstance.getGame().getGameState().equals(GameState.LOBBY)) {
            List<String> l = gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.lobby.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                        .replace("%max_players%", String.valueOf(gameInstance.getMaxPlayers()))
                        .replace("%date%", this.date));
                i--;
            }
        } else if (gameInstance.getGame().getGameState().equals(GameState.PREGAME)) {
            List<String> l = gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.pre-game.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                        .replace("%max_players%", String.valueOf(gameInstance.getMaxPlayers()))
                        .replace("%date%", this.date)
                        .replace("%seconds%", String.valueOf(gameInstance.getGame().getStart().getIntSeconds())));
                i--;
            }
        } else {
            int currentTeam = 0;
            List<Team> teams = gameInstance.getGame().getTeams().getTeams();
            List<String> l = teams.size() < 5 ? gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.gameFourTeamsOrLess.scores") :
                    gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.gameUpToEightTeams.scores");
            int i = 15;
            for (String st : l) {
                String text = AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                        .replace("%max_players%", String.valueOf(gameInstance.getMaxPlayers()))
                        .replace("%date%", this.date)
                        .replace("%maxPointsWin%", String.valueOf(gameInstance.getConfig(ConfigType.CONFIG).getInt("Options.Points")))
                        .replace("%player_kills%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.KILLS)))
                        .replace("%player_points%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.POINTS)))
                        .replace("%player_deaths%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.DEATHS)))
                        .replace("%refill_time%", convertirTimeXd((int) this.refill.getTimeRegeneration()));
                if (st.contains("%team_points%")) {
                    if (currentTeam < teams.size()) {
                        text = AmazingTowers.getColor(text
                                .replace("%team_color%", String.valueOf(teams.get(currentTeam).getTeamColor().getColor()))
                                .replace("%team_points%", String.valueOf(teams.get(currentTeam).getPoints()))
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
    }

    private String convertirTimeXd(int pTime) {
        return this.gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.refill_chests.enabled") ? ((this.refill.getTimeRegeneration() == 0.0F) ? AmazingTowers.getColor(this.gameInstance.getConfig(ConfigType.CONFIG).getString("Options.refill_chests.message_scoreboard")) : String.format("%02d:%02d", pTime / 60, pTime % 60)) : "00:00";
    }

    public RefilleadoGalloConTenis getRefill() {
        return this.refill;
    }
}


