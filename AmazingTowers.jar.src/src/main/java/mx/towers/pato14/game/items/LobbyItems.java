package mx.towers.pato14.game.items;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.menus.BungeecordQuit;
import mx.towers.pato14.game.items.menus.ModifyGameSettings;
import mx.towers.pato14.game.items.menus.SelectKit;
import mx.towers.pato14.game.items.menus.SelectTeam;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyItems implements Listener {
    private final HashMap<Integer, ItemStack> hotbarItems;
    private final SelectTeam selectTeam;
    private final SelectKit selectKit;
    private BungeecordQuit quit;
    private final ModifyGameSettings modifyGameSettings;
    private final AmazingTowers plugin;

    public LobbyItems(Game game) {
        this.plugin = game.getGameInstance().getPlugin();
        this.hotbarItems = new HashMap<>();
        Config config = game.getGameInstance().getConfig(ConfigType.CONFIG);
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.selectTeam.position"), this.selectTeam = new SelectTeam(game.getGameInstance()));
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.selectKit.position"), this.selectKit = new SelectKit(game));
        if (getPlugin().getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
            this.quit = new BungeecordQuit(game);
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

    public ModifyGameSettings getModifyGameSettings() {
        return modifyGameSettings;
    }

    public List<ChestMenuItem> getChestMenus() {
        List<ChestMenuItem> toret = new ArrayList<>();
        for (ItemStack hotbarItem : hotbarItems.values()) {
            recursiveSearchChestMenus(toret, hotbarItem);
        }
        return toret;
    }

    public BookMenuItem getBookMenu(String path) {
        BookMenuItem toret = null;
        for (ItemStack hotbarItem : hotbarItems.values()) {
            toret = recursiveSearchBookMenu(hotbarItem, path);
            if (toret != null) return toret;
        }
        return toret;
    }

    private void recursiveSearchChestMenus(List<ChestMenuItem> list, ItemStack item) {
        if (!(item instanceof ChestMenuItem))
            return;
        list.add(((ChestMenuItem) item));
        for (ItemStack item2 : ((ChestMenuItem) item).getContents().values())
            recursiveSearchChestMenus(list, item2);
    }

    private BookMenuItem recursiveSearchBookMenu(ItemStack item, String path) {
        BookMenuItem toret;
        if (item instanceof BookMenuItem && ((BookMenuItem) item).getFullPath().equals(path)) {
            return (BookMenuItem) item;
        } else if (item instanceof ChestMenuItem) {
            for (ItemStack item2 : ((ChestMenuItem) item).getContents().values()) {
                toret = recursiveSearchBookMenu(item2, path);
                if (toret != null) return toret;
            }
        }
        return null;
    }
}