package mx.towers.pato14.game.events.player;

import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class EnchantItem implements Listener {
    @EventHandler
    public void onEnchantTableClick(PlayerInteractEvent e) {
        if (!Rule.ENCHANTS.getCurrentState()) {
            Material block = e.getClickedBlock().getType();
            if (block.equals(Material.ENCHANTMENT_TABLE))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onAnvilEnchant(InventoryClickEvent e) {
        if (Rule.ENCHANTS.getCurrentState())
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
