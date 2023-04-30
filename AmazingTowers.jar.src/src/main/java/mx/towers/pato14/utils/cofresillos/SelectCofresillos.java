package mx.towers.pato14.utils.cofresillos;

import java.util.HashMap;
import java.util.List;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SelectCofresillos implements Listener {
    private AmazingTowers plugin;

    public SelectCofresillos(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSelectorCofressillos(PlayerInteractEvent e) {
        if (e.getItem() != null && e.getItem().getType() == Material.IRON_SPADE && e.getItem().getItemMeta().getDisplayName() == "§aSelect and Remove Chest Refill") {
            Block block = e.getClickedBlock().getState().getBlock();
            Location loc = block.getLocation();
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (block.getType() == Material.CHEST) {
                    e.setCancelled(true);
                    List<String> locConfig = this.plugin.getLocations().getStringList("LOCATIONS.REFILLCHEST");
                    String locString = Locations.getLocationStringBlock(loc);
                    if (locConfig.contains(locString)) {
                        e.getPlayer().sendMessage("§7(§cAT§7) §cThe location in this block already exist in Config!");
                    } else {
                        locConfig.add(locString);
                        this.plugin.getLocations().set("LOCATIONS.REFILLCHEST", locConfig);
                        this.plugin.getLocations().saveConfig();
                        e.getPlayer().sendMessage("§7(§aAT§7) §fSelected position the chest set to §a(x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ() + ")");
                    }
                }
            } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                    block.getType() == Material.CHEST) {
                e.setCancelled(true);
                List<String> locConfig = this.plugin.getLocations().getStringList("LOCATIONS.REFILLCHEST");
                String locString = Locations.getLocationStringBlock(loc);
                if (locConfig.contains(locString)) {
                    locConfig.remove(locString);
                    this.plugin.getLocations().set("LOCATIONS.REFILLCHEST", locConfig);
                    this.plugin.getLocations().saveConfig();
                    e.getPlayer().sendMessage("§7(§cAT§7) §cThe location in this block removed in Config successfully!");
                } else {
                    e.getPlayer().sendMessage("§7(§cAT§7) §cThe location in this block not exist in Config!");
                }
            }
        }
    }

    public static HashMap<Location, FixedItem[]> makelist(Config conf, String path) {
        HashMap<Location, FixedItem[]> chs = (HashMap) new HashMap<>();
        List<String> Chests = conf.getStringList(path);
        for (String st : Chests) {
            Location loc = Locations.getLocationFromString(st);
            Location locBlock = new Location(loc.getWorld(), loc.getBlock().getX(), loc.getBlock().getY(), loc.getBlock().getZ());
            if (locBlock.getBlock().getType() == Material.CHEST) {
                Chest ch = (Chest) locBlock.getBlock().getState();
                FixedItem[] i = FixedItem.getArrayoBobin((ItemStack[]) ch.getInventory().getContents().clone());
                chs.put(locBlock, i);
            }
        }
        return chs;
    }

    public static void refill(HashMap<Location, FixedItem[]> Chests) {
        for (Location ch : Chests.keySet()) {
            if (ch.getBlock().getType() == Material.CHEST) {
                Chest cht = (Chest) ch.getBlock().getState();
                cht.getInventory().setContents(FixedItem.getAGalloConTennis(Chests.get(ch)));
            }
        }
    }
}


