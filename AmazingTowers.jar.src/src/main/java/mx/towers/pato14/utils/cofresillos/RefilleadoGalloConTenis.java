package mx.towers.pato14.utils.cofresillos;

import java.util.HashMap;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RefilleadoGalloConTenis {
    private AmazingTowers plugin;
    private int regeneration;
    private static HashMap<Location, FixedItem[]> refileadoProaso = (HashMap) new HashMap<>();

    public RefilleadoGalloConTenis(AmazingTowers plugin) {
        this.plugin = plugin;
        String[] timer = plugin.getConfig().getString("Options.refill_chests.timer_refill").split(";");
        this.regeneration = Integer.parseInt(timer[0]) * 60 + Integer.parseInt(timer[1]);
    }

    public void iniciarRefill() {
        if (this.plugin.getConfig().getBoolean("Options.refill_chests.enabled") &&
                this.plugin.getLocations().getStringList("LOCATIONS.REFILLCHEST") != null) {
            refileadoProaso = SelectCofresillos.makelist(this.plugin.getLocations(), "LOCATIONS.REFILLCHEST");
            (new BukkitRunnable() {
                public void run() {
                    if (GameState.isState(GameState.FINISH)) {
                        cancel();
                        return;
                    }
                    RefilleadoGalloConTenis.this.plugin.getUpdates().updateScoreboardAll();
                    if (RefilleadoGalloConTenis.this.regeneration == 0) {
                        String[] timer = RefilleadoGalloConTenis.this.plugin.getConfig().getString("Options.refill_chests.timer_refill").split(";");
                        RefilleadoGalloConTenis.this.regeneration = Integer.valueOf(timer[0]).intValue() * 60 + Integer.valueOf(timer[1]).intValue();
                        SelectCofresillos.refill(RefilleadoGalloConTenis.refileadoProaso);
                        if (RefilleadoGalloConTenis.this.plugin.getConfig().getBoolean("Options.refill_chests.message_refill")) {
                            Bukkit.broadcastMessage(RefilleadoGalloConTenis.this.plugin.getColor(RefilleadoGalloConTenis.this.plugin.getMessages().getString("messages.filledChest")));
                        }
                        return;
                    }
                    RefilleadoGalloConTenis.this.regeneration = RefilleadoGalloConTenis.this.regeneration - 1;
                }
            }).runTaskTimerAsynchronously((Plugin) this.plugin, 0L, 20L);
        }
    }

    public static HashMap<Location, FixedItem[]> getChestRefill() {
        return refileadoProaso;
    }

    public float getTimeRegeneration() {
        return this.regeneration;
    }
}


