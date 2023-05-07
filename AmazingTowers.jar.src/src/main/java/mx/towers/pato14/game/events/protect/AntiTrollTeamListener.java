package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
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
        if (!GameState.isState(GameState.GAME))
            return;
        if (!e.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() || e.getBlock().getRelative(BlockFace.DOWN).getDrops().contains(new ItemStack(Material.SIGN))) {
            Player pl = e.getPlayer();
            byte b;
            int i;
            Entity[] arrayOfEntity;
            for (i = (arrayOfEntity = e.getBlock().getChunk().getEntities()).length, b = 0; b < i; ) {
                Entity en = arrayOfEntity[b];
                if (en instanceof Player) {
                    Player player = (Player) en;
                    if (!pl.equals(player)) {
                        if (this.plugin.getGameInstance(pl).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(pl.getName())) {
                            if (this.plugin.getGameInstance(pl).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(player.getName()) &&
                                    e.getBlock().getRelative(BlockFace.UP).equals(player.getLocation().getBlock())) {
                                e.setCancelled(true);
                                pl.sendMessage(AmazingTowers.getColor(this.plugin.getGameInstance(pl).getConfig(ConfigType.MESSAGES).getString("messages.noTrollBreakBlock")));
                                return;
                            }
                        } else if (this.plugin.getGameInstance(pl).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(pl.getName()) &&
                                this.plugin.getGameInstance(pl).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(player.getName()) &&
                                e.getBlock().getRelative(BlockFace.UP).equals(player.getLocation().getBlock())) {
                            e.setCancelled(true);
                            pl.sendMessage(AmazingTowers.getColor(this.plugin.getGameInstance(pl).getConfig(ConfigType.MESSAGES).getString("messages.noTrollBreakBlock")));
                            return;
                        }
                    }
                }
                b++;
            }
        }
    }
}


