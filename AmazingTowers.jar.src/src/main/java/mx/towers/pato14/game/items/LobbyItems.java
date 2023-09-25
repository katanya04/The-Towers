package mx.towers.pato14.game.items;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.game.items.actions.BungeecordQuit;
import mx.towers.pato14.game.items.menus.SelectGameMenu;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;

public class LobbyItems extends HotbarItems {
    private final SelectGameMenu selectGameMenu;
    public LobbyItems(LobbyInstance lobbyInstance) {
        Config config = lobbyInstance.getConfig(ConfigType.CONFIG);
        this.hotbarItems.put(config.getInt("lobbyItems.selectGame.position"), (this.selectGameMenu = new SelectGameMenu(lobbyInstance)));
        if (AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
            this.hotbarItems.put(config.getInt("lobbyItems.quit.position"), new BungeecordQuit(lobbyInstance));
        }
    }
    public SelectGameMenu getSelectGameMenu() {
        return selectGameMenu;
    }
}