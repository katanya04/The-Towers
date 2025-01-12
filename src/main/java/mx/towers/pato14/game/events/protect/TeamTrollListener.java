// package mx.towers.pato14.game.events.protect;

// import mx.towers.pato14.AmazingTowers;
// import mx.towers.pato14.GameInstance;
// import mx.towers.pato14.game.team.ITeam;
// import mx.towers.pato14.utils.Utils;
// import mx.towers.pato14.utils.enums.ConfigType;
// import org.bukkit.GameMode;
// import org.bukkit.Material;
// import org.bukkit.block.BlockFace;
// import org.bukkit.entity.Entity;
// import org.bukkit.entity.Player;
// import org.bukkit.event.EventHandler;
// import org.bukkit.event.Listener;
// import org.bukkit.event.block.BlockBreakEvent;
// import org.bukkit.inventory.ItemStack;

// public class TeamTrollListener implements Listener {

//     @EventHandler
//     public void onBreak(BlockBreakEvent e) {
//         GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
//         if (gameInstance == null || gameInstance.getGame() == null)
//             return;
//         if (!gameInstance.getGame().getGameState().matchIsBeingPlayed)
//             return;
//         if (!e.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() || e.getBlock().getRelative(BlockFace.DOWN).getDrops().contains(new ItemStack(Material.SIGN))) {
//             Player pl = e.getPlayer();
//             ITeam plTeam = gameInstance.getGame().getTeams().getTeamByPlayer(pl.getName());
//             Entity[] arrayOfEntity = e.getBlock().getChunk().getEntities();
//             for (Entity en : arrayOfEntity) {
//                 if (!(en instanceof Player))
//                     continue;
//                 Player player = (Player) en;
//                 if (pl.equals(player))
//                     continue;
//                 ITeam playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(player.getName());
//                 if (playerTeam == null)
//                     continue;
//                 if (playerTeam.equals(plTeam) && e.getBlock().getRelative(BlockFace.UP).equals(player.getLocation().getBlock()) && player.getGameMode() == GameMode.SURVIVAL) {
//                     e.setCancelled(true);
//                     pl.sendMessage(Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("trollingTeammates")));
//                     return;
//                 }
//             }
//         }
//     }
// }
