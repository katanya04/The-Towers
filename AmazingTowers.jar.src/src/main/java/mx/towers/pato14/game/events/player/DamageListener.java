package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.utils.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void voidDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        TowersWorldInstance instance = AmazingTowers.getInstance(player);
        if (instance == null)
            return;
        if (instance instanceof LobbyInstance) {
            e.setCancelled(true);
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID)
                Utils.tpToLobby(instance, player);
        } else if (instance instanceof GameInstance && e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            GameInstance gameInstance = (GameInstance) instance;
            if (gameInstance.getGame() == null)
                return;
            player.setLastDamageCause(e);
            if (!gameInstance.getGame().getGameState().matchIsBeingPlayed)
                Utils.tpToLobby(gameInstance, player);
            else if (player.getHealth() > 0.0) {
                if (player.isInsideVehicle())
                    player.leaveVehicle();
                player.setHealth(0.0);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.PLAYER)
            return;
        Player player = (Player) e.getEntity();
        TowersWorldInstance instance = AmazingTowers.getInstance(player);
        if (instance == null)
            return;
        if (instance instanceof LobbyInstance) {
            e.setCancelled(true);
        } else if (instance instanceof GameInstance) {
            GameInstance gameInstance = (GameInstance) instance;
            if (gameInstance.getGame() == null)
                return;
            if (!gameInstance.getGame().getGameState().matchIsBeingPlayed) {
                e.setCancelled(true);
                return;
            }
            GameTeams teams = gameInstance.getGame().getTeams();
            if (teams.getTeamByPlayer(player.getName()) == null) {
                e.setCancelled(true);
                return;
            }
            if (e.getDamager().getType().equals(EntityType.PLAYER)) { // Player attacks player
                if (teams.getTeamByPlayer(e.getDamager().getName()) == null ||
                        teams.getTeamByPlayer(player.getName()).equals(teams.getTeamByPlayer(e.getDamager().getName())))
                    e.setCancelled(true);
                // Player shoots player
            } else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
                if (teams.getTeamByPlayer(player.getName()).equals(teams.getTeamByPlayer(((Player) ((Projectile) e.getDamager()).getShooter()).getName())))
                    e.setCancelled(true);
            }
        }
    }
}