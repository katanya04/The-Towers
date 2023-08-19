package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.ChestMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SelectTeam extends ChestMenuItem {
    public SelectTeam(GameInstance gameInstance) {
        super(
                Utils.setName(new ItemStack(Material.WOOL, 1, (short) 14), AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.selectTeam.name"))),
                JoinTeamItem.createAllTeams(gameInstance)
        );
    }

    public JoinTeamItem getItemByTeam(TeamColor team) {
        for (ItemStack item : getContents().values()) {
            if (item instanceof JoinTeamItem && ((JoinTeamItem) item).getTeamColor() == team)
                return (JoinTeamItem) item;
        }
        return null;
    }
}
