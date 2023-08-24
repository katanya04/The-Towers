package mx.towers.pato14.game.items.actions;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class DenyBuy extends ActionItem {
    public DenyBuy(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.WOOL, 1, (short) 14), AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.denyBuy"))));
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        player.openInventory(gameInstance.getHotbarItems().getSelectKit().getMenu());
    }
}
