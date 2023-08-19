package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SetWhitelist extends BookMenuItem {
    public SetWhitelist(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.BOOK_AND_QUILL),
                AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.setWhitelist"))),
                ConfigType.GAME_SETTINGS,
                gameInstance,
                "whitelist");
    }

    @Override
    public void updateSettings(GameInstance gameInstance) {
        super.updateSettings(gameInstance);
        gameInstance.updateWhiteList();
    }
}
