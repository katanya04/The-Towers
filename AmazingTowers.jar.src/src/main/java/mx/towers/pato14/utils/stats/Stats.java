package mx.towers.pato14.utils.stats;

import java.util.LinkedHashMap;
import java.util.Map;

public class Stats {
    private final Map<StatType, Integer> stats = new LinkedHashMap<>();
    public Stats() {
        for (StatType st : StatType.values())
            stats.put(st, 0);
    }
    public void addOne(StatType st) {
        this.stats.replace(st, this.stats.get(st) + 1);
    }

    public int getStat(StatType st) {
        return this.stats.get(st);
    }

    @Override
    public String toString() {
        return stats.toString();
    }

}
