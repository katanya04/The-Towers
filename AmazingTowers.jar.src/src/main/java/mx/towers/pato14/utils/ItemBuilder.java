package mx.towers.pato14.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta itemMeta;
    private String name;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(Material material, Short damage) {
        this.item = new ItemStack(material, 1, damage.shortValue());
    }

    public ItemBuilder(Integer type, short damage) {
        this.item = new ItemStack(type.intValue(), 1, damage);
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        this.itemMeta = this.item.getItemMeta();
        this.itemMeta.setDisplayName(name);
        this.item.setItemMeta(this.itemMeta);
        return this;
    }

    public ItemBuilder setDurability(Short sh) {
        this.item.setDurability(sh.shortValue());
        return this;
    }

    public ItemBuilder setLore(List<String> listLore) {
        this.itemMeta = this.item.getItemMeta();
        this.itemMeta.setLore(listLore);
        this.item.setItemMeta(this.itemMeta);
        return this;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public String getName() {
        return this.name;
    }
}


