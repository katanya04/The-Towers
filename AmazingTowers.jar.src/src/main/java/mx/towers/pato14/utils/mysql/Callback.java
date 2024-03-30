package mx.towers.pato14.utils.mysql;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.Bukkit;

public interface Callback<T> {
    void onQueryDone(T result);
    static void findPlayerAsync(final String name, final String tableName, final Callback<int[]> callback) {
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
