package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.AreaUtil;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestsProtect implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        e.blockList().removeIf(bl -> bl.getType() == Material.CHEST &&
                Locations.isInsideBase(bl.getLocation(), gameInstance.getGame().getTeams()));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getBlock());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        Block bl = e.getBlock();
        if (bl.getType() == Material.CHEST &&
                Locations.isInsideBase(bl.getLocation(), gameInstance.getGame().getTeams())) {
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
        if (Locations.isInsideBase(block.getLocation(), gameInstance.getGame().getTeams())) {
            TeamColor teamColor = gameInstance.getGame().getTeams().getTeamColorByPlayer(player.getName());
            if (teamColor == null)
                return;
            if (!AreaUtil.isInsideArea(gameInstance.getConfig(ConfigType.LOCATIONS).getStringList(mx.towers.pato14.utils.enums.Location.CHEST_PROTECT.getPath(teamColor)), block.getLocation())) {
                e.setCancelled(true);
                player.sendMessage(Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("openingEnemyChest")));
            }
        }
    }
}


