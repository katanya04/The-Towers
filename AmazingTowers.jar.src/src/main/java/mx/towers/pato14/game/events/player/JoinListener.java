package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final AmazingTowers p = AmazingTowers.getPlugin();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        this.p.getGameInstance(player).getUpdates().createScoreboard(player);
        this.p.getGameInstance(player).getUpdates().updateScoreboardAll();
        switch (GameState.getState()) {
            case LOBBY:
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                if (Bukkit.getOnlinePlayers().size() >= this.p.getGameInstance(player).getConfig(ConfigType.CONFIG).getInt("Options.gameStart.min-players")) {
                    GameState.setState(GameState.PREGAME);
                    this.p.getGameInstance(player).getGame().getStart().gameStart();
                }
                break;
            case PREGAME:
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case GAME:
                if (this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.BLUE).containsOffline(player.getName())) {
                    this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.BLUE).removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    break;
                }
                if (this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.RED).containsOffline(player.getName())) {
                    this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.RED).removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    break;
                }
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case FINISH:
                break;
            default:
                if (this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.BLUE).containsOffline(player.getName())) {
                    this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.BLUE).removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    player.setGameMode(GameMode.SPECTATOR);
                    break;
                }
                if (this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.RED).containsOffline(player.getName())) {
                    this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.RED).removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    player.setGameMode(GameMode.SPECTATOR);
                    break;
                }
                Dar.DarItemsJoin(player, GameMode.SPECTATOR);
                break;
        }
        if (this.p.getGameInstance(player).getGame().getTeams().containsTeamPlayer(player)) {
            e.setJoinMessage(this.p.getGameInstance(player).getGame().getTeams().getTeam(TeamColor.BLUE).containsPlayer(player.getName()) ? this.p.getGameInstance(player).getConfig(ConfigType.MESSAGES).getString("messages.joinBlueTeam").replaceAll("&", "§")
                    .replace("{Player}", player.getName()) : this.p.getGameInstance(player).getConfig(ConfigType.MESSAGES).getString("messages.joinRedTeam").replaceAll("&", "§")
                    .replace("{Player}", player.getName()));
        } else {
            e.setJoinMessage(this.p.getGameInstance(player).getConfig(ConfigType.MESSAGES).getString("messages.joinMessage").replaceAll("&", "§")
                    .replace("{Player}", player.getName()).replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers())));
        }
        if (this.p.getGameInstance(player).getConfig(ConfigType.CONFIG).getBoolean("Options.mysql.active"))
            this.p.con.CreateAcount(player.getName());
    }
}


