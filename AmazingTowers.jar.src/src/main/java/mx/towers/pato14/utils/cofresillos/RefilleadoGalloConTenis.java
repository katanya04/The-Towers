package mx.towers.pato14.utils.cofresillos;

import java.util.HashMap;
import java.util.Map;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RefilleadoGalloConTenis {
    private final AmazingTowers plugin;
    private int regeneration;
    private static Map<Location, FixedItem[]> refileadoProaso = new HashMap<>();
    private final GameInstance gameInstance;

    public RefilleadoGalloConTenis(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        this.plugin = gameInstance.getPlugin();
        String[] timer = gameInstance.getConfig(ConfigType.CONFIG).getString("Options.refill_chests.timer_refill").split(";");
        this.regeneration = Integer.parseInt(timer[0]) * 60 + Integer.parseInt(timer[1]);
    }

    public void iniciarRefill() {
        if (this.gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.refill_chests.enabled") &&
                this.gameInstance.getConfig(ConfigType.LOCATIONS).getStringList("LOCATIONS.REFILLCHEST") != null) {
            refileadoProaso = SelectCofresillos.makelist(this.gameInstance.getConfig(ConfigType.LOCATIONS), "LOCATIONS.REFILLCHEST");
            (new BukkitRunnable() {
                public void run() {
                    if (GameState.isState(GameState.FINISH)) {
                        cancel();
                        return;
                    }
                    RefilleadoGalloConTenis.this.gameInstance.getUpdates().updateScoreboardAll();
                    if (RefilleadoGalloConTenis.this.regeneration == 0) {
                        String[] timer = RefilleadoGalloConTenis.this.gameInstance.getConfig(ConfigType.CONFIG).getString("Options.refill_chests.timer_refill").split(";");
                        RefilleadoGalloConTenis.this.regeneration = Integer.parseInt(timer[0]) * 60 + Integer.parseInt(timer[1]);
                        SelectCofresillos.refill(RefilleadoGalloConTenis.refileadoProaso);
                        if (RefilleadoGalloConTenis.this.gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.refill_chests.message_refill")) {
                            Bukkit.broadcastMessage(AmazingTowers.getColor(RefilleadoGalloConTenis.this.gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.filledChest")));
                        }
                        return;
                    }
                    RefilleadoGalloConTenis.this.regeneration = RefilleadoGalloConTenis.this.regeneration - 1;
                }
            }).runTaskTimerAsynchronously((Plugin) this.plugin, 0L, 20L);
        }
    }

    public static Map<Location, FixedItem[]> getChestRefill() {
        return refileadoProaso;
    }

    public float getTimeRegeneration() {
        return this.regeneration;
    }
}


