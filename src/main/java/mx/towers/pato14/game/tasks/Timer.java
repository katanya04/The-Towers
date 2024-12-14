package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
    private boolean activated;
    private int time;
    private BukkitRunnable timerTask;
    private final String instanceName;

    public Timer(GameInstance gameInstance) {
        this.instanceName = gameInstance.getInternalName();
        this.activated = getActivated(gameInstance);
        this.time = getTime(gameInstance);
    }

    private boolean getActivated(GameInstance gameInstance) {
        return Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("timer.activated"));
    }

    public int getTime(GameInstance gameInstance) {
        try {
            return Utils.stringTimeToInt(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("timer.time").split(":"));
        } catch (Exception ex) {
            gameInstance.getConfig(ConfigType.GAME_SETTINGS).set("timer.time", "30:00");
            Utils.sendConsoleMessage("Error while reading timer's time. Set to default value (30:00)", MessageType.ERROR);
        }
        return 1800;
    }

    // 
    public void timerStart() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(this.instanceName);
        if (time <= 60) {
            gameInstance.broadcastMessage(" &eLa partida terminará en &b" + time + " &esegundos.", true);
        } else{
            gameInstance.broadcastMessage(" &eLa partida terminará en &b" + (time / 60) + " &eminutos.", true);
        }
        (timerTask = new BukkitRunnable() {
            public void run() {
                if (!activated || !AmazingTowers.getGameInstance(instanceName).getGame().getGameState().matchIsBeingPlayed) {
                    return;
                }
                if (time <= 0) {
                    AmazingTowers.getGameInstance(instanceName).getGame().getFinish().endMatchOrGoldenGoal();
                    timerTask.cancel();
                    return;
                }
                if (time % 300 == 0 && time > 0) {
                    int minutesRemaining = time / 60;
                    gameInstance.broadcastMessage(" &eQuedan &b" + minutesRemaining + " &eminutos.", true);
                }else if(time==120 || time==180 || time==240) {
                    int minutesRemaining = time / 60;
                    gameInstance.broadcastMessage(" &eQuedan &b" + minutesRemaining + " &eminutos.", true);
                }else if (time == 60 ||time == 30 || time == 10 || (time <= 5)) {
                    gameInstance.broadcastMessage(" &eQuedan &b" + time + " &esegundos.", true);
                } else if (time == 1){
                    gameInstance.broadcastMessage(" &eQueda &b" + time + " &esegundo.", true);
                }
                time--;
            }
        }).runTaskTimer(gameInstance.getPlugin(), 20L, 20L);
    }
    
    public void update(GameInstance gameInstance) {
        this.activated = getActivated(gameInstance);
        this.time = getTime(gameInstance);
        if (gameInstance.getGame().getGameState().matchIsBeingPlayed) {
            if (this.activated) {
                if (timerTask != null) {
                    timerTask.cancel();
                }
                timerStart();
            }
        }
    }
}