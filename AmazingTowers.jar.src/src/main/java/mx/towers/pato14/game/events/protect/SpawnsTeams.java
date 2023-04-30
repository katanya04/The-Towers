package mx.towers.pato14.game.events.protect;

import java.util.ArrayList;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class SpawnsTeams implements Listener {
    private AmazingTowers plugin;

    public SpawnsTeams(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (!GameState.isState(GameState.GAME)) {
            return;
        }
        if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNRED_PROTECT_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNRED_PROTECT_2), e.getBlock().getLocation())) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNBLUE_PROTECT_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNBLUE_PROTECT_2), e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Main(EntityExplodeEvent e) {
        ArrayList<Block> end = new ArrayList<>(e.blockList());
        for (Block bl : e.blockList()) {
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNBLUE_PROTECT_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNBLUE_PROTECT_2), bl.getLocation())) {
                end.remove(bl);
                continue;
            }
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNRED_PROTECT_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNRED_PROTECT_2), bl.getLocation())) {
                e.setCancelled(true);
            }
        }
        e.blockList().clear();
        e.blockList().addAll(end);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (!GameState.isState(GameState.GAME)) {
            return;
        }
        if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNRED_PROTECT_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNRED_PROTECT_2), e.getBlock().getLocation())) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNBLUE_PROTECT_1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.SPAWNBLUE_PROTECT_2), e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }
}


