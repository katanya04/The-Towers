package mx.towers.pato14;

import mx.towers.pato14.game.items.LobbyItems;
import mx.towers.pato14.game.items.LobbyParkourPrize;

public class LobbyInstance extends TowersWorldInstance {
    private final LobbyParkourPrize lobbyParkourPrize;
    public LobbyInstance(String name) {
        super(name, LobbyInstance.class);
        this.hotbarItems = new LobbyItems(this);
        this.lobbyParkourPrize = new LobbyParkourPrize(this);
    }
    @Override
    public LobbyItems getHotbarItems() {
        return (LobbyItems) hotbarItems;
    }

    public LobbyParkourPrize getLobbyParkourPrize() {
        return lobbyParkourPrize;
    }
}
