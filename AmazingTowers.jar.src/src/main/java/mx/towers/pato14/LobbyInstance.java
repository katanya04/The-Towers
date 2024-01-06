package mx.towers.pato14;

import mx.towers.pato14.game.items.LobbyItems;
import mx.towers.pato14.game.items.LobbyParkourPrize;
import mx.towers.pato14.utils.Utils;
import org.bukkit.entity.Player;

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
    @Override
    public void joinInstance(Player player) {
        super.joinInstance(player);
        Utils.joinMainLobby(player);
    }
    public LobbyParkourPrize getLobbyParkourPrize() {
        return lobbyParkourPrize;
    }
}