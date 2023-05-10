package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class OrePlacedListener implements Listener {
    private final AmazingTowers plugin;
    public OrePlacedListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onOrePlaced(BlockPlaceEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getPlayer());
        Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
        if (!gameInstance.getRules().get(Rule.EMERALD) && e.getBlock().getType().equals(Material.EMERALD_BLOCK)) {
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), e.getBlock().getLocation()) ||
                    Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), e.getBlock().getLocation()))
                e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.REDSTONE) && e.getBlock().getType().equals(Material.REDSTONE_BLOCK)) {
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), e.getBlock().getLocation()) ||
                    Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), e.getBlock().getLocation()))
                e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.COAL) && e.getBlock().getType().equals(Material.COAL_BLOCK)) {
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), e.getBlock().getLocation()) ||
                    Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), e.getBlock().getLocation()))
                e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.LAPISLAZULI) && e.getBlock().getType().equals(Material.LAPIS_BLOCK)) {
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), e.getBlock().getLocation()) ||
                    Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), e.getBlock().getLocation()))
                e.setCancelled(true);
        }
    }
}
