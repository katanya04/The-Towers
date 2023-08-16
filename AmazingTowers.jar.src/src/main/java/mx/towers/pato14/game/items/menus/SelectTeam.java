package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.ChestInventoryItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SelectTeam extends ChestInventoryItem {
    public SelectTeam(GameInstance gameInstance) {
        super(
                Utils.setName(new ItemStack(Material.WOOL, 1, (short) 14), AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.selectTeam.name"))),
                JoinTeamItem.createAllTeams(gameInstance)
        );
    }

    public void updateMenu() {
        this.setContents(this.getContents());
    }

    public JoinTeamItem getItemByTeam(TeamColor team) {
        for (ItemStack item : getContents()) {
            if (item instanceof JoinTeamItem && ((JoinTeamItem) item).getTeamColor() == team)
                return (JoinTeamItem) item;
        }
        return null;
    }
}
