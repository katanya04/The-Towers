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

import java.util.Arrays;

public class SelectGame extends ActionItem {
    private final String instanceName;
    private static SelectGame[] instances;
    protected SelectGame(GameInstance gameInstance, LobbyInstance lobbyInstance) {
        super(Utils.setLore(Utils.setName(new ItemStack(Material.EMPTY_MAP),
                gameInstance.getConfig(ConfigType.CONFIG).getString("name") == null ? "§r§a" + gameInstance.getInternalName() : "§r" + Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("name"))),
                "§f" + gameInstance.getNumPlayers() + " " + lobbyInstance.getConfig(ConfigType.MESSAGES)
                                .getString(gameInstance.getNumPlayers() == 1 ? "player" : "players")));
        this.instanceName = gameInstance.getInternalName();
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        super.interact(player, instance);
        GameInstance gameInstance = AmazingTowers.getGameInstance(this.instanceName);
        if (gameInstance.canJoin(player)) {
            if (Bukkit.getWorld(gameInstance.getInternalName()) == null)
                new WorldCreator(gameInstance.getInternalName()).createWorld();
            Utils.tpToWorld(Bukkit.getWorld(gameInstance.getInternalName()), (Player) player);
        } else {
            Utils.sendMessage(Utils.getColor(instance.getConfig(ConfigType.MESSAGES).getString("canNotJoinGame")), MessageType.ERROR, player);
        }
    }

    public static SelectGame[] getItems(LobbyInstance lobby) {
        return instances == null ? (instances = Arrays.stream(AmazingTowers.getGameInstances()).filter(o -> o.getGame() != null)
                .map(o -> new SelectGame(o, lobby)).toArray(SelectGame[]::new))
                : instances;
    }

    public void update(int numPlayers) {
        Utils.setLore(this, "§f" + numPlayers + " " + AmazingTowers.getLobby().getConfig(ConfigType.MESSAGES)
                        .getString(numPlayers == 1 ? "player" : "players"));
    }

    public GameInstance getGameInstance() {
        return AmazingTowers.getGameInstance(this.instanceName);
    }
}
