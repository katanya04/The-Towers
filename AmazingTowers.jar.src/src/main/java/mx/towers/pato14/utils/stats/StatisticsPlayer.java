package mx.towers.pato14.utils.stats;

import java.util.HashMap;

import mx.towers.pato14.utils.enums.StatType;

public class StatisticsPlayer {
    private final HashMap<String, Stats> playerStats = new HashMap<>();

    public void addOne(String player, StatType st) {
        if (this.playerStats.containsKey(player)) {
            this.playerStats.get(player).addOne(st);
        }
    }

    public void setHashStats(String player) {
        if (!this.playerStats.containsKey(player)) {
            this.playerStats.put(player, new Stats());
        }
    }

    public int getStat(String player, StatType st) {
        return this.playerStats.containsKey(player) ? this.playerStats.get(player).getStat(st) : 0;
    }

    public HashMap<String, Stats> getPlayerStats() {
        return this.playerStats;
    }
}


