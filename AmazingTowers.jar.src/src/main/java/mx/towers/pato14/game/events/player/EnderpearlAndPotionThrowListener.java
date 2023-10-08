package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class EnderpearlAndPotionThrowListener implements Listener {
    @EventHandler
    public void onEnderpearlAndPotionThrow(ProjectileLaunchEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getRules().get(Rule.ENDER_PEARL) && e.getEntity() instanceof EnderPearl)
            e.setCancelled(true);
        else if (!gameInstance.getRules().get(Rule.POTS_AND_APPLE) && e.getEntity() instanceof ThrownPotion)
            e.setCancelled(true);
    }
}
