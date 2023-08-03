package mx.towers.pato14.game.events.player;

import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.scoreboard.ScoreHelper;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class QuitListener implements Listener {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        final GameInstance gameInstance = plugin.getGameInstance(player);
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        final Team playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(player);
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper.removeScore(player);
        }
        this.plugin.getPermissions().remove(player.getName());
        gameInstance.removePlayer();

        e.setQuitMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.quitMessage").replaceAll("&", "§")
                .replace("{Player}", name));
        (new BukkitRunnable() {
            public void run() {
                gameInstance.getScoreUpdates().updateScoreboardAll();
                List<Team> teams = gameInstance.getGame().getTeams().getTeams();
                switch (gameInstance.getGame().getGameState()) {
                    case LOBBY:
                    case PREGAME:
                        if (playerTeam != null) {
                            playerTeam.removePlayer(player);
                            NametagEdit.getApi().clearNametag(player);
                        }
                        break;
                    case GAME:
                        if (playerTeam == null) {
                            break;
                        }
                        playerTeam.addOfflinePlayer(name);
                        playerTeam.removePlayer(player);
                        boolean makeATeamWin = true;
                        Team temp = null;
                        for (Team team : teams) {
                            if (team.getSizePlayers() > 0) {
                                if (temp == null)
                                    temp = team;
                                else
                                    makeATeamWin = false;
                            }
                        }
                        if (makeATeamWin) {
                            if (temp != null)
                                gameInstance.getGame().getFinish().Fatality(temp.getTeamColor());
                            else {
                                int numberOfTeams = teams.size();
                                int numero = (int) Math.floor(Math.random() * numberOfTeams);
                                gameInstance.getGame().getFinish().Fatality(TeamColor.values()[numero]);
                            }
                        }
                        break;
                }
            }
        }).runTaskLaterAsynchronously(this.plugin, 5L);
    }
}


