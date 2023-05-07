package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class PistonsillosListener implements Listener {
    private final AmazingTowers plugin;

    public PistonsillosListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent e) {
        Config locations = this.plugin.getGameInstance(e.getBlock()).getConfig(ConfigType.LOCATIONS);
        if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNRED_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNRED_PROTECT_2), e.getBlock().getRelative(e.getDirection()).getLocation()) ||
                Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNBLUE_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNBLUE_PROTECT_2), e.getBlock().getRelative(e.getDirection()).getLocation()) ||
                !Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.BORDER_1), Locations.getLocationFromStringConfig(locations, Locationshion.BORDER_2), e.getBlock().getRelative(e.getDirection()).getLocation()) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1) ||
                Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), e.getBlock().getRelative(e.getDirection()).getLocation(), 1)) {
            e.setCancelled(true);
        }
        for (Block bl : e.getBlocks()) {
            if (Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNRED_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNRED_PROTECT_2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNBLUE_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNBLUE_PROTECT_2), bl.getLocation(), 1) ||
                    !Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.BORDER_1), Locations.getLocationFromStringConfig(locations, Locationshion.BORDER_2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), bl.getLocation(), 1) ||
                    Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), bl.getLocation(), 1)) {
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onSticky(BlockPistonRetractEvent e) {
        Config locations = this.plugin.getGameInstance(e.getBlock()).getConfig(ConfigType.LOCATIONS);
        if (e.isSticky())
            for (Block bl : e.getBlocks()) {
                if (Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNRED_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNRED_PROTECT_2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNBLUE_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.SPAWNBLUE_PROTECT_2), bl.getLocation(), 1) ||
                        !Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.BORDER_1), Locations.getLocationFromStringConfig(locations, Locationshion.BORDER_2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_BLUE_2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_1), Locations.getLocationFromStringConfig(locations, Locationshion.POOL_RED_2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGERED2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.BRIDGEBLUE2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1RED2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2RED2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM1BLUE2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.CHESTROOM2BLUE2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTRED2), bl.getLocation(), 1) ||
                        Cuboide.InCuboideExtraHeight(Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE1), Locations.getLocationFromStringConfig(locations, Locationshion.POINTBLUE2), bl.getLocation(), 1)) {
                    e.setCancelled(true);
                    break;
                }
            }
    }
}


