package mx.towers.pato14;

import mx.towers.pato14.game.items.LobbyItems;

public class LobbyInstance extends TowersWorldInstance {
    public LobbyInstance(String name) {
        super(name, LobbyInstance.class);
        this.hotbarItems = new LobbyItems(this);
    }
    @Override
    public LobbyItems getHotbarItems() {
        return (LobbyItems) hotbarItems;
    }
}
