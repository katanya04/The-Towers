package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MobSpawn implements Listener {
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        Location loc = e.getLocation();
        GameInstance instance = AmazingTowers.getGameInstance(loc.getWorld());
        if (instance == null)
            return;
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            if (e.getEntityType() == EntityType.HORSE) {
                Horse horse = (Horse) e.getEntity();
                horse.setTamed(true);
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            }
        }
        if (instance.getRules().get(Rule.EXPLOSIVE_CHICKEN) && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1, 5, false, false));
            loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 1.f, false, false);
        }
    }
}
