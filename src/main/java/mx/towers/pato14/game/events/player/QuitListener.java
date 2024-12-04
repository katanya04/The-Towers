package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        AmazingTowers.removePlayerWand(player);
        final String name = player.getName();
        final TowersWorldInstance instance = AmazingTowers.getInstance(player);
        if (instance instanceof GameInstance)
            e.setQuitMessage(instance.getConfig(ConfigType.MESSAGES).getString("quitMessage").replaceAll("&", "§")
                    .replace("{Player}", name));
        if (instance != null) {
            instance.leaveInstance(player);
        }
    }
}


