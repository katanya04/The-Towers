package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.MenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SelectTeam extends MenuItem {
    public SelectTeam(Game game) {
        super(
                Utils.setName(new ItemStack(Material.WOOL, 1, (short) 14), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.selectTeam.name"))),
                game.getTeams().getLobbyItems()
        );
    }
}
