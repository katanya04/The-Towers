package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.MenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SetRules extends MenuItem {
    public SetRules(Game game) {
        super(
                Utils.setName(new ItemStack(Material.PAPER), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("settingsBook.setRules"))),
                Rule.getIcons(game)
        );
    }
}
