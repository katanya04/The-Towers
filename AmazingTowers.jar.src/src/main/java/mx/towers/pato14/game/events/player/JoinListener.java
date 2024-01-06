package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final LobbyInstance lobby = AmazingTowers.getLobby();
        if (lobby != null) {
            e.setJoinMessage(Utils.getColor(lobby.getConfig(ConfigType.MESSAGES).getString("playerJoinedLobby")
                    .replace("{player}", player.getName())));
            Utils.tpToWorld(lobby.getWorld(), player);
        } else {
            final GameInstance gameInstance = AmazingTowers.getGameInstance(player);
            if (gameInstance != null)
                gameInstance.joinInstance(player);
        }
    }
}


