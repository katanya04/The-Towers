package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

public class AntiFallingSandTrollListener implements Listener {
    private final AmazingTowers plugin;

    public AntiFallingSandTrollListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFallingSand(EntityChangeBlockEvent e) {
        if (e.getEntityType().equals(EntityType.FALLING_BLOCK)) {
            FallingBlock flbl = (FallingBlock) e.getEntity();
            GameInstance gameInstance = this.plugin.getGameInstance(e.getBlock());
            Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
            if (!Locations.isValidLocation(locations,
                    e.getBlock().getLocation(),
                    gameInstance.getGame().getDetectionMove().getPools(),
                    gameInstance.getRules().get(Rule.PROTECT_POINT),
                    gameInstance.getRules().get(Rule.GRIEF),
                    0, gameInstance.getNumberOfTeams())) {
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(flbl.getMaterial()));
                flbl.remove();
                e.setCancelled(true);
            }
        }
    }
}


