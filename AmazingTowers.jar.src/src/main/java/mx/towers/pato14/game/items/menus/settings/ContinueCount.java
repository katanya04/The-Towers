package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ContinueCount extends ActionItem {
    public ContinueCount(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4),
                AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.continueCount"))));
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        super.interact(player, gameInstance);
        Start start = gameInstance.getGame().getStart();
        if (!start.hasStarted()) {
            gameInstance.getGame().setGameState(GameState.PREGAME);
            start.setRunFromCommand(true);
            start.setHasStarted(true);
            start.gameStart();
        }
        start.continueCount();
    }
}