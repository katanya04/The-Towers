package mx.towers.pato14.game.events.player;

import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class EnderpearlAndPotionThrowListener implements Listener {
    @EventHandler
    public void onEnderpearlAndPotionThrow(ProjectileLaunchEvent e) {
        if (!Rule.ENDERPEARL.getCurrentState() && e.getEntity() instanceof EnderPearl)
            e.setCancelled(true);
        else if (!Rule.POTS_AND_APPLE.getCurrentState() && e.getEntity() instanceof ThrownPotion)
            e.setCancelled(true);
    }
}
