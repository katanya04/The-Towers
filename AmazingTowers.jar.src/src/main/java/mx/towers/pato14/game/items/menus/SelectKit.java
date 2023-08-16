package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.ChestInventoryItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SelectKit extends ChestInventoryItem {
    public SelectKit(Game game) {
        super(
                Utils.setName(new ItemStack(Material.IRON_SWORD), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.selectKit.name"))),
                game.getKits().getIcons()
        );
    }
}
