package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final String name = player.getName();
        final GameInstance gameInstance = plugin.getGameInstance(player);
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        e.setQuitMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("quitMessage").replaceAll("&", "§")
                .replace("{Player}", name));
        gameInstance.playerLeaveGame(player);
    }
}


