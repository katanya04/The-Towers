package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        GameInstance gameInstance = plugin.getGameInstance(player);
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        gameInstance.playerJoinGame(player);
        if (gameInstance.getGame().getTeams().containsTeamPlayer(player)) {
            e.setJoinMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("joinTeam")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", gameInstance.getGame().getTeams().getTeamByPlayer(player).getTeamColor().getColor())
                    .replace("{Team}", gameInstance.getGame().getTeams().getTeamByPlayer(player).getTeamColor().getName(gameInstance)).replaceAll("&", "§"));
        } else {
            e.setJoinMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("joinMessage").replaceAll("&", "§")
                    .replace("{Player}", player.getName()).replace("%online_players%", String.valueOf(gameInstance.getNumPlayers()))
                    .replace("%max_players%", String.valueOf(gameInstance.getMaxPlayers())));
        }
    }

    @EventHandler
    public void onTpToNewGame(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        GameInstance oldGameInstance = plugin.getGameInstance(e.getFrom());
        if (oldGameInstance != null && oldGameInstance.getGame() != null)
            oldGameInstance.playerLeaveGame(player);
        GameInstance newGameInstance = plugin.getGameInstance(player);
        if (newGameInstance == null || newGameInstance.getGame() == null)
            return;
        newGameInstance.playerJoinGame(player);
        if (newGameInstance.getGame().getTeams().containsTeamPlayer(player)) {
            newGameInstance.broadcastMessage(newGameInstance.getConfig(ConfigType.MESSAGES).getString("joinTeam")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", newGameInstance.getGame().getTeams().getTeamByPlayer(player).getTeamColor().getColor())
                    .replace("{Team}", newGameInstance.getGame().getTeams().getTeamByPlayer(player).getTeamColor().getName(newGameInstance)), true);
        } else {
            newGameInstance.broadcastMessage(newGameInstance.getConfig(ConfigType.MESSAGES).getString("joinMessage")
                    .replace("{Player}", player.getName()).replace("%online_players%", String.valueOf(newGameInstance.getNumPlayers()))
                    .replace("%max_players%", String.valueOf(newGameInstance.getMaxPlayers())), true);
        }
    }
}


