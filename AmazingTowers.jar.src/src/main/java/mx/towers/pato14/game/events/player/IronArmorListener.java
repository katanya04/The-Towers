package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class IronArmorListener implements Listener {
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getWhoClicked());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getRules().get(Rule.IRON_ARMOR) && e.getSlotType() == InventoryType.SlotType.ARMOR){
            e.setCancelled(true);
        }

    }
}
