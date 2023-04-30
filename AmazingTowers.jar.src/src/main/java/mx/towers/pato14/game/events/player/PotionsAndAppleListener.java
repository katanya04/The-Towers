package mx.towers.pato14.game.events.player;

import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PotionsAndAppleListener implements Listener {
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        if (!Rule.POTS_AND_APPLE.getCurrentState() && (e.getItem().getType().equals(Material.GOLDEN_APPLE) ||
                e.getItem().getType().equals(Material.POTION)))
            e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerThrowPotion(PotionSplashEvent e) {
        if (!Rule.POTS_AND_APPLE.getCurrentState())
            e.setCancelled(true);
    }
    @EventHandler
    public void potionDispensedEvent(BlockDispenseEvent e){
        if (!Rule.POTS_AND_APPLE.getCurrentState() && e.getItem().getType().equals(Material.POTION))
            e.setCancelled(true);
    }
}
