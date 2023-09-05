package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
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
            GameInstance gameInstance = AmazingTowers.getGameInstance(player);
            if (gameInstance != null)
                gameInstance.addPlayer();
            Utils.clearNameTagPlayer(player);
            Dar.joinMainLobby(player);
            if (lobby.getWorld().equals(player.getWorld()))
                lobby.playerJoinGame(player);
            Utils.tpToWorld(lobby.getWorld(), player);
            Utils.updatePlayerTab(player);
        }
    }

    @EventHandler
    public void onTpToNewGame(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        Utils.clearNameTagPlayer(player);
        TowersWorldInstance oldInstance = AmazingTowers.getInstance(e.getFrom());
        if (oldInstance != null)
            oldInstance.playerLeaveGame(player);
        TowersWorldInstance newInstance = AmazingTowers.getInstance(player);
        if (newInstance == null)
            return;
        newInstance.playerJoinGame(player);
        if (newInstance instanceof GameInstance)
            newInstance.broadcastMessage(getMessage((GameInstance) newInstance, player.getName()), true);
        Utils.updatePlayerTab(player);
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


