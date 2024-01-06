package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.ChestMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SelectKit extends ChestMenuItem {
    public SelectKit(GameInstance gameInstance) {
        super(
                Utils.setName(new ItemStack(Material.IRON_SWORD), Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.selectKit.name"))),
                gameInstance.getGame().getKits().getIcons()
        );
    }
}
