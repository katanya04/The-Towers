package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.plugin.PluginA;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JoinListener implements Listener {
    private final AmazingTowers p = AmazingTowers.getPlugin();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        this.p.getUpdates().createScoreboard(player);
        this.p.getUpdates().updateScoreboardAll();
        switch (GameState.getState()) {
            case LOBBY:
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                if (Bukkit.getOnlinePlayers().size() >= this.p.getConfig().getInt("Options.gameStart.min-players")) {
                    GameState.setState(GameState.PREGAME);
                    this.p.getGame().getStart().gameStart();
                }
                break;
            case PREGAME:
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case GAME:
                if (this.p.getGame().getTeams().getBlue().containsOffline(player.getName())) {
                    this.p.getGame().getTeams().getBlue().removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    break;
                }
                if (this.p.getGame().getTeams().getRed().containsOffline(player.getName())) {
                    this.p.getGame().getTeams().getRed().removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    break;
                }
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case FINISH:
                break;
            default:
                if (this.p.getGame().getTeams().getBlue().containsOffline(player.getName())) {
                    this.p.getGame().getTeams().getBlue().removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    player.setGameMode(GameMode.SPECTATOR);
                    break;
                }
                if (this.p.getGame().getTeams().getRed().containsOffline(player.getName())) {
                    this.p.getGame().getTeams().getRed().removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    player.setGameMode(GameMode.SPECTATOR);
                    break;
                }
                Dar.DarItemsJoin(player, GameMode.SPECTATOR);
                break;
        }
        if (this.p.getGame().getTeams().containsTeamPlayer(player)) {
            e.setJoinMessage(this.p.getGame().getTeams().getBlue().containsPlayer(player.getName()) ? this.p.getMessages().getString("messages.joinBlueTeam").replaceAll("&", "§")
                    .replace("{Player}", player.getName()) : this.p.getMessages().getString("messages.joinRedTeam").replaceAll("&", "§")
                    .replace("{Player}", player.getName()));
        } else {
            e.setJoinMessage(this.p.getMessages().getString("messages.joinMessage").replaceAll("&", "§")
                    .replace("{Player}", player.getName()).replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers())));
        }
        if (this.p.getConfig().getBoolean("Options.mysql.active"))
            this.p.con.CreateAcount(player.getName());
    }
}


