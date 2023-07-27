package mx.towers.pato14.utils.cofresillos;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FixedItem {
    private HashMap<Enchantment, Integer> papu;
    private final Material material;
    private final int amount;
    private final short durability;

    public FixedItem(ItemStack i) {
        this.material = i.getType();
        if (i.getItemMeta().hasEnchants()) {
            this.papu = new HashMap<>(i.getItemMeta().getEnchants());
        }
        this.amount = i.getAmount();
        this.durability = i.getDurability();
    }

    public static FixedItem[] itemStackToFixedItem(ItemStack[] chestContents) {
        int i = 0;
        FixedItem[] toret = new FixedItem[chestContents.length];
        for (ItemStack item : chestContents) {
            toret[i] = item == null ? null : new FixedItem(item);
            i++;
        }
        return toret;
    }

    public static ItemStack[] fixedItemToItemStack(FixedItem[] fixedItems) {
        int i = 0;
        ItemStack[] toret = new ItemStack[fixedItems.length];
        for (FixedItem item : fixedItems) {
            toret[i] = item == null ? null : item.getItemStack();
            i++;
        }
        return toret;
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(this.material, this.amount, this.durability);
        ItemMeta imeta = item.getItemMeta();
        if (this.papu != null) {
            for (Enchantment e : this.papu.keySet()) {
                imeta.addEnchant(e, this.papu.get(e), false);
            }
        }
        item.setItemMeta(imeta);
        return item;
    }
}


