package mx.towers.pato14.game.gameevents;

import org.bukkit.scheduler.BukkitRunnable;

public class PotionEvent extends RepeatingGameEvent {
    public PotionEvent(long startAt, BukkitRunnable task, int period) {
        super(startAt, task, period);
    }
}
