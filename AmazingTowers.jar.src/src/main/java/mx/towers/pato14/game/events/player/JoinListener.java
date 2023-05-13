package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
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
    private final AmazingTowers plugin = AmazingTowers.getPlugin();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        GameInstance gameInstance = plugin.getGameInstance(player);
        Team team = gameInstance.getGame().getTeams().getTeamByPlayer(player);
        gameInstance.getUpdates().createScoreboard(player);
        gameInstance.getUpdates().updateScoreboardAll();
        gameInstance.addPlayer();
        switch (GameState.getState()) {
            case LOBBY:
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                if (Bukkit.getOnlinePlayers().size() >= gameInstance.getConfig(ConfigType.CONFIG).getInt("Options.gameStart.min-players")) {
                    GameState.setState(GameState.PREGAME);
                    gameInstance.getGame().getStart().gameStart();
                }
                break;
            case PREGAME:
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case GAME:
                if (team != null && team.containsOffline(player.getName())) {
                    team.removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    break;
                }
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case FINISH:
                break;
            default:
        }
        if (gameInstance.getGame().getTeams().containsTeamPlayer(player)) {
            e.setJoinMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.joinTeam")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", gameInstance.getGame().getTeams().getTeamByPlayer(player).getTeamColor().getColor())
                    .replace("{Team}", gameInstance.getGame().getTeams().getTeamByPlayer(player).getTeamColor().getName()).replaceAll("&", "§"));
        } else {
            e.setJoinMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.joinMessage").replaceAll("&", "§")
                    .replace("{Player}", player.getName()).replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                    .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers() - Bukkit.getOnlinePlayers().size() + gameInstance.getNumPlayers())));
        }
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.mysql.active"))
            this.plugin.con.CreateAcount(player.getName());
    }
}


