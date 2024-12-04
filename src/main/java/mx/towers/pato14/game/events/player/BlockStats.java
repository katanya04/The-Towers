package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.stats.StatType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockStats implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlaced(BlockPlaceEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!e.isCancelled())
            gameInstance.getGame().getStats().increaseOne(e.getPlayer().getName(), StatType.BLOCKS_PLACED);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!e.isCancelled())
            gameInstance.getGame().getStats().increaseOne(e.getPlayer().getName(), StatType.BLOCKS_BROKEN);
    }
}
