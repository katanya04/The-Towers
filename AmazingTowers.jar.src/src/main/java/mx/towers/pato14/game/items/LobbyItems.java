package mx.towers.pato14.game.items;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.menus.ModifyGameSettings;
import mx.towers.pato14.game.items.menus.SelectKit;
import mx.towers.pato14.game.items.menus.SelectTeam;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LobbyItems implements Listener {
    private final HashMap<Integer, ItemStack> hotbarItems;
    private final SelectTeam selectTeam;
    private final SelectKit selectKit;
    private ItemStack quit;
    private final ModifyGameSettings modifyGameSettings;
    private final Game game;
    private final AmazingTowers plugin;

    public LobbyItems(Game game) {
        this.game = game;
        this.plugin = game.getGameInstance().getPlugin();
        this.hotbarItems = new HashMap<>();
        Config config = game.getGameInstance().getConfig(ConfigType.CONFIG);
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.selectTeam.position"), this.selectTeam = new SelectTeam(game));
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.selectKit.position"), this.selectKit = new SelectKit(game));
        if (getPlugin().getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
            this.quit = Utils.setName(new ItemStack(Material.BED), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.quit.name")));
            this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.quit.position"), this.quit);
        }
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.modifyGameSettings.position"), this.modifyGameSettings = new ModifyGameSettings(game));
    }

    public void giveHotbarItems(HumanEntity player) {
        for (Map.Entry<Integer, ItemStack> item : this.hotbarItems.entrySet()) {
            if (item.getValue().equals(this.modifyGameSettings) && !player.hasPermission("towers.organizer"))
                continue;
            player.getInventory().setItem(item.getKey(), item.getValue());
        }
    }

    public boolean isALobbyItem(ItemStack itemStack, Inventory inventory) {
        if (inventory.equals(selectTeam.getMenu()) || inventory.equals(selectKit.getMenu()) || inventory.equals(modifyGameSettings.getMenu()) || inventory.equals(modifyGameSettings.getSetRules().getMenu()))
            return true;
        for (ItemStack item : this.hotbarItems.values()) {
            if (item.equals(itemStack))
                return true;
        }
        return false;
    }

    public ItemStack getItemQuit() {
        return this.quit;
    }

    private AmazingTowers getPlugin() {
        return this.plugin;
    }

    public SelectTeam getSelectTeam() {
        return selectTeam;
    }

    public SelectKit getSelectKit() {
        return selectKit;
    }

    public HashMap<Integer, ItemStack> getHotbarItems() {
        return hotbarItems;
    }

    public ItemStack getQuitItem() {
        return quit;
    }

    public void updateTeamsMenu() {
        this.selectTeam.setContents(game.getTeams().getLobbyItems());
    }

    public ModifyGameSettings getModifyGameSettings() {
        return modifyGameSettings;
    }
}