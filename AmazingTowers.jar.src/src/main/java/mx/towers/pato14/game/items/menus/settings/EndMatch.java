package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.MessageType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class EndMatch extends ActionItem {
    public EndMatch(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15),
                Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.endMatch"))));
    }
    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        super.interact(player, instance);
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        Utils.endMatch(gameInstance);
        if (Objects.requireNonNull(gameInstance.getGame().getGameState()) == GameState.GOLDEN_GOAL)
            Utils.sendMessage("Redo this action to finish the match definitively", MessageType.INFO, player);
        else if (Objects.requireNonNull(gameInstance.getGame().getGameState()) != GameState.FINISH)
            Utils.sendMessage("This action can only be done while a match is taking place", MessageType.ERROR, player);
    }
}
