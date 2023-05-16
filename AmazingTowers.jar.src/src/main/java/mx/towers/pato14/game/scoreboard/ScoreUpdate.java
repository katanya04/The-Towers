package mx.towers.pato14.game.scoreboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
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
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    public void updateScoreboardGame(Game game) {
        for (Player player : game.getPlayers()) {
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
                        .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers() - Bukkit.getOnlinePlayers().size() + gameInstance.getNumPlayers()))
                        .replace("%date%", this.date));
                i--;
            }
        } else if (gameInstance.getGame().getGameState().equals(GameState.PREGAME)) {
            List<String> l = gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.pre-game.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                        .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers() - Bukkit.getOnlinePlayers().size() + gameInstance.getNumPlayers()))
                        .replace("%date%", this.date)
                        .replace("%seconds%", String.valueOf(gameInstance.getGame().getStart().getIntSeconds())));
                i--;
            }
        } else {
            List<String> l = gameInstance.getConfig(ConfigType.SCOREBOARD).getStringList("Scoreboard.game.scores");
            int i = l.size();
            for (String st : l) {
                helper.setSlot(i, AmazingTowers.getColor(st)
                        .replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                        .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers() - Bukkit.getOnlinePlayers().size() + gameInstance.getNumPlayers()))
                        .replace("%date%", this.date)
                        .replace("%points_blue%", String.valueOf((gameInstance.getGame().getTeams().getTeam(TeamColor.BLUE)).getPoints()))
                        .replace("%points_red%", String.valueOf((gameInstance.getGame().getTeams().getTeam(TeamColor.RED)).getPoints()))
                        .replace("%maxPointsWin%", String.valueOf(gameInstance.getConfig(ConfigType.CONFIG).getInt("Options.Points")))
                        .replace("%player_kills%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.KILLS)))
                        .replace("%player_deaths%", String.valueOf(gameInstance.getGame().getStats().getStat(player.getName(), StatType.DEATHS)))
                        .replace("%refill_time%", convertirTimeXd((int) this.refill.getTimeRegeneration())));
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


