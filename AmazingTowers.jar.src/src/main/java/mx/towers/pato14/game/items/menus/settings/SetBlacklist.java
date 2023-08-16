package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SetBlacklist extends ItemStack {
    public SetBlacklist(Game game) {
        super(Utils.setName(new ItemStack(Material.BOOK_AND_QUILL),
                AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("settingsBook.setBlacklist"))));
    }
}
