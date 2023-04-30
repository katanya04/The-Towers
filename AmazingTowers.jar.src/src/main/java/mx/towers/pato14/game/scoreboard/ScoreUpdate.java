package mx.towers.pato14.game.scoreboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.StatType;
import mx.towers.pato14.utils.cofresillos.RefilleadoGalloConTenis;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreUpdate {
    private AmazingTowers plugin;
    private RefilleadoGalloConTenis refill;
    private String title;
    private final String date;

    public ScoreUpdate(AmazingTowers plugin) {
        this.plugin = plugin;
        this.refill = new RefilleadoGalloConTenis(this.plugin);
        this.title = this.plugin.getScoreboard().getString("Scoreboard.name").replace("&", "§");
        this.date = (new SimpleDateFormat(this.plugin.getScoreboard().getString("Scoreboard.formatDate"))).format(Calendar.getInstance().getTime());
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
            List<String> l = this.plugin.getScoreboard().getStringList("Scoreboard.lobby.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(Integer.valueOf(i).intValue(), this.plugin.getColor(st)
                        .replace("%online_players%", (new StringBuilder(String.valueOf(Bukkit.getOnlinePlayers().size()))).toString())
                        .replace("%max_players%", (new StringBuilder(String.valueOf(Bukkit.getMaxPlayers()))).toString())
                        .replace("%date%", this.date));
                i--;
            }
        } else if (GameState.isState(GameState.PREGAME)) {
            List<String> l = this.plugin.getScoreboard().getStringList("Scoreboard.pre-game.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(Integer.valueOf(i).intValue(), this.plugin.getColor(st)
                        .replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()))
                        .replace("%date%", this.date)
                        .replace("%seconds%", String.valueOf(this.plugin.getGame().getStart().getIntSeconds())));
                i--;
            }
        } else {
            List<String> l = this.plugin.getScoreboard().getStringList("Scoreboard.game.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(Integer.valueOf(i).intValue(), this.plugin.getColor(st)
                        .replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()))
                        .replace("%date%", this.date)
                        .replace("%points_blue%", String.valueOf((this.plugin.getGame().getTeams()).bluePoints))
                        .replace("%points_red%", String.valueOf((this.plugin.getGame().getTeams()).redPoints))
                        .replace("%maxPointsWin%", String.valueOf(this.plugin.getConfig().getInt("Options.Points")))
                        .replace("%player_kills%", String.valueOf(this.plugin.getGame().getStats().getStat(player.getName(), StatType.KILLS)))
                        .replace("%player_deaths%", String.valueOf(this.plugin.getGame().getStats().getStat(player.getName(), StatType.DEATHS)))
                        .replace("%refill_time%", convertirTimeXd((int) this.refill.getTimeRegeneration())));
                i--;
            }
        }
    }

    private String convertirTimeXd(int pTime) {
        return this.plugin.getConfig().getBoolean("Options.refill_chests.enabled") ? ((this.refill.getTimeRegeneration() == 0.0F) ? this.plugin.getColor(this.plugin.getConfig().getString("Options.refill_chests.message_scoreboard")) : String.format("%02d:%02d", new Object[]{Integer.valueOf(pTime / 60), Integer.valueOf(pTime % 60)})) : "00:00";
    }

    public RefilleadoGalloConTenis getRefill() {
        return this.refill;
    }
}


