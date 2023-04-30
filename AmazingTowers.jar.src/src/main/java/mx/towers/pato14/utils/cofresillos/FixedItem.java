package mx.towers.pato14.utils.cofresillos;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FixedItem {
    private HashMap<Enchantment, Integer> papu;
    private Material material;
    private int amount;
    private short durability;

    public FixedItem(ItemStack i) {
        this.material = i.getType();
        if (i.getItemMeta().hasEnchants()) {
            this.papu = new HashMap<>(i.getItemMeta().getEnchants());
        }
        this.amount = i.getAmount();
        this.durability = i.getDurability();
    }

    public static FixedItem[] getArrayoBobin(ItemStack[] i) {
        int in = 0;
        FixedItem[] end = new FixedItem[i.length];
        byte b;
        int j;
        ItemStack[] arrayOfItemStack;
        for (j = (arrayOfItemStack = i).length, b = 0; b < j; ) {
            ItemStack is = arrayOfItemStack[b];
            end[in] = (is != null) ? new FixedItem(is) : null;
            in++;
            b++;
        }
        return end;
    }

    public static ItemStack[] getAGalloConTennis(FixedItem[] f) {
        int in = 0;
        ItemStack[] end = new ItemStack[f.length];
        byte b;
        int i;
        FixedItem[] arrayOfFixedItem;
        for (i = (arrayOfFixedItem = f).length, b = 0; b < i; ) {
            FixedItem is = arrayOfFixedItem[b];
            end[in] = (is != null) ? is.getItemStack() : null;
            in++;
            b++;
        }
        return end;
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(this.material, this.amount, this.durability);
        ItemMeta imeta = item.getItemMeta();
        if (this.papu != null) {
            for (Enchantment e : this.papu.keySet()) {
                imeta.addEnchant(e, ((Integer) this.papu.get(e)).intValue(), false);
            }
        }
        item.setItemMeta(imeta);
        return item;
    }
}


