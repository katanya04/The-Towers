package mx.towers.pato14.game.events.protect;

import java.util.ArrayList;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.AreaUtil;
import mx.towers.pato14.utils.Utils;
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

public class ChestsProtect implements Listener {
    private static final ArrayList<Location> protectedChest = new ArrayList<>();

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        ArrayList<Block> end = new ArrayList<>(e.blockList());
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        for (Block bl : e.blockList()) {
            if (bl.getType() == Material.CHEST) {
                if (protectedChest.contains(bl.getLocation()))
                    end.remove(bl);
            }
        }
        e.blockList().clear();
        e.blockList().addAll(end);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getBlock());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Block bl = e.getBlock();
        if (bl.getType() == Material.CHEST &&
                protectedChest.contains(bl.getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractChest(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        GameInstance gameInstance = AmazingTowers.getGameInstance(player);
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Block block = e.getClickedBlock();
        if (!gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.chests.lockChestsToOtherTeams"))
            return;
        if (block == null || !block.getType().equals(Material.CHEST))
            return;
        if (protectedChest.contains(block.getLocation())) {
            TeamColor teamColor = gameInstance.getGame().getTeams().getTeamColorByPlayer(player.getName());
            if (!AreaUtil.isInsideArea(gameInstance.getConfig(ConfigType.LOCATIONS).getStringList(mx.towers.pato14.utils.enums.Location.CHEST_PROTECT.getPath(teamColor)), block.getLocation())) {
                e.setCancelled(true);
                player.sendMessage(Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("openingEnemyChest")));
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


