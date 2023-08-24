package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.game.tasks.Dar;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        LobbyInstance lobby = AmazingTowers.getLobby();
        if (lobby != null) {
            Player player = e.getPlayer();
            Dar.joinMainLobby(player);
            Utils.tpToWorld(lobby.getWorld(), player);
            lobby.playerJoinGame(player);
        }
    }

    @EventHandler
    public void onTpToNewGame(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        GameInstance oldGameInstance = AmazingTowers.getGameInstance(e.getFrom());
        if (oldGameInstance != null && oldGameInstance.getGame() != null)
            oldGameInstance.playerLeaveGame(player);
        GameInstance newGameInstance = AmazingTowers.getGameInstance(player);
        if (newGameInstance == null || newGameInstance.getGame() == null)
            return;
        newGameInstance.playerJoinGame(player);
        newGameInstance.broadcastMessage(getMessage(newGameInstance, player.getName()), true);
    }

    private String getMessage(GameInstance gameInstance, String playerName) {
        Team team = gameInstance.getGame().getTeams().getTeamByPlayer(playerName);
        if (team != null) {
            return gameInstance.getConfig(ConfigType.MESSAGES).getString("joinTeam")
                    .replace("{Player}", playerName)
                    .replace("{Color}", team.getTeamColor().getColor())
                    .replace("{Team}", team.getTeamColor().getName(gameInstance));
        } else {
            return gameInstance.getConfig(ConfigType.MESSAGES).getString("joinMessage")
                    .replace("{Player}", playerName).replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()));
        }
    }
}


