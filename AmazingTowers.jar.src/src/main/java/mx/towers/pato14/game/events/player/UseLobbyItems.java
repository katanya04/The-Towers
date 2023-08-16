package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.game.items.menus.BuyKitMenu;
import mx.towers.pato14.game.items.ChestInventoryItem;
import mx.towers.pato14.utils.enums.*;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UseLobbyItems implements Listener {
    AmazingTowers plugin = AmazingTowers.getPlugin();
    private boolean canUseLobbyItem(GameInstance gameInstance) {
        return gameInstance != null && gameInstance.getGame() != null;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        GameInstance gameInstance = this.plugin.getGameInstance(player);
        if (!canUseLobbyItem(gameInstance))
            return;
        Game game = gameInstance.getGame();
        if (game.getGameState().equals(GameState.FINISH))
            return;
        if (e.getItem() == null || !(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;
        for (ItemStack item : game.getLobbyItems().getHotbarItems().values()) {
            if (!item.equals(e.getItem()) || !(item instanceof ActionItem))
                continue;
            ((ActionItem) item).interact(player, gameInstance);
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
        GameInstance gameInstance = this.plugin.getGameInstance(player);
        if (!canUseLobbyItem(gameInstance))
            return;
        Game game = gameInstance.getGame();
        if (!game.getLobbyItems().isALobbyItem(clickedItem, e.getClickedInventory()))
            return;
        e.setCancelled(true);
        for (ChestInventoryItem inventory : game.getLobbyItems().getInventories()) {
            if (inventory instanceof BuyKitMenu && inventory.getMenu().getViewers().isEmpty()) {
                game.getLobbyItems().getInventories().remove(inventory);
                System.out.println("Removed inventory");
            }
            if (!inventory.getMenu().equals(e.getClickedInventory()))
                continue;
            for (ItemStack item : inventory.getContents()) {
                if (!item.equals(clickedItem))
                    continue;
                if (item instanceof ActionItem) {
                    ((ActionItem) item).interact(player, gameInstance);
                    return;
                }
            }
        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getWhoClicked());
        if (!canUseLobbyItem(gameInstance))
            return;
        if (e.getWhoClicked().getGameMode().equals(GameMode.ADVENTURE)) {
            e.setCancelled(true);
            return;
        }
        Game game = gameInstance.getGame();
        for (ItemStack item : game.getLobbyItems().getHotbarItems().values()) {
            if ((item instanceof ChestInventoryItem && ((ChestInventoryItem) item).getMenu().equals(e.getInventory()))
                || item.equals(e.getCursor())) {
                e.setCancelled(true);
                return;
            }
        }
    }
}