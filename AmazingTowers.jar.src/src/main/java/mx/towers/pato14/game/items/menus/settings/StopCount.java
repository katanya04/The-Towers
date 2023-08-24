package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StopCount extends ActionItem {
    public StopCount(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14),
                AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.stopCount"))));
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        super.interact(player, gameInstance);
        gameInstance.getGame().getStart().stopCount();
    }
}