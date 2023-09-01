package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.game.items.menus.BuyKitMenu;
import mx.towers.pato14.game.items.ChestMenuItem;
import mx.towers.pato14.game.items.menus.settings.GetStringFromItems;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UseLobbyItems implements Listener {

    private boolean canUseLobbyItem(TowersWorldInstance instance) {
        return instance != null && (instance instanceof LobbyInstance || ((GameInstance) instance).getGame() != null);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getItem() == null || !(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;
        Player player = e.getPlayer();
        TowersWorldInstance instance = AmazingTowers.getInstance(player);
        if (instance instanceof GameInstance && (((GameInstance) (instance)).getGame() == null || ((GameInstance) (instance)).getGame().getGameState().equals(GameState.FINISH)))
            return;
        for (ItemStack item : instance.getHotbarItems().getHotbarItems().values()) {
            if (!item.equals(e.getItem()) || !(item instanceof ActionItem))
                continue;
            ((ActionItem) item).interact(player, instance);
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR))
            return;
        HumanEntity player = e.getWhoClicked();
        TowersWorldInstance instance = AmazingTowers.getInstance(player);
        if (!canUseLobbyItem(instance))
            return;
        if (!player.isOp() && instance instanceof LobbyInstance && (e.getClickedInventory().getHolder() instanceof Chest || e.getClickedInventory().getHolder() instanceof DoubleChest)) {
            e.setCancelled(true);
            return;
        }
        if (player.getOpenInventory().getTopInventory() instanceof AnvilInventory) {
            String path = player.getOpenInventory().getTopInventory().getItem(0).getItemMeta().getLore().get(0).replace("§r§8", "");
            if (!Utils.isValidPath(path))
                return;
            e.setCancelled(true);
            InventoryView view = e.getView();
            String[] pathSplit = path.split(";");
            if (pathSplit.length != 2) {
                Utils.sendMessage("Error while changing game setting, bad path", MessageType.ERROR, player);
                return;
            }
            BookMenuItem currentMenu = instance.getHotbarItems().getBookMenu(pathSplit[0] + ";" + pathSplit[1].split("\\.")[0]);
            String newValue = null;
            if (e.getClickedInventory() instanceof AnvilInventory) {
                int rawSlot = e.getRawSlot();
                if (rawSlot != view.convertSlot(rawSlot) || rawSlot != 2 || !(e.getClickedInventory().getItem(1) == null ||
                                e.getClickedInventory().getItem(1).getType() == Material.AIR) ||
                        e.getClickedInventory().getItem(0).getType() != Material.PAPER || clickedItem.getType() != Material.PAPER)
                    return;
                newValue = clickedItem.getItemMeta().getDisplayName();
                e.getClickedInventory().setItem(0, new ItemStack(Material.AIR));
            } else {
                for (ItemStack itemStack : currentMenu.getAuxiliarContents().values()) {
                    if (!itemStack.equals(clickedItem))
                        continue;
                    if (itemStack instanceof GetStringFromItems)
                        newValue = ((GetStringFromItems) itemStack).getItems(player);
                }
                currentMenu.removeAuxiliarItemsFromPlayer(player);
                if (newValue == null)
                    newValue = AmazingTowers.getPlugin().getNms().serializeItemStack(clickedItem);
                else if (newValue.equals("<skip_item>"))
                    return;
            }
            Config settings = instance.getConfig(ConfigType.valueOf(Utils.camelCaseToMacroCase(pathSplit[0])));
            Object currentValue = settings.get(pathSplit[1]);
            if (currentValue instanceof String) {
                settings.set(pathSplit[1], newValue);
                if (instance instanceof GameInstance)
                    currentMenu.updateSettings((GameInstance) (instance), path);
            } else if (currentValue instanceof List) { //add String case by default
                List<String> value = settings.getStringList(pathSplit[1]);
                value.add(newValue);
                settings.set(pathSplit[1], value);
                if (instance instanceof GameInstance)
                    currentMenu.updateSettings((GameInstance) (instance), path + ";" + newValue + " add");
            } else if (currentValue instanceof ConfigurationSection) { //add String case by default
                ConfigurationSection configSection = settings.getConfigurationSection(pathSplit[1]);
                if (configSection.getValues(false).containsKey(newValue)) {
                    Utils.sendMessage("That key already exists in the map", MessageType.ERROR, player);
                    player.closeInventory();
                    return;
                }
                Optional<Map.Entry<String, Object>> randomEntry = configSection.getValues(false).entrySet().stream().findAny();
                if (randomEntry.isPresent())
                    configSection.createSection(newValue, ((ConfigurationSection) randomEntry.get().getValue()).getValues(false));
                else {
                    Optional<Map.Entry<String, Object>> randomDefaultEntry = configSection.getDefaultSection().getValues(false).entrySet().stream().findAny();
                    if (randomDefaultEntry.isPresent())
                        configSection.createSection(newValue, ((ConfigurationSection) randomDefaultEntry.get().getValue()).getValues(false));
                    else
                        configSection.createSection(newValue);
                }
                if (instance instanceof GameInstance)
                    currentMenu.updateSettings((GameInstance) (instance), path + "." + newValue + " add");
            }
            currentMenu.openMenu(player);
            return;
        }
        if (!instance.getHotbarItems().isALobbyItem(clickedItem, e.getClickedInventory()))
            return;
        e.setCancelled(true);
        for (ChestMenuItem inventory : instance.getHotbarItems().getChestMenus()) {
            if (inventory instanceof BuyKitMenu && inventory.getMenu().getViewers().isEmpty()) {
                instance.getHotbarItems().getChestMenus().remove(inventory);
                System.out.println("Removed inventory");
            }
            if (!inventory.getMenu().equals(e.getClickedInventory()))
                continue;
            for (ItemStack item : inventory.getContents().values()) {
                if (!item.equals(clickedItem))
                    continue;
                if (item instanceof ActionItem) {
                    ((ActionItem) item).interact(player, instance);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        if (!(e.getInventory() instanceof AnvilInventory))
            return;
        if (!(e.getInventory().getItem(1) == null || e.getInventory().getItem(1).getType() == Material.AIR) ||
                e.getInventory().getItem(0) == null || e.getInventory().getItem(0).getType() != Material.PAPER)
            return;
        String path = e.getInventory().getItem(0).getItemMeta().getLore().get(0).replace("§r§8", "");
        if (!Utils.isValidPath(path))
            return;
        e.getInventory().setItem(0, new ItemStack(Material.AIR));
        String[] pathSplit = path.split(";");
        BookMenuItem currentMenu = AmazingTowers.getGameInstance(e.getPlayer()).getHotbarItems().getBookMenu(pathSplit[0] + ";" + pathSplit[1].split("\\.")[0]);
        if (currentMenu.getAuxiliarContents() == null || currentMenu.getAuxiliarContents().isEmpty())
            return;
        currentMenu.removeAuxiliarItemsFromPlayer(e.getPlayer());
    }
}