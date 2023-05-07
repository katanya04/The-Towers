package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class EnderpearlAndPotionThrowListener implements Listener {
    private final AmazingTowers plugin;
    public EnderpearlAndPotionThrowListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onEnderpearlAndPotionThrow(ProjectileLaunchEvent e) {
        if (!this.plugin.getGameInstance(e.getEntity()).getRules().get(Rule.ENDERPEARL) && e.getEntity() instanceof EnderPearl)
            e.setCancelled(true);
        else if (!this.plugin.getGameInstance(e.getEntity()).getRules().get(Rule.POTS_AND_APPLE) && e.getEntity() instanceof ThrownPotion)
            e.setCancelled(true);
    }
}
