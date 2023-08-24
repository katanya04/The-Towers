package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.GameMode;
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
        GameInstance gameInstance = this.plugin.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        ItemStack i = e.getItemDrop().getItemStack();
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.canNotDropLeatherArmor") && Utils.isLeatherArmor(i.getType()))
            e.getItemDrop().remove();
        if (i.getItemMeta().getDisplayName() == null || i.getType() == null)
            return;
        if (e.getPlayer().getGameMode() == GameMode.ADVENTURE)
            e.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getWhoClicked());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
            return;
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.canNotDropLeatherArmor")) {
            if (!InventoryType.CRAFTING.equals(e.getInventory().getType()) && !InventoryType.CREATIVE.equals(e.getInventory().getType()) && !InventoryType.PLAYER.equals(e.getInventory().getType()) && Utils.isLeatherArmor(e.getCurrentItem().getType()))
                e.setCancelled(true);
        }
    }
}


