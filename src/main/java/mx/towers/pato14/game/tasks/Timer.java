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
        } else {
            gameInstance.broadcastMessage(" &eLa partida terminará en &b" + (time / 60) + " &eminutos.", true);
        }
    
        // Registrar el tiempo de inicio del temporizador
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + (time * 1000L);
    
        (timerTask = new BukkitRunnable() {
            public void run() {
                if (!activated || !AmazingTowers.getGameInstance(instanceName).getGame().getGameState().matchIsBeingPlayed) {
                    this.cancel();
                    return;
                }
    
                // Calcular el tiempo restante basado en el reloj del sistema
                long currentTime = System.currentTimeMillis();
                long remainingTime = (endTime - currentTime) / 1000;
    
                if (remainingTime <= 0) {
                    AmazingTowers.getGameInstance(instanceName).getGame().getFinish().endMatchOrGoldenGoal();
                    this.cancel();
                    return;
                }
    
                // Enviar mensajes en los momentos adecuados
                if (remainingTime % 300 == 0 && remainingTime > 0 || remainingTime == 120 || remainingTime == 180 || remainingTime == 240) {
                    int minutesRemaining = (int) (remainingTime / 60);
                    gameInstance.broadcastMessage(" &eQuedan &b" + minutesRemaining + " &eminutos.", true);
                } else if (remainingTime == 60 || remainingTime == 30 || remainingTime == 10 || remainingTime <= 5) {
                    gameInstance.broadcastMessage(" &eQuedan &b" + remainingTime + " &esegundos.", true);
                } else if (remainingTime == 1) {
                    gameInstance.broadcastMessage(" &eQueda &b" + remainingTime + " &esegundo.", true);
                }
    
                // Actualizar la variable de tiempo (opcional, para sincronización interna)
                time = (int) remainingTime;
            }
        }).runTaskTimer(gameInstance.getPlugin(), 20L, 20L); // El tick rate se usa solo para verificar, no para medir tiempo real
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