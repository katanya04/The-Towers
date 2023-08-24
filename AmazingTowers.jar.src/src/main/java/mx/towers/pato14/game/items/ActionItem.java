package mx.towers.pato14.game.items;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ActionItem extends ItemStack {
    protected ActionItem(ItemStack itemStack) {
        super(itemStack);
    }
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
    }
}
