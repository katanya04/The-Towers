package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class AntiTrollTeamListener implements Listener {
    private final AmazingTowers plugin;

    public AntiTrollTeamListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getGame().getGameState().equals(GameState.GAME))
            return;
        if (!e.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() || e.getBlock().getRelative(BlockFace.DOWN).getDrops().contains(new ItemStack(Material.SIGN))) {
            Player pl = e.getPlayer();
            Team plTeam = gameInstance.getGame().getTeams().getTeamByPlayer(pl);
            byte b;
            int i;
            Entity[] arrayOfEntity;
            for (i = (arrayOfEntity = e.getBlock().getChunk().getEntities()).length, b = 0; b < i; ) {
                Entity en = arrayOfEntity[b];
                if (en instanceof Player) {
                    Player player = (Player) en;
                    if (!pl.equals(player)) {
                        Team playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(player);
                        if (playerTeam.equals(plTeam) && e.getBlock().getRelative(BlockFace.UP).equals(player.getLocation().getBlock())) {
                            e.setCancelled(true);
                            pl.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.noTrollBreakBlock")));
                            return;
                        }
                    }
                }
                b++;
            }
        }
    }
}


