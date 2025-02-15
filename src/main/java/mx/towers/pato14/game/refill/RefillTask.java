package mx.towers.pato14.game.refill;

import java.util.HashMap;
import java.util.Map;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class RefillTask {
    private int refillTime;
    private int time;
    private Map<Location, FixedItem[]> refileadoProaso = new HashMap<>();
    private final String instanceName;

    public RefillTask(GameInstance gameInstance) {
        this.instanceName = gameInstance.getInternalName();
        this.refillTime = Utils.stringTimeToInt(gameInstance.getConfig(ConfigType.GAME_SETTINGS)
                .getString("refill.time").split(":"));
        String timeString = "00:00";
        this.time = Utils.stringTimeToInt(timeString.split(":"));
    }

    public void startRefillTask() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(this.instanceName);
        if (Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("refill.activated")) &&
                gameInstance.getConfig(ConfigType.LOCATIONS).getStringList("LOCATIONS.REFILLCHEST") != null) {
            refileadoProaso = SelectCofresillos.makelist(gameInstance.getConfig(ConfigType.LOCATIONS), "LOCATIONS.REFILLCHEST");
            new BukkitRunnable() {
                public void run() {
                    if (!gameInstance.getGame().getGameState().matchIsBeingPlayed || !Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS)
                            .getString("refill.activated"))) {
                        cancel();
                        time = 0;
                        refillTime = 0;
                        gameInstance.getScoreUpdates().updateScoreboardAll(false, gameInstance.getWorld().getPlayers());
                        refileadoProaso.clear();
                        return;
                    }
                    gameInstance.getScoreUpdates().updateScoreboardAll(false, gameInstance.getWorld().getPlayers());
                    time++;
                    if (RefillTask.this.refillTime == 1) {
                        resetTime();
                        SelectCofresillos.refill(refileadoProaso);
                        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.chests.refillChests.sendMessageOnRefill"))
                            gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("filledChest"), true);
                        return;
                    }
                    refillTime--;
                }
            }.runTaskTimer(gameInstance.getPlugin(), 0L, 20L);
        } else {
            this.refillTime = 0;
        }
    }

    public int getTimeRegeneration() {
        return this.refillTime;
    }

    public int getTime() {
        return this.time;
    }

    public void resetTime() {
        this.refillTime = Utils.stringTimeToInt(AmazingTowers.getGameInstance(instanceName).getConfig(ConfigType.GAME_SETTINGS)
                .getString("refill.time").split(":"));
    }
}


