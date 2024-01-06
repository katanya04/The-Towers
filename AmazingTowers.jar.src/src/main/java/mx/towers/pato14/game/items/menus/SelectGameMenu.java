package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.game.items.ChestMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SelectGameMenu extends ChestMenuItem {
    private final SelectGame[] games;
    public SelectGameMenu(LobbyInstance lobbyInstance) {
        super(Utils.setName(new ItemStack(Material.COMPASS),
                Utils.getColor(lobbyInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.selectGame.name"))),
                SelectGame.getItems(lobbyInstance));
        games = SelectGame.getItems(lobbyInstance);
    }
    public void updateMenu(GameInstance gameInstance) {
        for (SelectGame selectGame : games) {
            if (selectGame.getGameInstance().equals(gameInstance))
                selectGame.update(gameInstance.getNumPlayers());
        }
        updateMenu();
    }
}
