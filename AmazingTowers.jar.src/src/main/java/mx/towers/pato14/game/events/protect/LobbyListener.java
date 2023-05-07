package mx.towers.pato14.game.events.protect;

import java.util.ArrayList;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class LobbyListener implements Listener {
    private final AmazingTowers plugin;

    public LobbyListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        Config locations = this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        if (GameState.isState(GameState.LOBBY) ||
                Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_2), e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        Config locations = this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        if (GameState.isState(GameState.LOBBY) ||
                Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_2), e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Main(EntityExplodeEvent e) {
        Config locations = this.plugin.getGameInstance(e.getEntity()).getConfig(ConfigType.LOCATIONS);
        ArrayList<Block> end = new ArrayList<>(e.blockList());
        for (Block bl : e.blockList()) {
            if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_2), bl.getLocation())) {
                end.remove(bl);
            }
        }
        e.blockList().clear();
        e.blockList().addAll(end);
    }

    @EventHandler
    public void Food(FoodLevelChangeEvent e) {
        Config locations = this.plugin.getGameInstance(e.getEntity()).getConfig(ConfigType.LOCATIONS);
        if (GameState.isState(GameState.LOBBY) ||
                Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_2), e.getEntity().getLocation())) {
            e.setCancelled(true);
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                p.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Config locations = this.plugin.getGameInstance(e.getEntity()).getConfig(ConfigType.LOCATIONS);
        if (e.getEntityType().equals(EntityType.PLAYER) &&
                Cuboide.InCuboide(Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_1), Locations.getLocationFromStringConfig(locations, Locationshion.LOBBY_PROTECT_2), e.getEntity().getLocation()))
            e.setCancelled(true);
    }
}


