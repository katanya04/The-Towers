package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class OrePlacedListener implements Listener {
    @EventHandler
    public void onOrePlaced(BlockPlaceEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
        if (!gameInstance.getRules().get(Rule.EMERALD) && e.getBlock().getType().equals(Material.EMERALD_BLOCK)) {
            if (Locations.isInsidePoolRoom(locations, e.getBlock().getLocation(), 0, gameInstance.getNumberOfTeams()))
                e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.REDSTONE) && e.getBlock().getType().equals(Material.REDSTONE_BLOCK)) {
            if (Locations.isInsidePoolRoom(locations, e.getBlock().getLocation(), 0, gameInstance.getNumberOfTeams()))
                e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.COAL) && e.getBlock().getType().equals(Material.COAL_BLOCK)) {
            if (Locations.isInsidePoolRoom(locations, e.getBlock().getLocation(), 0, gameInstance.getNumberOfTeams()))
                e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.LAPISLAZULI) && e.getBlock().getType().equals(Material.LAPIS_BLOCK)) {
            if (Locations.isInsidePoolRoom(locations, e.getBlock().getLocation(), 0, gameInstance.getNumberOfTeams()))
                e.setCancelled(true);
        }
    }
}
