package mx.towers.pato14.utils.cofresillos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Tool;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SelectCofresillos implements Listener {
    private final AmazingTowers plugin;

    public SelectCofresillos(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSelectorCofressillos(PlayerInteractEvent e) {
        if (!Tool.REFILLCHEST.checkIfItemIsTool(e.getItem()))
            return;
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_AIR))
            return;
        Block block = e.getClickedBlock().getState().getBlock();
        if (!block.getType().equals(Material.CHEST))
            return;
        Location loc = block.getLocation();
        Config locations = this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.LOCATIONS);
        e.setCancelled(true);
        List<String> locConfig = locations.getStringList("LOCATIONS.REFILLCHEST");
        String locString = Locations.getLocationStringBlock(loc);
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (locConfig.contains(locString)) {
                e.getPlayer().sendMessage("§7(§cAT§7) §cThe location in this block already exist in Config!");
            } else {
                locConfig.add(locString);
                locations.set("LOCATIONS.REFILLCHEST", locConfig);
                locations.saveConfig();
                e.getPlayer().sendMessage("§7(§aAT§7) §fSelected position of the chest set to §a(x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ() + ")");
            }
        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (locConfig.contains(locString)) {
                locConfig.remove(locString);
                locations.set("LOCATIONS.REFILLCHEST", locConfig);
                locations.saveConfig();
                e.getPlayer().sendMessage("§7(§cAT§7) §cThe location of this block was removed in Config successfully!");
            } else {
                e.getPlayer().sendMessage("§7(§cAT§7) §cThe location of this block doesn't exist in Config!");
            }
        }
    }

    public static Map<Location, FixedItem[]> makelist(Config conf, String path) {
        Map<Location, FixedItem[]> chs = new HashMap<>();
        List<String> Chests = conf.getStringList(path);
        for (String st : Chests) {
            Location loc = Locations.getLocationFromString(st);
            Location locBlock = new Location(loc.getWorld(), loc.getBlock().getX(), loc.getBlock().getY(), loc.getBlock().getZ());
            if (locBlock.getBlock().getType() == Material.CHEST) {
                Chest ch = (Chest) locBlock.getBlock().getState();
                FixedItem[] i = FixedItem.itemStackToFixedItem(ch.getInventory().getContents().clone());
                chs.put(locBlock, i);
            }
        }
        return chs;
    }

    public static void refill(Map<Location, FixedItem[]> Chests) {
        for (Location ch : Chests.keySet()) {
            if (ch.getBlock().getType() == Material.CHEST) {
                Chest cht = (Chest) ch.getBlock().getState();
                cht.getInventory().setContents(FixedItem.fixedItemToItemStack(Chests.get(ch)));
            }
        }
    }
}


