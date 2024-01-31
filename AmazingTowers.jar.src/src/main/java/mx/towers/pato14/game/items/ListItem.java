package mx.towers.pato14.game.items;

import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.utils.Utils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ListItem<T> extends ActionItem {
    protected List<T> list;
    protected int index;
    protected final boolean nullOption;
    protected ListItem(ItemStack itemStack, List<T> list, int index, boolean nullOption) {
        super(itemStack);
        this.list = list;
        this.index = index;
        this.nullOption = nullOption;
        Utils.setLore(this, "§r§f" + getName());
    }
    protected T getCurrentItem() {
        return index >= 0 ? list.get(index) : null;
    }
    protected String getName() {
        return index >= 0 ? list.get(index).toString() : "None";
    }
    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        super.interact(player, instance);
        if (index >= list.size() - 1)
            index = nullOption ? -1 : 0;
        else
            index++;
        Utils.setLore(this, "§r§f" + getName());
    }
}
