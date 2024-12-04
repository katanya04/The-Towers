package mx.towers.pato14;

import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.items.Items;
import mx.towers.pato14.utils.items.ItemsEnum;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LobbyInstance extends TowersWorldInstance {
    public enum LobbyItems {JOIN_GAME, QUIT}
    protected final ItemStack lobbyParkourPrize;
    public LobbyInstance(String name) {
        super(name, LobbyInstance.class);
        this.lobbyParkourPrize = new ItemStack(
                Utils.colorArmor(Utils.addGlint(Utils.setLore(Utils.setName(new ItemStack(Material.LEATHER_CHESTPLATE),
                Utils.getColor("&r" + this.getConfig(ConfigType.MESSAGES).getString("lobbyParkourPrize.name"))),
                (Utils.getColor(this.getConfig(ConfigType.MESSAGES).getStringList("lobbyParkourPrize.lore"))))), Color.RED));
        state = State.READY;
    }
    @Override
    public void joinInstance(Player player) {
        super.joinInstance(player);
        joinMainLobby(player);
    }

    public static void joinMainLobby(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        Utils.resetPlayer(player);
        AmazingTowers.getLobby().getHotbar().apply(player);
    }

    public ItemStack getLobbyParkourPrize() {
        return lobbyParkourPrize;
    }
    @Override
    public void setHotbarItems() {
        Config lobbyConfig = AmazingTowers.getLobby().getConfig(ConfigType.CONFIG);
        hotbarItems.setItem(lobbyConfig.getInt("lobbyItems.selectGame.position"), Items.get(ItemsEnum.GAME_SELECT));
        if (AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled"))
            hotbarItems.setItem(lobbyConfig.getInt("lobbyItems.quit.position"), Items.get(ItemsEnum.QUIT_LOBBY));
    }
}