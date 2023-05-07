package mx.towers.pato14.game.events.player;

import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.scoreboard.ScoreHelper;
import mx.towers.pato14.game.team.TeamGame;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class QuitListener implements Listener {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper.removeScore(player);
        }
        this.plugin.getPermissions().remove(player.getName());

        e.setQuitMessage(this.plugin.getGameInstance(player).getConfig(ConfigType.MESSAGES).getString("messages.quitMessage").replaceAll("&", "§")
                .replace("{Player}", player.getName()));
        (new BukkitRunnable() {
            public void run() {
                int teamBlue, teamRed;
                QuitListener.this.plugin.getGameInstance(player).getUpdates().updateScoreboardAll();
                TeamGame teams = QuitListener.this.plugin.getGameInstance(player).getGame().getTeams();
                switch (GameState.getState()) {
                    case LOBBY:
                    case PREGAME:
                        if (teams.containsTeamPlayer(name)) {
                            teams.removePlayer(player);
                            NametagEdit.getApi().clearNametag(player);
                        }
                        break;
                    case GAME:
                        if (!teams.containsTeamPlayer(player)) {
                            break;
                        }
                        if (teams.getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(name)) {
                            teams.getTeam(mx.towers.pato14.utils.enums.Team.BLUE).addOfflinePlayer(name);
                            teams.getTeam(mx.towers.pato14.utils.enums.Team.BLUE).removePlayer(player);
                        } else if (teams.getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(name)) {
                            teams.getTeam(mx.towers.pato14.utils.enums.Team.RED).addOfflinePlayer(name);
                            teams.getTeam(mx.towers.pato14.utils.enums.Team.RED).removePlayer(player);
                        }
                        teamBlue = teams.getTeam(mx.towers.pato14.utils.enums.Team.BLUE).getSizePlayers();
                        teamRed = teams.getTeam(mx.towers.pato14.utils.enums.Team.RED).getSizePlayers();
                        if (teamBlue == 0 && teamRed > 0) {
                            QuitListener.this.plugin.getGameInstance(player).getGame().getFinish().Fatality(Team.RED);
                            break;
                        }
                        if (teamRed == 0 && teamBlue > 0) {
                            QuitListener.this.plugin.getGameInstance(player).getGame().getFinish().Fatality(Team.BLUE);
                            break;
                        }
                        if (teamRed == 0 && teamBlue == 0) {
                            int numero = (int) (Math.random() * 10.0D) + 1;
                            if (numero <= 5) {
                                QuitListener.this.plugin.getGameInstance(player).getGame().getFinish().Fatality(Team.RED);
                            } else {
                                QuitListener.this.plugin.getGameInstance(player).getGame().getFinish().Fatality(Team.BLUE);
                            }
                            System.out.println(numero);
                        }
                        break;
                }
            }
        }).runTaskLaterAsynchronously(this.plugin, 5L);
    }
}


