package mx.towers.pato14.utils.wand;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Tool;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener implements Listener {
    private final AmazingTowers plugin;

    public WandListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null)
            return;
        if (e.getItem().getType().equals(Tool.WAND.getItem().getType()) && e.getItem().getItemMeta().getDisplayName().equals(Tool.WAND.getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
            Block block = e.getClickedBlock().getState().getBlock();
            Location loc = block.getLocation();
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (this.plugin.getWand().equalsPos1(Locations.getLocationStringBlock(loc))) {
                    e.getPlayer().sendMessage("§7(§cAT§7) §cThe location in this block already exists in Pos1");
                } else {
                    this.plugin.getWand().setPos1(Locations.getLocationStringBlock(loc));
                    e.getPlayer().sendMessage("§7(§aAT§7) §fFirst position set to §a(x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ() + ")");
                }
            } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (this.plugin.getWand().equalsPos2(Locations.getLocationStringBlock(loc))) {
                    e.getPlayer().sendMessage("§7(§cAT§7) §cThe location in this block already exists in Pos2");
                } else {
                    this.plugin.getWand().setPos2(Locations.getLocationStringBlock(loc));
                    e.getPlayer().sendMessage("§7(§aAT§7) §fSecond position set to §a(x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ() + ")");
                }
            }
        }
    }
}


