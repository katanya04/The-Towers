package mx.towers.pato14.utils.mysql;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.Team;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.Bukkit;

import java.util.HashMap;

public interface FindOneCallback {

    public void onQueryDone(int[] result);
    public static void findPlayerAsync(final String name, final AmazingTowers plugin, final FindOneCallback callback) {
        // Run outside the tick loop
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                int data[] = plugin.con.getData(name);
                // go back to the tick loop
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        // call the callback with the result
                        callback.onQueryDone(data);
                    }
                });
            }
        });
    }
    public static void updatePlayersDataAsync(final HashMap<String, Stats> stats, final AmazingTowers plugin, final FindOneCallback callback) {
        // Run outside the tick loop
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (String p : stats.keySet()) {
                    plugin.con.UpdateData(p, stats.get(p));
                }
                // go back to the tick loop
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        // call the callback with the result
                        callback.onQueryDone(null);
                    }
                });
            }
        });
    }
}
