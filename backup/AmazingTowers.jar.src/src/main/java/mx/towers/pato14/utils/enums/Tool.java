package mx.towers.pato14.utils.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Tool {
    WAND("§aRegion Selector", new ItemStack(Material.IRON_AXE), "§aLeft Click §7to set §aPos1 §7and §aRight click §7to set §aPos2"),
    REFILLCHEST("§aSelect and Remove Chest Refill", new ItemStack(Material.IRON_SPADE), "§aLeft Click §7to set chest Refill and §cRight Click §7to remove chest location in Config!");
    private final ItemStack item;
    private final String msg;
    Tool(String name, ItemStack item, String msg) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        this.item = item;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean checkIfItemIsTool(ItemStack item) {
        return item != null &&
                item.getType().equals(this.getItem().getType()) &&
                item.getItemMeta().getDisplayName().equals(this.getItem().getItemMeta().getDisplayName());
    }
}
