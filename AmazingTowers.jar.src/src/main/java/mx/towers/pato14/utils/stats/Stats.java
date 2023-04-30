package mx.towers.pato14.utils.stats;

import mx.towers.pato14.utils.enums.StatType;

import java.util.LinkedHashMap;

public class Stats {
    private final LinkedHashMap<StatType, Integer> stats = new LinkedHashMap<>();
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

}
