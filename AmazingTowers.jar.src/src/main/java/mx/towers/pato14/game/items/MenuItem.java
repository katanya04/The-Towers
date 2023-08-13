package mx.towers.pato14.game.items;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MenuItem extends ItemStack {
    private final Inventory menu;
    private ItemStack[] contents;

    public MenuItem(ItemStack icon, ItemStack[] contents) {
        super(icon);
        this.menu = Bukkit.createInventory(null, Utils.ceilToMultipleOfNine(contents.length), icon.getItemMeta().getDisplayName());
        this.menu.addItem(contents);
        this.contents = contents;
    }

    public MenuItem(String name, ItemStack icon, Map<Integer, ItemStack> contents, int size) {
        super(icon);
        this.menu = Bukkit.createInventory(null, size, name);
        for (Map.Entry<Integer, ItemStack> item : contents.entrySet())
            this.menu.setItem(item.getKey(), item.getValue());
        this.contents = contents.values().toArray(new ItemStack[0]);
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

    public void setContents(ItemStack[] contents) {
        menu.setContents(contents);
        this.contents = contents;
    }

    public void setContents(Map<Integer, ItemStack> contents) {
        for (Map.Entry<Integer, ItemStack> item : contents.entrySet())
            this.menu.setItem(item.getKey(), item.getValue());
        this.contents = contents.values().toArray(new ItemStack[0]);
    }

    public String getConfigName(String name, Game game) {
        return game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems." + name + ".name");
    }
}
