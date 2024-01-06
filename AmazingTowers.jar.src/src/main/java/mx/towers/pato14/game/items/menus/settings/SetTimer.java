package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SetTimer extends BookMenuItem {
    public SetTimer(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.WATCH),
                Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.setTimer"))),
                ConfigType.GAME_SETTINGS, gameInstance, "timer");
    }

    @Override
    public void updateSettings(GameInstance gameInstance, String path) {
        super.updateSettings(gameInstance, path);
        gameInstance.getGame().getTimer().update(gameInstance);
    }
}
