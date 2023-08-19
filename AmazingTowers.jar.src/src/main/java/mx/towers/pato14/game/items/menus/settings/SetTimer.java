package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SetTimer extends BookMenuItem {
    public SetTimer(Game game) {
        super(Utils.setName(new ItemStack(Material.WATCH),
                AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("settingsBook.setTimer"))),
                ConfigType.GAME_SETTINGS, game.getGameInstance(), "timer");
    }

    @Override
    public void updateSettings(GameInstance gameInstance) {
        super.updateSettings(gameInstance);
        gameInstance.getGame().getTimer().update();
    }
}