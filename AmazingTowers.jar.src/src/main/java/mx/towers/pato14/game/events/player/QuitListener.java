package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        Utils.clearNameTagPlayer(player);
        final String name = player.getName();
        final TowersWorldInstance instance = AmazingTowers.getInstance(player);
        LobbyInstance lobby = AmazingTowers.getLobby();
        if (instance == null)
            return;
        if (instance instanceof LobbyInstance && lobby != null) {
            AmazingTowers.getLobby().playerLeaveGame(player);
        } else if (instance instanceof GameInstance) {
            e.setQuitMessage(instance.getConfig(ConfigType.MESSAGES).getString("quitMessage").replaceAll("&", "§")
                    .replace("{Player}", name));
            instance.playerLeaveGame(player);
        }
    }
}


