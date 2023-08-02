package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
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
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Team team = gameInstance.getGame().getTeams().getTeamByPlayerIncludingOffline(player.getName());
        gameInstance.addPlayer();
        gameInstance.getScoreUpdates().createScoreboard(player);
        gameInstance.getScoreUpdates().updateScoreboardAll();
        switch (gameInstance.getGame().getGameState()) {
            case LOBBY:
                if (gameInstance.getNumPlayers() >= gameInstance.getConfig(ConfigType.CONFIG).getInt("Options.gameStart.min-players")) {
                    gameInstance.getGame().setGameState(GameState.PREGAME);
                    gameInstance.getGame().getStart().gameStart();
                }
            case PREGAME:
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case GAME:
                if (team != null) {
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
                    .replace("{Team}", gameInstance.getGame().getTeams().getTeamByPlayer(player).getTeamColor().getName(gameInstance)).replaceAll("&", "§"));
        } else {
            e.setJoinMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.joinMessage").replaceAll("&", "§")
                    .replace("{Player}", player.getName()).replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                    .replace("%max_players%", String.valueOf(gameInstance.getMaxPlayers())));
        }
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.mysql.active"))
            this.plugin.con.CreateAccount(player.getName());
    }
}


