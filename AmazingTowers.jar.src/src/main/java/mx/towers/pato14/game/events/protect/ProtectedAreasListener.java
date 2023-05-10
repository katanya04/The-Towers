package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Locationshion;
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
    private final AmazingTowers plugin;

    public ProtectedAreasListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        GameInstance gameInstance = plugin.getGameInstance(e.getPlayer());
        if (!GameState.isState(GameState.GAME) || gameInstance.getRules().get(Rule.GRIEF)) {
            return;
        }
        Config locations = this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        Location blockLocation = e.getBlock().getLocation();
        if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE2), blockLocation)) {
            e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.PROTECT_POINT) && Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), blockLocation)) {
            e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.PROTECT_POINT) && Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), blockLocation)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Main(EntityExplodeEvent e) {
        GameInstance gameInstance = plugin.getGameInstance(e.getEntity());
        if (!GameState.isState(GameState.GAME) || gameInstance.getRules().get(Rule.GRIEF)) {
            return;
        }
        Config locations = this.plugin.getGameInstance(e.getEntity()).getConfig(ConfigType.LOCATIONS);
        ArrayList<Block> end = new ArrayList<>(e.blockList());
        for (Block bl : e.blockList()) {
            Location blockLocation = bl.getLocation();
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED2), blockLocation)) {
                end.remove(bl);
            } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE2), blockLocation)) {
                end.remove(bl);
            } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED2), blockLocation)) {
                end.remove(bl);
            } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED2), blockLocation)) {
                end.remove(bl);
            } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE2), blockLocation)) {
                end.remove(bl);
            } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE2), blockLocation)) {
                end.remove(bl);
            } else if (!gameInstance.getRules().get(Rule.PROTECT_POINT) && Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), blockLocation)) {
                end.remove(bl);
            } else if (!gameInstance.getRules().get(Rule.PROTECT_POINT) && Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), blockLocation)) {
                end.remove(bl);
            }
        }
        e.blockList().clear();
        e.blockList().addAll(end);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        GameInstance gameInstance = plugin.getGameInstance(e.getPlayer());
        if (!GameState.isState(GameState.GAME) || gameInstance.getRules().get(Rule.GRIEF)) {
            return;
        }
        Config locations = this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        Location blockLocation = e.getBlock().getLocation();
        if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE2), blockLocation)) {
            e.setCancelled(true);
        } else if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE2), blockLocation)) {
            e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.PROTECT_POINT) && Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), blockLocation)) {
            e.setCancelled(true);
        } else if (!gameInstance.getRules().get(Rule.PROTECT_POINT) && Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), blockLocation)) {
            e.setCancelled(true);
        }
    }
}


