package mx.towers.pato14.utils.mysql;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.Bukkit;

import java.util.HashMap;

public interface FindOneCallback {

    void onQueryDone(int[] result);
    static void findPlayerAsync(final String name, final String tableName, final FindOneCallback callback) {
        // Run outside the tick loop
        Bukkit.getScheduler().runTaskAsynchronously(AmazingTowers.getPlugin(), () -> {
            int[] data = AmazingTowers.connexion.getStats(name, tableName);
            // go back to the tick loop
            Bukkit.getScheduler().runTask(AmazingTowers.getPlugin(), () -> {
                // call the callback with the result
                callback.onQueryDone(data);
            });
        });
    }
}
