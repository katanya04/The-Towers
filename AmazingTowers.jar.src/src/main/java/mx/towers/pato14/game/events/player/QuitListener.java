package mx.towers.pato14.game.events.player;

import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.scoreboard.ScoreHelper;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Team;
import mx.towers.pato14.utils.plugin.PluginA;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class QuitListener implements Listener {
    private final AmazingTowers a = AmazingTowers.getPlugin();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper.removeScore(player);
        }
        this.a.getPermissions().remove(player.getName());
        /*if (this.a.getGame().getStats().hasKillsPlayer(player.getName())) {
            this.a.getGame().getStats().getKillsPlayer().remove(player);
        }
        if (this.a.getGame().getStats().hasDeathPlayer(player.getName())) {
            this.a.getGame().getStats().getDeathsPlayer().remove(player);
        }*/
        e.setQuitMessage(this.a.getMessages().getString("messages.quitMessage").replaceAll("&", "§")
                .replace("{Player}", player.getName()));
        (new BukkitRunnable() {
            public void run() {
                int teamBlue, teamRed;
                QuitListener.this.a.getUpdates().updateScoreboardAll();
                switch (GameState.getState()) {
                    case LOBBY:
                    case PREGAME:
                        if (QuitListener.this.a.getGame().getTeams().containsTeamPlayer(name)) {
                            QuitListener.this.a.getGame().getTeams().removePlayer(player);
                            NametagEdit.getApi().clearNametag(player);
                        }
                        break;
                    case GAME:
                        if (!QuitListener.this.a.getGame().getTeams().containsTeamPlayer(player)) {
                            break;
                        }
                        if (QuitListener.this.a.getGame().getTeams().getBlue().containsPlayer(name)) {
                            QuitListener.this.a.getGame().getTeams().getBlue().addOfflinePlayer(name);
                            QuitListener.this.a.getGame().getTeams().getBlue().removePlayer(player);
                        } else if (QuitListener.this.a.getGame().getTeams().getRed().containsPlayer(name)) {
                            QuitListener.this.a.getGame().getTeams().getRed().addOfflinePlayer(name);
                            QuitListener.this.a.getGame().getTeams().getRed().removePlayer(player);
                        }
                        teamBlue = QuitListener.this.a.getGame().getTeams().getBlue().getSizePlayers();
                        teamRed = QuitListener.this.a.getGame().getTeams().getRed().getSizePlayers();
                        if (teamBlue == 0 && teamRed > 0) {
                            QuitListener.this.a.getGame().getFinish().Fatality(Team.RED);
                            break;
                        }
                        if (teamRed == 0 && teamBlue > 0) {
                            QuitListener.this.a.getGame().getFinish().Fatality(Team.BLUE);
                            break;
                        }
                        if (teamRed == 0 && teamBlue == 0) {
                            int numero = (int) (Math.random() * 10.0D) + 1;
                            if (numero <= 5) {
                                QuitListener.this.a.getGame().getFinish().Fatality(Team.RED);
                            } else {
                                QuitListener.this.a.getGame().getFinish().Fatality(Team.BLUE);
                            }
                            System.out.println(numero);
                        }
                        break;
                }
            }
        }).runTaskLaterAsynchronously((Plugin) this.a, 5L);
    }
}


