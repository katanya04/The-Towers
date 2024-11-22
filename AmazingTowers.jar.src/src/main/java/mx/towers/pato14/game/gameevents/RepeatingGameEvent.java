package mx.towers.pato14.game.gameevents;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.scheduler.BukkitRunnable;

public class RepeatingGameEvent extends GameEvent {
    protected final int period;
    public RepeatingGameEvent(long startAt, BukkitRunnable task, int period) {
        super(startAt, task);
        this.period = period;
    }
    @Override
    protected void startTask() {
        this.task.runTaskTimer(AmazingTowers.getPlugin(), startAt, period);
    }
}
