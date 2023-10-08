package mx.towers.pato14.game.items.actions;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuitItem extends ActionItem {
    public QuitItem(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.BED), AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.quit.name"))));
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        Utils.tpToWorld(AmazingTowers.getLobby().getWorld(), (Player) player);
    }
}
