package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

public class AntiFallingSandTrollListener implements Listener {
    private AmazingTowers plugin;

    public AntiFallingSandTrollListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFallingSand(EntityChangeBlockEvent e) {
        if (e.getEntityType().equals(EntityType.FALLING_BLOCK)) {
            FallingBlock flbl = (FallingBlock) e.getEntity();
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNRED_PROTECT_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNRED_PROTECT_2), e.getBlock().getLocation())) {
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(flbl.getMaterial()));
                flbl.remove();
                e.setCancelled(true);
            } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNBLUE_PROTECT_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNBLUE_PROTECT_2), e.getBlock().getLocation())) {
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(flbl.getMaterial()));
                flbl.remove();
                e.setCancelled(true);
            }
        }
    }
}


