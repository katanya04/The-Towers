package mx.towers.pato14.game.items;

import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.game.items.menus.HotbarItems;
import mx.towers.pato14.game.items.menus.SelectGameMenu;
import mx.towers.pato14.utils.enums.ConfigType;

public class LobbyItems extends HotbarItems {
    private final SelectGameMenu selectGameMenu;
    public LobbyItems(LobbyInstance lobbyInstance) {
        this.hotbarItems.put(lobbyInstance.getConfig(ConfigType.CONFIG).getInt("lobbyItems.selectGame.position"),
                (this.selectGameMenu = new SelectGameMenu(lobbyInstance)));
    }
    public SelectGameMenu getSelectGameMenu() {
        return selectGameMenu;
    }
}