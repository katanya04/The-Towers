package mx.towers.pato14.game.team;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuItem extends ItemStack {
    private final Inventory menu;
    private final ItemStack[] contents;

    public MenuItem(String name, int size, ItemStack icon, ItemStack[] contents) {
        super(icon);
        this.menu = Bukkit.createInventory(null, size, name);
        this.menu.addItem(contents);
        this.contents = contents;
    }

    public ItemStack getIcon() {
        return this;
    }

    public Inventory getMenu() {
        return menu;
    }

    public ItemStack[] getContents() {
        return contents;
    }
}
