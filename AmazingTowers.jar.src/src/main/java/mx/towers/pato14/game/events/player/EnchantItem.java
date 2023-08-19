package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class EnchantItem implements Listener {
    private final AmazingTowers plugin;
    public EnchantItem(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnchantTableClick(PlayerInteractEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getRules().get(Rule.ENCHANTS)) {
            Material block = e.getClickedBlock().getType();
            if (block.equals(Material.ENCHANTMENT_TABLE))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onAnvilEnchant(InventoryClickEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getWhoClicked());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (gameInstance.getRules().get(Rule.ENCHANTS))
            return;
        if (e.getInventory() instanceof AnvilInventory) {
            InventoryView view = e.getView();
            int rawSlot = e.getRawSlot();
            if (rawSlot == view.convertSlot(rawSlot)) {
                if (rawSlot == 2) {
                    ItemStack item = e.getCurrentItem();
                    if (!item.getEnchantments().isEmpty()) {
                        ItemStack item1 = e.getInventory().getItem(0);
                        ItemStack item2 = e.getInventory().getItem(1);
                        if (item1.getType().equals(Material.ENCHANTED_BOOK) ||
                                item2.getType().equals(Material.ENCHANTED_BOOK))
                            e.setCancelled(true);
                        else if (!item1.getEnchantments().isEmpty() &&
                                !item2.getEnchantments().isEmpty())
                            e.setCancelled(true);
                    }
                }
            }
        }
    }
}