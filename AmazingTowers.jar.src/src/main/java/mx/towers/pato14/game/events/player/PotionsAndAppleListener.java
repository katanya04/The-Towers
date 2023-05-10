package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PotionsAndAppleListener implements Listener {
    private final AmazingTowers plugin;
    public PotionsAndAppleListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        if (!this.plugin.getGameInstance(e.getPlayer()).getRules().get(Rule.POTS_AND_APPLE)
                && (e.getItem().getType().equals(Material.GOLDEN_APPLE) || e.getItem().getType().equals(Material.POTION)))
            e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerThrowPotion(PotionSplashEvent e) {
        if (!this.plugin.getGameInstance(e.getEntity()).getRules().get(Rule.POTS_AND_APPLE))
            e.setCancelled(true);
    }
    @EventHandler
    public void potionDispensedEvent(BlockDispenseEvent e){
        if (!this.plugin.getGameInstance(e.getBlock()).getRules().get(Rule.POTS_AND_APPLE) && e.getItem().getType().equals(Material.POTION))
            e.setCancelled(true);
    }
}
