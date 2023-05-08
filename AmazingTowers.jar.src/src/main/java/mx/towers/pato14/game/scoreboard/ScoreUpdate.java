package mx.towers.pato14.game.scoreboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.StatType;
import mx.towers.pato14.utils.cofresillos.RefilleadoGalloConTenis;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreUpdate {
    private final RefilleadoGalloConTenis refill;
    private final String title;
    private final String date;
    private final GameInstance game;

    public ScoreUpdate(GameInstance gameInstance) {
        this.game = gameInstance;
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
        for (Player player : Bukkit.getOnlinePlayers()) {
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
        if (GameState.isState(GameState.LOBBY)) {
            List<String> l = game.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.lobby.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()))
                        .replace("%date%", this.date));
                i--;
            }
        } else if (GameState.isState(GameState.PREGAME)) {
            List<String> l = game.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.pre-game.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()))
                        .replace("%date%", this.date)
                        .replace("%seconds%", String.valueOf(game.getGame().getStart().getIntSeconds())));
                i--;
            }
        } else {
            List<String> l = game.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.game.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()))
                        .replace("%date%", this.date)
                        .replace("%points_blue%", String.valueOf((game.getGame().getTeams().getTeam(TeamColor.BLUE)).getPoints()))
                        .replace("%points_red%", String.valueOf((game.getGame().getTeams().getTeam(TeamColor.RED)).getPoints()))
                        .replace("%maxPointsWin%", String.valueOf(game.getConfig(ConfigType.CONFIG).getInt("Options.Points")))
                        .replace("%player_kills%", String.valueOf(game.getGame().getStats().getStat(player.getName(), StatType.KILLS)))
                        .replace("%player_deaths%", String.valueOf(game.getGame().getStats().getStat(player.getName(), StatType.DEATHS)))
                        .replace("%refill_time%", convertirTimeXd((int) this.refill.getTimeRegeneration())));
                i--;
            }
        }
    }

    private String convertirTimeXd(int pTime) {
        return this.game.getConfig(ConfigType.CONFIG).getBoolean("Options.refill_chests.enabled") ? ((this.refill.getTimeRegeneration() == 0.0F) ? AmazingTowers.getColor(this.game.getConfig(ConfigType.CONFIG).getString("Options.refill_chests.message_scoreboard")) : String.format("%02d:%02d", pTime / 60, pTime % 60)) : "00:00";
    }

    public RefilleadoGalloConTenis getRefill() {
        return this.refill;
    }
}


