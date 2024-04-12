package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

public class UseSpawnEgg implements Listener {
    @EventHandler
    public void onUseSpawnEgg(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (p.getItemInHand().getType() != Material.MONSTER_EGG)
            return;
        GameInstance instance = AmazingTowers.getGameInstance(p);
        if (instance == null)
            return;
        TeamColor teamColor = instance.getGame().getTeams().getTeamColorByPlayer(p.getName());
        if (teamColor == null)
            return;
        e.setCancelled(true);
        SpawnEgg spawnEgg = (SpawnEgg) p.getItemInHand().getData();
        Entity entity = p.getWorld().spawnEntity(e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().add(0.5, 0, 0.5), spawnEgg.getSpawnedType());
        if (entity.getType() == EntityType.HORSE) {
            Horse horse = (Horse) entity;
            horse.setTamed(true);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            horse.setAdult();
        }
        Utils.removeItemFromHand(p);
    }
}