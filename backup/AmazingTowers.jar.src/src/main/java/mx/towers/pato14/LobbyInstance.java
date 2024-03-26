package mx.towers.pato14;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import me.katanya04.anotherguiplugin.actionItems.MenuItem;
import me.katanya04.anotherguiplugin.menu.ChestMenu;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ItemsEnum;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
        Utils.joinMainLobby(player);
    }
    public ItemStack getLobbyParkourPrize() {
        return lobbyParkourPrize;
    }
    @Override
    public void setHotbarItems() {
        Config lobbyConfig = AmazingTowers.getLobby().getConfig(ConfigType.CONFIG);
        hotbarItems.setItem(lobbyConfig.getInt("lobbyItems.selectGame.position"), ActionItem.getByName(ItemsEnum.GAME_SELECT.name).returnPlaceholder());
        if (AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled"))
            hotbarItems.setItem(lobbyConfig.getInt("lobbyItems.quit.position"), ActionItem.getByName(ItemsEnum.QUIT_LOBBY.name).returnPlaceholder());
    }
}