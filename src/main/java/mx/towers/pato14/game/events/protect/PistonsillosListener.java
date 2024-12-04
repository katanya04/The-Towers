package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class PistonsillosListener implements Listener {

    @EventHandler
    public void onPiston(BlockPistonExtendEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getBlock());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
        if (!Locations.isValidLocation(locations,
                e.getBlock().getRelative(e.getDirection()).getLocation(),
                gameInstance.getGame().getDetectionMove().getPools(),
                gameInstance.getRules().get(Rule.PROTECT_POINT),
                gameInstance.getRules().get(Rule.GRIEF),
                1, gameInstance.getNumberOfTeams())) {
            e.setCancelled(true);
            return;
        }
        for (Block bl : e.getBlocks()) {
            if (!Locations.isValidLocation(locations,
                    bl.getLocation(),
                    gameInstance.getGame().getDetectionMove().getPools(),
                    gameInstance.getRules().get(Rule.PROTECT_POINT),
                    gameInstance.getRules().get(Rule.GRIEF),
                    1, gameInstance.getNumberOfTeams())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onSticky(BlockPistonRetractEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getBlock());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
        if (e.isSticky())
            for (Block bl : e.getBlocks()) {
                if (!Locations.isValidLocation(locations,
                        bl.getLocation(),
                        gameInstance.getGame().getDetectionMove().getPools(),
                        gameInstance.getRules().get(Rule.PROTECT_POINT),
                        gameInstance.getRules().get(Rule.GRIEF),
                        1, gameInstance.getNumberOfTeams())) {
                    e.setCancelled(true);
                    return;
                }
            }
    }
}


