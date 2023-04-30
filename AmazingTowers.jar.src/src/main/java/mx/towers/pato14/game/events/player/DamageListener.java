package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.team.TeamGame;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.plugin.PluginA;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {
    private AmazingTowers at = AmazingTowers.getPlugin();

    @EventHandler
    public void voidDamage(EntityDamageEvent e) {
        if (!GameState.isState(GameState.GAME)) {
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
    public void ondamage(EntityDamageByEntityEvent e) {
        if (e.getEntityType().equals(EntityType.PLAYER) && e.getDamager().getType().equals(EntityType.PLAYER)) {
            if (t().getBlue().containsPlayer(e.getEntity().getName()) && t().getBlue().containsPlayer(e.getDamager().getName())) {
                e.setCancelled(true);
            } else if (t().getRed().containsPlayer(e.getEntity().getName()) && t().getRed().containsPlayer(e.getDamager().getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSnowball(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile && e.getEntity() instanceof Player) {
            Projectile p = (Projectile) e.getDamager();
            if (p.getShooter() instanceof Player) {
                Player pl1 = (Player) p.getShooter();
                Player pl2 = (Player) e.getEntity();
                if (t().getBlue().containsPlayer(pl1.getName()) && t().getBlue().containsPlayer(pl2.getName())) {
                    e.setCancelled(true);
                    p.remove();
                } else if (t().getRed().containsPlayer(pl1.getName()) && t().getRed().containsPlayer(pl2.getName())) {
                    e.setCancelled(true);
                    p.remove();
                }
            }
        }
    }

    private TeamGame t() {
        return this.at.getGame().getTeams();
    }
}


