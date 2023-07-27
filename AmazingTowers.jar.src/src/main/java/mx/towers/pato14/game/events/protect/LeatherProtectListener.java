package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class LeatherProtectListener implements Listener {
    private final AmazingTowers plugin;

    public LeatherProtectListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrops(PlayerDropItemEvent e) {
        ItemStack i = e.getItemDrop().getItemStack();
        if (this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.CONFIG).getBoolean("Options.protect_leatherArmor") && (
                i.getType() == Material.LEATHER_HELMET || i.getType() == Material.LEATHER_CHESTPLATE || i.getType() == Material.LEATHER_LEGGINGS || i.getType() == Material.LEATHER_BOOTS)) {
            e.getItemDrop().remove();
        }
        if (i.getItemMeta().getDisplayName() == null || i.getType() == null) {
            return;
        }
        Game game = this.plugin.getGameInstance(e.getPlayer()).getGame();
        for (TeamColor teamColor : TeamColor.values()) {
            if (game.getLobbyItems().checkIfItemIsJoinTeamItem(i, teamColor))
                e.setCancelled(true);
        }
        if (game.getItemBook() != null && i.getItemMeta().getDisplayName().equals(game.getItemBook().getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        } else if (game.getLobbyItems().getItemQuit() != null && i.getItemMeta().getDisplayName().equals(game.getLobbyItems().getItemQuit().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (this.plugin.getGameInstance(e.getWhoClicked()).getConfig(ConfigType.CONFIG).getBoolean("Options.protect_leatherArmor")) {
            Material i = e.getCurrentItem().getType();
            if (!InventoryType.CRAFTING.equals(e.getInventory().getType()) && !InventoryType.CREATIVE.equals(e.getInventory().getType()) && !InventoryType.PLAYER.equals(e.getInventory().getType()) && (i == Material.LEATHER_HELMET || i == Material.LEATHER_CHESTPLATE || i == Material.LEATHER_LEGGINGS || i == Material.LEATHER_BOOTS))
                e.setCancelled(true);
        }
    }
}


