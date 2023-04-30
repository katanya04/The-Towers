package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BorderListener implements Listener {
    private AmazingTowers plugin;

    public BorderListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (!Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.BORDER_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.BORDER_2), e.getBlock().getLocation()))
            e.setCancelled(true);
    }
}


