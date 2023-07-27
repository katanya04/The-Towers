package mx.towers.pato14.game.events.protect;

import java.util.ArrayList;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CofresillosListener implements Listener {
    private final AmazingTowers plugin;
    private static final ArrayList<Location> protectedChest = new ArrayList<>();
    public CofresillosListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        ArrayList<Block> end = new ArrayList<>(e.blockList());
        GameInstance gameInstance = this.plugin.getGameInstance(e.getEntity());
        for (Block bl : e.blockList()) {
            if (bl.getType() == Material.CHEST) {
                if (Locations.isInsideBase(bl.getLocation(), gameInstance.getGame().getTeams()))
                    end.remove(bl);
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
        Player player = e.getPlayer();
        GameInstance gameInstance = this.plugin.getGameInstance(player);
        Block block = e.getClickedBlock();
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.chestsTeam")) {
            if (block == null || !block.getType().equals(Material.CHEST)) {
                return;
            }
            System.out.println("Interact chest");
            System.out.println(protectedChest.contains(block.getLocation()));
            if (protectedChest.contains(block.getLocation())) {
                TeamColor teamColor = gameInstance.getGame().getTeams().getTeamColorByPlayer(player);
                if (!Cuboide.InCuboide(gameInstance.getConfig(ConfigType.LOCATIONS).getStringList(mx.towers.pato14.utils.enums.Location.CHEST_PROTECT.getPath(teamColor)), block.getLocation())) {
                    e.setCancelled(true);
                    player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.open_chest")));
                }
            }
        }
    }

    public static void getChests(GameInstance gameInstance) {
        for (Chunk ch : gameInstance.getWorld().getLoadedChunks()) {
            for (BlockState bls : ch.getTileEntities()) {
                if (bls.getType().equals(Material.CHEST) && Locations.isInsideBase(bls.getLocation(), gameInstance.getGame().getTeams()))
                        protectedChest.add(bls.getLocation());
            }
        }
    }
}


