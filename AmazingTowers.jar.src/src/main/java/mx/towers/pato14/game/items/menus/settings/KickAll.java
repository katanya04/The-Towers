package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KickAll extends ActionItem {
    public KickAll(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.REDSTONE_TORCH_ON),
                AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.kickAll.name"))));
    }

    @Override
    public void interact(HumanEntity player, GameInstance gameInstance) {
        super.interact(player, gameInstance);
        for (Player p : gameInstance.getGame().getPlayers()) {
            if (!p.isOp() && !p.equals(player))
                p.kickPlayer(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.kickAll.kickMessage"));
        }
    }
}