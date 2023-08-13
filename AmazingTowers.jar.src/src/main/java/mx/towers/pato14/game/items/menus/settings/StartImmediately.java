package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StartImmediately extends ItemStack {
    public StartImmediately(Game game) {
        super(Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5),
                AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("settingsBook.startImmediately"))));
    }
}