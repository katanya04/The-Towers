package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();

    @EventHandler
    public void voidDamage(EntityDamageEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getGame().getGameState().equals(GameState.GAME)) {
            e.setCancelled(true);
            return;
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (p.getHealth() > 0.0) p.setHealth(0.0);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            GameTeams teams = gameInstance.getGame().getTeams();
            if (e.getDamager().getType().equals(EntityType.PLAYER)) { // Player attacks player
                if (teams.getTeamByPlayer((Player) e.getEntity()).equals(teams.getTeamByPlayer((Player) e.getDamager())))
                    e.setCancelled(true);
            // Player shoots player
            } else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
                if (teams.getTeamByPlayer((Player) e.getEntity()).equals(teams.getTeamByPlayer((Player) ((Projectile) e.getDamager()).getShooter())))
                    e.setCancelled(true);
            }
        }
    }
}