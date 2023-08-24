package mx.towers.pato14.utils.stats;

import mx.towers.pato14.utils.Utils;
import org.bukkit.Sound;

public enum Rank {
    S(8F, Sound.ENDERDRAGON_GROWL, 2F, "§c§lS"),
    A(6F, Sound.LEVEL_UP, 1.5F, "§6§lA"),
    B(4F, Sound.ORB_PICKUP, 2F, "§e§lB"),
    C(2F, Sound.NOTE_PLING, 2F, "§a§lC"),
    D(1F, Sound.NOTE_SNARE_DRUM, 0.5F, "§3§lD"),
    E(0.5F, Sound.ANVIL_LAND, 0.5F, "§9§lE"),
    F(0F, Sound.CAT_HIT, 0.5F, "§d§lF");
    private final float points;
    private final Sound sound;
    private final float pitch;
    private final String text;
    Rank(float points, Sound sound, float pitch, String text) {
        this.points = points;
        this.sound = sound;
        this.pitch = pitch;
        this.text = text;
    }
    public static Rank getRank(double points) {
        for (Rank r: values()) {
            if (points >= r.points)
                return r;
        }
        return F;
    }
    public static Rank getTotalRank(int[] stats) {
        double toret = Utils.safeDivide(stats[0], stats[1]) * 1.25 + Utils.safeDivide(stats[2], stats[3]) * 2.5 +
                (Utils.safeDivide(stats[0], stats[3]) / 15 - Utils.safeDivide(stats[1], stats[3]) / 20) + Utils.safeDivide(stats[4], stats[3]) * 2;
        return getRank(toret);
    }
    public String toText() {
        return this.text;
    }
    public Sound getSound() {
        return this.sound;
    }
    public float getPitch() {
        return this.pitch;
    }
}
