package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class WaterListener implements Listener {
    @EventHandler
    public void onWaterPlaced(PlayerBucketEmptyEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getRules().get(Rule.WATER))
            e.setCancelled(true);
    }
    @EventHandler
    public void liquidDispensedEvent(BlockDispenseEvent e){
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getBlock());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getRules().get(Rule.WATER) && e.getItem().getType().equals(Material.WATER_BUCKET))
            e.setCancelled(true);
    }
}
