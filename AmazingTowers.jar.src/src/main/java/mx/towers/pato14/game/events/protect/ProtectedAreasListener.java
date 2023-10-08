package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;

public class ProtectedAreasListener implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Config locations = AmazingTowers.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        Location blockLocation = e.getBlock().getLocation();
        if (!Locations.isValidLocation(locations,
                blockLocation,
                gameInstance.getGame().getDetectionMove().getPools(),
                gameInstance.getRules().get(Rule.PROTECT_POINT),
                gameInstance.getRules().get(Rule.GRIEF),
                0, gameInstance.getNumberOfTeams())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Config locations = AmazingTowers.getGameInstance(e.getEntity()).getConfig(ConfigType.LOCATIONS);
        ArrayList<Block> end = new ArrayList<>(e.blockList());
        for (Block bl : e.blockList()) {
            Location blockLocation = bl.getLocation();
            if (!Locations.isValidLocation(locations,
                    blockLocation,
                    gameInstance.getGame().getDetectionMove().getPools(),
                    gameInstance.getRules().get(Rule.PROTECT_POINT),
                    gameInstance.getRules().get(Rule.GRIEF),
                    0, gameInstance.getNumberOfTeams())) {
                end.remove(bl);
            }
        }
        e.blockList().clear();
        e.blockList().addAll(end);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Config locations = AmazingTowers.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        Location blockLocation = e.getBlock().getLocation();
        if (!Locations.isValidLocation(locations,
                blockLocation,
                gameInstance.getGame().getDetectionMove().getPools(),
                gameInstance.getRules().get(Rule.PROTECT_POINT),
                gameInstance.getRules().get(Rule.GRIEF),
                0, gameInstance.getNumberOfTeams())) {
            e.setCancelled(true);
        }
    }
}


