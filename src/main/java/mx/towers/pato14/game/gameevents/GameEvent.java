package mx.towers.pato14.game.gameevents;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.scheduler.BukkitRunnable;

public class GameEvent {
    public final long startAt;
    public final BukkitRunnable task;
    protected boolean isRunning;
    public GameEvent(long startAt, BukkitRunnable task) {
        this.startAt = startAt;
        this.task = task;
        this.isRunning = false;
    }
    public void initialize() {
        if (!this.isRunning) {
            startTask();
            this.isRunning = true;
        }
    }
    public void stop() {
        if (this.isRunning) {
            this.task.cancel();
            this.isRunning = false;
        }
    }
    protected void startTask() {
        this.task.runTaskLater(AmazingTowers.getPlugin(), startAt);
    }
}
