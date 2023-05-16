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
        if (plugin.getGameInstance(e.getEntity()).getGame().getGameState().equals(GameState.GAME)) {
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
            GameTeams teams = plugin.getGameInstance(e.getEntity()).getGame().getTeams();
            if (teams.getTeamByPlayer((Player) e.getEntity()).equals(teams.getTeamByPlayer((Player) e.getDamager())))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSnowball(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile && e.getEntity() instanceof Player) {
            Projectile p = (Projectile) e.getDamager();
            if (p.getShooter() instanceof Player) {
                Player pl1 = (Player) p.getShooter();
                Player pl2 = (Player) e.getEntity();
                GameTeams teams = plugin.getGameInstance(pl1).getGame().getTeams();
                if (teams.getTeamByPlayer(pl1).equals(teams.getTeamByPlayer(pl2))) {
                    e.setCancelled(true);
                    p.remove();
                }
            }
        }
    }

}


