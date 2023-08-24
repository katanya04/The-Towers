package mx.towers.pato14.utils.cofresillos;

import java.util.HashMap;
import java.util.Map;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class RefillTask {
    private int refillTime;
    private static Map<Location, FixedItem[]> refileadoProaso = new HashMap<>();
    private final GameInstance gameInstance;

    public RefillTask(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        this.refillTime = Utils.stringTimeToInt(gameInstance.getConfig(ConfigType.CONFIG)
                .getString("options.chests.refillChests.refillTime").split(":"));
    }

    public void startRefillTask() {
        if (this.gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.chests.refillChests.enabled") &&
                this.gameInstance.getConfig(ConfigType.LOCATIONS).getStringList("LOCATIONS.REFILLCHEST") != null) {
            refileadoProaso = SelectCofresillos.makelist(this.gameInstance.getConfig(ConfigType.LOCATIONS), "LOCATIONS.REFILLCHEST");
            (new BukkitRunnable() {
                public void run() {
                    if (gameInstance.getGame().getGameState().equals(GameState.FINISH)) {
                        cancel();
                        return;
                    }
                    gameInstance.getScoreUpdates().updateScoreboardAll();
                    if (RefillTask.this.refillTime == 0) {
                        refillTime = Utils.stringTimeToInt(gameInstance.getConfig(ConfigType.CONFIG)
                                .getString("options.chests.refillChests.refillTime").split(":"));
                        SelectCofresillos.refill(RefillTask.refileadoProaso);
                        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.chests.refillChests.sendMessageOnRefill"))
                            gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("filledChest"), true);
                        return;
                    }
                    refillTime--;
                }
            }).runTaskTimerAsynchronously(gameInstance.getPlugin(), 0L, 20L);
        }
    }

    public static Map<Location, FixedItem[]> getChestRefill() {
        return refileadoProaso;
    }

    public int getTimeRegeneration() {
        return this.refillTime;
    }
}


