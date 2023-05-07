package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class BowListener implements Listener {
    private final AmazingTowers plugin;
    public BowListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {
        if (!this.plugin.getGameInstance(e.getEntity()).getRules().get(Rule.BOW))
            e.setCancelled(true);
    }
}
