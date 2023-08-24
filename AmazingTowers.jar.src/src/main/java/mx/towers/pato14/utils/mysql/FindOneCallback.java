package mx.towers.pato14.utils.mysql;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.Bukkit;

import java.util.HashMap;

public interface FindOneCallback {

    void onQueryDone(int[] result);
    static void findPlayerAsync(final String name, final AmazingTowers plugin, final FindOneCallback callback) {
        // Run outside the tick loop
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int[] data = plugin.connexion.getStats(name);
            // go back to the tick loop
            Bukkit.getScheduler().runTask(plugin, () -> {
                // call the callback with the result
                callback.onQueryDone(data);
            });
        });
    }
    static void updatePlayersDataAsync(final HashMap<String, Stats> stats, final AmazingTowers plugin, final FindOneCallback callback) {
        // Run outside the tick loop
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (String p : stats.keySet()) {
                plugin.connexion.updateData(p, stats.get(p));
            }
            // go back to the tick loop
            Bukkit.getScheduler().runTask(plugin, () -> {
                // call the callback with the result
                callback.onQueryDone(null);
            });
        });
    }
}
