package mx.towers.pato14.game.items;

import mx.towers.pato14.GameInstance;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public abstract class MenuItem extends ActionItem {
    MenuItem(ItemStack itemStack) {
        super(itemStack);
    }
    abstract void openMenu(HumanEntity player);
    public void interact(HumanEntity player, GameInstance gameInstance) {
        super.interact(player, gameInstance);
        openMenu(player);
    }
}
