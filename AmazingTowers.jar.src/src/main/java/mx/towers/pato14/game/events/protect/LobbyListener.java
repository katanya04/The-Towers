package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class LobbyListener implements Listener {
    private final AmazingTowers plugin;

    public LobbyListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void Food(FoodLevelChangeEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getEntity());
        Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
        if (gameInstance.getGame().getGameState().equals(GameState.LOBBY) ||
                Cuboide.InCuboide(locations.getString(Location.LOBBY.getPath()), e.getEntity().getLocation())) {
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
                Cuboide.InCuboide(locations.getString(Location.LOBBY.getPath()), e.getEntity().getLocation()))
            e.setCancelled(true);
    }
}


