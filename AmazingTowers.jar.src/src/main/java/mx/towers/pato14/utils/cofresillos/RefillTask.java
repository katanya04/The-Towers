package mx.towers.pato14.utils.cofresillos;

import java.util.HashMap;
import java.util.Map;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class RefillTask {
    private int refillTime;
    private Map<Location, FixedItem[]> refileadoProaso = new HashMap<>();
    private final String instanceName;

    public RefillTask(GameInstance gameInstance) {
        this.instanceName = gameInstance.getInternalName();
        this.refillTime = Utils.stringTimeToInt(gameInstance.getConfig(ConfigType.CONFIG)
                .getString("options.chests.refillChests.refillTime").split(":"));
    }

    public void startRefillTask() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(this.instanceName);
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.chests.refillChests.enabled") &&
                gameInstance.getConfig(ConfigType.LOCATIONS).getStringList("LOCATIONS.REFILLCHEST") != null) {
            refileadoProaso = SelectCofresillos.makelist(gameInstance.getConfig(ConfigType.LOCATIONS), "LOCATIONS.REFILLCHEST");
            (new BukkitRunnable() {
                public void run() {
                    if (gameInstance.getGame().getGameState().equals(GameState.FINISH)) {
                        cancel();
                        return;
                    }
                    gameInstance.getScoreUpdates().updateScoreboardAll();
                    if (RefillTask.this.refillTime == 0) {
                        resetTime();
                        SelectCofresillos.refill(refileadoProaso);
                        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.chests.refillChests.sendMessageOnRefill"))
                            gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("filledChest"), true);
                        return;
                    }
                    refillTime--;
                }
            }).runTaskTimer(gameInstance.getPlugin(), 0L, 20L);
        }
    }

    public int getTimeRegeneration() {
        return this.refillTime;
    }
    public void resetTime() {
        this.refillTime = Utils.stringTimeToInt(AmazingTowers.getGameInstance(instanceName).getConfig(ConfigType.CONFIG)
                .getString("options.chests.refillChests.refillTime").split(":"));
    }
}


