package mx.towers.pato14.game.events.player;

import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class IronArmorListener implements Listener {
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if(!Rule.IRON_ARMOR.getCurrentState() && e.getSlotType() == InventoryType.SlotType.ARMOR){
            e.setCancelled(true);
        }

    }
}
