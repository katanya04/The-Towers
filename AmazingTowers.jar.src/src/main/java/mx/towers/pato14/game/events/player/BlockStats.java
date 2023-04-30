package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.StatType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockStats implements Listener {
    private AmazingTowers plugin;
    public BlockStats(AmazingTowers plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlaced(BlockPlaceEvent e) {
        if (!e.isCancelled())
            this.plugin.getGame().getStats().addOne(e.getPlayer().getName(), StatType.BLOCKS_PLACED);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.isCancelled())
            this.plugin.getGame().getStats().addOne(e.getPlayer().getName(), StatType.BLOCKS_BROKEN);
    }
}
