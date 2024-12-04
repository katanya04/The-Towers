package mx.towers.pato14.utils.mysql;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Map;

public interface Callback<T> {
    void onQueryDone(T result);
    static void findPlayerAsync(final String name, final String tableName, final Callback<Stats> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(AmazingTowers.getPlugin(), () -> {
            Stats data = AmazingTowers.connexion.getStats(name, tableName);
            Bukkit.getScheduler().runTask(AmazingTowers.getPlugin(), () -> callback.onQueryDone(data));
        });
    }
    static void findPlayerAsync(final Collection<String> players, final Collection<String> tables, final Callback<Map<String, Stats>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(AmazingTowers.getPlugin(), () -> {
            Map<String, Stats> data = AmazingTowers.connexion.getStats(players, tables);
            Bukkit.getScheduler().runTask(AmazingTowers.getPlugin(), () -> callback.onQueryDone(data));
        });
    }
}
