package mx.towers.pato14.utils.wand;

import mx.towers.pato14.utils.Utils;
import org.bukkit.Location;

public class WandCoords {
    private final Utils.Pair<Location, Location> region;
    private final Utils.Pair<Boolean, Boolean> set;

    public WandCoords() {
        this.region = new Utils.Pair<>(null, null);
        this.set = new Utils.Pair<>(false, false);
    }

    public void clearPos1() {
        this.set.setKey(false);
    }

    public void clearPos2() {
        this.set.setValue(false);
    }

    public void clearStrings() {
        clearPos1();
        clearPos2();
    }
    public boolean isSetPos1() {
        return set.getKey();
    }
    public boolean isSetPos2() {
        return set.getValue();
    }

    public void setPos1(Location pos1) {
        this.set.setKey(true);
        this.region.setKey(pos1);
    }

    public void setPos2(Location pos2) {
        this.set.setValue(true);
        this.region.setValue(pos2);
    }

    public Utils.Pair<Location, Location> getRegion() {
        return this.region;
    }

    public Location getPos1() {
        return this.region.getKey();
    }

    public Location getPos2() {
        return this.region.getValue();
    }
}


