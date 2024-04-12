package mx.towers.pato14.utils.items;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public interface Items {
    static ItemStack get(ItemsEnum itemEnum) {
        return getByName(itemEnum.name);
    }
    static ItemStack getByName(String name) {
        ActionItem item = ActionItem.getByName(name);
        return item == null ? null : item.returnPlaceholder();
    }
    static boolean is(ItemsEnum itemEnum, ItemStack item) {
        return Objects.equals(ActionItem.getActionItem(item), ActionItem.getByName(itemEnum.name));
    }
}