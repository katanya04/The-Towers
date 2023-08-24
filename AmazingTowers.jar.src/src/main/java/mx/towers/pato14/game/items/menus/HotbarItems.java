package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.game.items.ChestMenuItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HotbarItems {
    protected final HashMap<Integer, ItemStack> hotbarItems;
    public HotbarItems() {
        this.hotbarItems = new HashMap<>();
    }
    public HashMap<Integer, ItemStack> getHotbarItems() {
        return hotbarItems;
    }
    public void giveHotbarItems(HumanEntity player) {
        for (Map.Entry<Integer, ItemStack> item : hotbarItems.entrySet())
            player.getInventory().setItem(item.getKey(), item.getValue());
    }
    public boolean isALobbyItem(ItemStack itemStack, Inventory inventory) {
        for (ChestMenuItem chestMenuItem : getChestMenus())
            if (inventory.equals(chestMenuItem.getMenu()))
                return true;
        for (ItemStack item : this.hotbarItems.values()) {
            if (item.equals(itemStack))
                return true;
        }
        return false;
    }
    public List<ChestMenuItem> getChestMenus() {
        List<ChestMenuItem> toret = new ArrayList<>();
        for (ItemStack hotbarItem : hotbarItems.values()) {
            recursiveSearchChestMenus(toret, hotbarItem);
        }
        return toret;
    }
    public BookMenuItem getBookMenu(String path) {
        BookMenuItem toret = null;
        for (ItemStack hotbarItem : hotbarItems.values()) {
            toret = recursiveSearchBookMenu(hotbarItem, path);
            if (toret != null) return toret;
        }
        return toret;
    }
    private void recursiveSearchChestMenus(List<ChestMenuItem> list, ItemStack item) {
        if (!(item instanceof ChestMenuItem))
            return;
        list.add(((ChestMenuItem) item));
        for (ItemStack item2 : ((ChestMenuItem) item).getContents().values())
            recursiveSearchChestMenus(list, item2);
    }
    private BookMenuItem recursiveSearchBookMenu(ItemStack item, String path) {
        BookMenuItem toret;
        if (item instanceof BookMenuItem && ((BookMenuItem) item).getFullPath().equals(path)) {
            return (BookMenuItem) item;
        } else if (item instanceof ChestMenuItem) {
            for (ItemStack item2 : ((ChestMenuItem) item).getContents().values()) {
                toret = recursiveSearchBookMenu(item2, path);
                if (toret != null) return toret;
            }
        }
        return null;
    }
}