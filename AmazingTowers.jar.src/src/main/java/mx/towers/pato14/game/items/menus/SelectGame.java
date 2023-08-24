package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SelectGame extends ActionItem {
    private final GameInstance gameInstance;
    private static SelectGame[] instances;
    protected SelectGame(GameInstance gameInstance, LobbyInstance lobbyInstance) {
        super(Utils.setLore(Utils.setName(new ItemStack(Material.EMPTY_MAP),
                gameInstance.getConfig(ConfigType.CONFIG).getString("name") == null ? "§r§a" + gameInstance.getName() : AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("name"))),
                "§f" + gameInstance.getNumPlayers() + " " + lobbyInstance.getConfig(ConfigType.MESSAGES)
                                .getString(gameInstance.getNumPlayers() == 1 ? "player" : "players")));
        this.gameInstance = gameInstance;
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        super.interact(player, instance);
        if (this.gameInstance.canJoin(player)) {
            if (Bukkit.getWorld(this.gameInstance.getName()) == null)
                new WorldCreator(gameInstance.getName()).createWorld();
            Utils.tpToWorld(Bukkit.getWorld(this.gameInstance.getName()), (Player) player);
        } else {
            Utils.sendMessage(AmazingTowers.getColor(instance.getConfig(ConfigType.MESSAGES).getString("canNotJoinGame")), MessageType.ERROR, player);
        }

    }

    public static SelectGame[] getItems(LobbyInstance lobby) {
        return instances == null ? (instances = AmazingTowers.getGameInstances().values().stream().map(o -> new SelectGame(o, lobby)).toArray(SelectGame[]::new))
                : instances;
    }

    public void update(int numPlayers) {
        Utils.setLore(this, "§f" + numPlayers + " " + AmazingTowers.getLobby().getConfig(ConfigType.MESSAGES)
                        .getString(numPlayers == 1 ? "player" : "players"));
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }
}
