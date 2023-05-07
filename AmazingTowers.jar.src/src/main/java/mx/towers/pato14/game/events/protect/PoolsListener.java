package mx.towers.pato14.game.events.protect;

import java.util.ArrayList;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class PoolsListener implements Listener {
    private final AmazingTowers plugin;

    public PoolsListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreakPools(BlockBreakEvent e) {
        Config locations = this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        if (Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_2), e.getBlock().getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_2), e.getBlock().getLocation(), 1)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlocks(BlockPlaceEvent e) {
        Config locations = this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        if (Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_2), e.getBlock().getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_2), e.getBlock().getLocation(), 1)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Main(EntityExplodeEvent e) {
        Config locations = this.plugin.getGameInstance(e.getEntity()).getConfig(ConfigType.LOCATIONS);
        ArrayList<Block> end = new ArrayList<>(e.blockList());
        for (Block bl : e.blockList()) {
            if (Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_2), bl.getLocation(), 1)) {
                end.remove(bl);
            }
        }
        e.blockList().clear();
        e.blockList().addAll(end);
    }
}


