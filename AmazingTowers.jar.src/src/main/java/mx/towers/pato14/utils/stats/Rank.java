package mx.towers.pato14.utils.stats;

import mx.towers.pato14.utils.Utils;
import org.bukkit.Sound;

public enum Rank {
    S(8F, Sound.ENDERDRAGON_GROWL, 2F, "§c"),
    A(6F, Sound.LEVEL_UP, 1.5F, "§6"),
    B(4F, Sound.ORB_PICKUP, 2F, "§e"),
    C(2F, Sound.NOTE_PLING, 2F, "§a"),
    D(1F, Sound.NOTE_SNARE_DRUM, 0.5F, "§3"),
    E(0.5F, Sound.ANVIL_LAND, 0.5F, "§9"),
    F(0F, Sound.CAT_HIT, 0.5F, "§d");
    private final float points;
    private final Sound sound;
    private final float pitch;
    private final String color;
    Rank(float points, Sound sound, float pitch, String color) {
        this.points = points;
        this.sound = sound;
        this.pitch = pitch;
        this.color = color;
    }
    public static Rank getRank(double points) {
        for (Rank r: values()) {
            if (points >= r.points)
                return r;
        }
        return F;
    }
    public static Rank getTotalRank(Stats st) {
        double toret = Utils.safeDivide(st.getStat(StatType.KILLS), st.getStat(StatType.DEATHS)) * 1.25 + Utils.safeDivide(st.getStat(StatType.POINTS), st.getStat(StatType.GAMES_PLAYED)) * 2.5 +
                (Utils.safeDivide(st.getStat(StatType.KILLS), st.getStat(StatType.GAMES_PLAYED)) / 15 - Utils.safeDivide(st.getStat(StatType.DEATHS), st.getStat(StatType.GAMES_PLAYED)) / 20) + Utils.safeDivide(st.getStat(StatType.WINS), st.getStat(StatType.GAMES_PLAYED)) * 2;
        return getRank(toret);
    }
    public String getColor() {
        return this.color;
    }
    public Sound getSound() {
        return this.sound;
    }
    public float getPitch() {
        return this.pitch;
    }
}
