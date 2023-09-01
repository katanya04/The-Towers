package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SetBlacklist extends BookMenuItem {
    public SetBlacklist(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.BOOK_AND_QUILL),
                        AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.setBlacklist"))),
                ConfigType.GAME_SETTINGS,
                gameInstance,
                "blacklist");
    }

    @Override
    public void updateSettings(GameInstance gameInstance, String path) {
        super.updateSettings(gameInstance, path);
        gameInstance.updateBlackList();
    }
}
