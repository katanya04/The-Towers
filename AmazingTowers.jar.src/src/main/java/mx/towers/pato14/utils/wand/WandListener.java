package mx.towers.pato14.utils.wand;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.Tool;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getItem() == null || !Tool.WAND.checkIfItemIsTool(e.getItem()))
            return;
        e.setCancelled(true);
        if (AmazingTowers.getWandCoords(player) == null)
            AmazingTowers.addPlayerWand(player);
        Location loc = e.getClickedBlock().getState().getBlock().getLocation();
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (loc.equals(AmazingTowers.getWandCoords(player).getPos1())) {
                e.getPlayer().sendMessage("§7(§cAT§7) §cThe location in this block already exists in Pos1");
            } else {
                AmazingTowers.getWandCoords(player).setPos1(loc);
                e.getPlayer().sendMessage("§7(§aAT§7) §fFirst position set to §a(x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ() + ")");
            }
        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (loc.equals(AmazingTowers.getWandCoords(player).getPos2())) {
                e.getPlayer().sendMessage("§7(§cAT§7) §cThe location in this block already exists in Pos2");
            } else {
                AmazingTowers.getWandCoords(player).setPos2(loc);
                e.getPlayer().sendMessage("§7(§aAT§7) §fSecond position set to §a(x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ() + ")");
            }
        }
    }
}


