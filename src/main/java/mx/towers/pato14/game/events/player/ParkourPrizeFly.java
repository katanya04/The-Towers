package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ParkourPrizeFly implements Listener {
    @EventHandler
    public void onPlayerInteract(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        TowersWorldInstance instance = AmazingTowers.getInstance(player);
        if (!(instance instanceof LobbyInstance))
            return;
        ItemStack prize = ((LobbyInstance) instance).getLobbyParkourPrize();
        if (player.getGameMode() != GameMode.ADVENTURE)
            return;
        (new BukkitRunnable() {
            @Override
            public void run() {
                player.setAllowFlight(prize.equals(player.getInventory().getArmorContents()[2]));
            }
        }).runTaskLater(AmazingTowers.getPlugin(), 5L);
    }
}
