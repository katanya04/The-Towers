package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class IronArmorListener implements Listener {

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getWhoClicked());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getRules().get(Rule.IRON_ARMOR) && e.getSlotType() == InventoryType.SlotType.ARMOR) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getView().getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;

        if (!gameInstance.getRules().get(Rule.IRON_ARMOR)) {
            CraftingInventory inventory = e.getInventory();
            ItemStack result = inventory.getResult();

            if (result != null && isIronArmor(result.getType())) {
                inventory.setResult(null);
            }
        }
    }

    private boolean isIronArmor(Material material) {
        return material == Material.IRON_HELMET ||
               material == Material.IRON_CHESTPLATE ||
               material == Material.IRON_LEGGINGS ||
               material == Material.IRON_BOOTS;
    }
}
