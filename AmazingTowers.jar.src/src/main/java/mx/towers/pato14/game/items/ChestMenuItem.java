package mx.towers.pato14.game.items;

import mx.towers.pato14.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class ChestMenuItem extends MenuItem {
    private final Inventory menu;
    private Map<Integer, ItemStack> contents;

    public ChestMenuItem(ItemStack icon, ItemStack[] contents) {
        super(icon);
        this.menu = Bukkit.createInventory(null, Utils.ceilToMultipleOfNine(contents.length), icon.getItemMeta().getDisplayName());
        this.menu.addItem(contents);
        this.contents = new HashMap<>();
        setContents(contents);
    }

    public ChestMenuItem(String name, ItemStack icon, Map<Integer, ItemStack> contents, int size) {
        super(icon);
        this.menu = Bukkit.createInventory(null, size, name);
        setContents(contents);
    }

    public Inventory getMenu() {
        return menu;
    }

    public Map<Integer, ItemStack> getContents() {
        return contents;
    }

    public void setContents(Map<Integer, ItemStack> contents) {
        for (Map.Entry<Integer, ItemStack> item : contents.entrySet())
            this.menu.setItem(item.getKey(), item.getValue());
        this.contents = contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = new HashMap<>();
        int i = 0;
        for (ItemStack item : contents) {
            this.contents.put(i, item);
            i++;
        }
        this.menu.setContents(contents);
    }

    @Override
    public void openMenu(HumanEntity player) {
        player.openInventory(this.menu);
    }

    public void updateMenu() {
        this.setContents(this.getContents());
    }
}
