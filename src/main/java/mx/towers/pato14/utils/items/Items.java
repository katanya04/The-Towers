package mx.towers.pato14.utils.items;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import me.katanya04.anotherguiplugin.actionItems.MenuItem;
import me.katanya04.anotherguiplugin.menu.InventoryMenu;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public interface Items {
    static ItemStack get(ItemsEnum itemEnum) {
        return getByName(itemEnum.name);
    }
    static ItemStack getByName(String name) {
        ActionItem<?> item = ActionItem.getByName(name);
        return item == null ? null : item.returnPlaceholder();
    }
    @SuppressWarnings("unchecked")
    static void updateMenu(ItemsEnum itemsEnum) {
        ActionItem<?> item = ActionItem.getByName(itemsEnum.name);
        ((MenuItem<InventoryMenu, ?>) item).getMenu().updateOpenMenus();
    }
    static boolean is(ItemsEnum itemEnum, ItemStack item) {
        return Objects.equals(ActionItem.getActionItem(item), ActionItem.getByName(itemEnum.name));
    }
    static ItemStack getAndParse(ItemsEnum itemEnum, Object arg) {
        return getByNameAndParse(itemEnum.name, arg);
    }
    static ItemStack getByNameAndParse(String name, Object arg) {
        ActionItem<Object> item = ActionItem.getByName(name);
        return item == null ? null : item.toItemStack(arg);
    }
}