package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Location;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class LobbyListener implements Listener {

    @EventHandler
    public void Food(FoodLevelChangeEvent e) {
        GameInstance gameInstance = AmazingTowers.getPlugin().getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (p.getGameMode().equals(GameMode.ADVENTURE)) {
                p.setFoodLevel(20);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        GameInstance gameInstance = AmazingTowers.getPlugin().getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (p.getGameMode().equals(GameMode.ADVENTURE))
                e.setCancelled(true);
        }
    }
}


