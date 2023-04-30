package mx.towers.pato14.game.events.protect;

import java.util.ArrayList;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.plugin.PluginA;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CofresillosListener implements Listener {
    private AmazingTowers plugin;
    private static ArrayList<Location> protectedChest = new ArrayList<>();
    private ArrayList<Player> chestPlayerProtect = new ArrayList<>();

    public CofresillosListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        ArrayList<Block> end = new ArrayList<>(e.blockList());
        for (Block bl : e.blockList()) {
            if (bl.getType() == Material.CHEST) {
                if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.CHESTPROTECTBLUE1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.CHESTPROTECTBLUE2), bl.getLocation())) {
                    end.remove(bl);
                    continue;
                }
                if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.CHESTPROTECTRED1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.CHESTPROTECTRED2), bl.getLocation())) {
                    end.remove(bl);
                }
            }
        }
        e.blockList().clear();
        e.blockList().addAll(end);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block bl = e.getBlock();
        if (bl.getType() == Material.CHEST &&
                protectedChest.contains(bl.getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractChest(PlayerInteractEvent e) {
        if (this.plugin.getConfig().getBoolean("Options.chestsTeam")) {
            if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.CHEST) {
                return;
            }
            if (e.getClickedBlock().getType() == Material.CHEST &&
                    protectedChest.contains(e.getClickedBlock().getLocation())) {
                if (this.plugin.getGame().getTeams().getBlue().containsPlayer(e.getPlayer().getName())) {
                    if (Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.CHESTPROTECTRED1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.CHESTPROTECTRED2), e.getClickedBlock().getLocation())) {
                        this.chestPlayerProtect.add(e.getPlayer());
                    }
                } else if (this.plugin.getGame().getTeams().getRed().containsPlayer(e.getPlayer().getName()) &&
                        Cuboide.InCuboide(Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.CHESTPROTECTBLUE1), Locations.getLocationFromStringConfig(this.plugin.getLocations(), Locationshion.CHESTPROTECTBLUE2), e.getClickedBlock().getLocation())) {
                    this.chestPlayerProtect.add(e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onOpenChest(InventoryOpenEvent e) {
        if (this.plugin.getConfig().getBoolean("Options.chestsTeam") &&
                this.chestPlayerProtect.contains(e.getPlayer())) {
            e.setCancelled(true);
            this.chestPlayerProtect.remove(e.getPlayer());
            e.getPlayer().sendMessage(this.plugin.getColor(this.plugin.getMessages().getString("messages.open_chest")));
        }
    }

    public static void getChests() {
        Chunk[] loadedChunks;
        for (int length = (loadedChunks = Bukkit.getWorld("TheTowers").getLoadedChunks()).length, i = 0; i < length; i++) {
            Chunk ch = loadedChunks[i];
            BlockState[] tileEntities;
            for (int length2 = (tileEntities = ch.getTileEntities()).length, j = 0; j < length2; j++) {
                BlockState bls = tileEntities[j];
                if ((bls.getType() == Material.CHEST && Cuboide.InCuboide(Locations.getLocationFromStringConfig((AmazingTowers.getPlugin()).getLocations(), Locationshion.CHESTPROTECTRED1), Locations.getLocationFromStringConfig((AmazingTowers.getPlugin()).getLocations(), Locationshion.CHESTPROTECTRED2), bls.getLocation())) ||
                        Cuboide.InCuboide(Locations.getLocationFromStringConfig((AmazingTowers.getPlugin()).getLocations(), Locationshion.CHESTPROTECTBLUE1), Locations.getLocationFromStringConfig((AmazingTowers.getPlugin()).getLocations(), Locationshion.CHESTPROTECTBLUE2), bls.getLocation()))
                    protectedChest.add(bls.getLocation());
            }
        }
    }
}


