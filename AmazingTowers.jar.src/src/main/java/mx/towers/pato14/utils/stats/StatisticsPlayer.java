package mx.towers.pato14.utils.stats;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StatisticsPlayer {
    private final HashMap<String, Stats> playerStats = new HashMap<>();

    public void increaseOne(String player, StatType st) {
        if (this.playerStats.containsKey(player)) {
            this.playerStats.get(player).addOne(st);
        }
    }

    public void increaseOneAll(StatType st) {
        this.playerStats.values().forEach(o -> o.addOne(st));
    }

    public void increaseOneConditional(StatType st, Predicate<String> condition) {
        this.playerStats.entrySet().stream().filter(o -> condition.test(o.getKey())).forEach(o -> o.getValue().addOne(st));
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

    public void clear() {
        this.playerStats.clear();
    }

    public LinkedHashMap<String, Stats> getSorted(StatType st) {
        Comparator<Stats> comparator = Comparator.comparingInt(o -> o.getStat(st));
        return this.playerStats.entrySet().stream().sorted(Map.Entry.comparingByValue(comparator.reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}


