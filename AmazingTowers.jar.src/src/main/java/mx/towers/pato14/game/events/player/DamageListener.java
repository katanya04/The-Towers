package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.entity.Entity;
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
            if (getTeams(e.getEntity()).getTeam(TeamColor.BLUE).containsPlayer(e.getEntity().getName()) && getTeams(e.getEntity()).getTeam(TeamColor.BLUE).containsPlayer(e.getDamager().getName())) {
                e.setCancelled(true);
            } else if (getTeams(e.getEntity()).getTeam(TeamColor.RED).containsPlayer(e.getEntity().getName()) && getTeams(e.getEntity()).getTeam(TeamColor.RED).containsPlayer(e.getDamager().getName())) {
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
                if (getTeams(pl1).getTeam(TeamColor.BLUE).containsPlayer(pl1.getName()) && getTeams(pl1).getTeam(TeamColor.BLUE).containsPlayer(pl2.getName())) {
                    e.setCancelled(true);
                    p.remove();
                } else if (getTeams(pl1).getTeam(TeamColor.RED).containsPlayer(pl1.getName()) && getTeams(pl1).getTeam(TeamColor.RED).containsPlayer(pl2.getName())) {
                    e.setCancelled(true);
                    p.remove();
                }
            }
        }
    }

    private GameTeams getTeams(Entity e) {
        return this.plugin.getGameInstance(e).getGame().getTeams();
    }
}


