package mx.towers.pato14.game.events.player;

import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class BowListener implements Listener {
    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {
        if (!Rule.BOW.getCurrentState())
            e.setCancelled(true);
    }
}
