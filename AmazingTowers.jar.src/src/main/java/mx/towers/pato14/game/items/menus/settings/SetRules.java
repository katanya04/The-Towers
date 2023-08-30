package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.ChestMenuItem;
import mx.towers.pato14.game.items.menus.RuleItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SetRules extends ChestMenuItem {
    public SetRules(GameInstance gameInstance) {
        super(
                Utils.setName(new ItemStack(Material.PAPER), AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.setRules"))),
                RuleItem.createAllRules(gameInstance)
        );
    }

    public void updateMenu(GameInstance gameInstance) {
        this.getMenu().setContents(RuleItem.createAllRules(gameInstance));
    }
}
