package mx.towers.pato14.utils;

import mx.towers.pato14.utils.locations.IntCoordinate;
import mx.towers.pato14.utils.locations.Pool;
import org.bukkit.Location;

import java.util.List;

public class AreaUtil {
    public static boolean isInsideArea(Pool pool, Location loc) {
        return isInsideArea(pool.getCorner1(), pool.getCorner2(), loc);
    }

    public static boolean isInsideArea(List<String> area, Location loc) {
        return isInsideArea(IntCoordinate.getFromString(area.get(0)), IntCoordinate.getFromString(area.get(1)), loc);
    }

    public static boolean isInsideArea(IntCoordinate corner1, IntCoordinate corner2, Location loc) {
        final int x1 = corner1.getX(), x2 = corner2.getX(), lx = loc.getBlockX();
        final int y1 = corner1.getY(), y2 = corner2.getY(), ly = loc.getBlockY();
        final int z1 = corner1.getZ(), z2 = corner2.getZ(), lz = loc.getBlockZ();
        if ( x1 <= x2) {
            if ( x1 >  lx || x2 <  lx) return false;
        } else if ( x1 <  lx || x2 >  lx) {
            return false;
        }
        if ( y1 <= y2) {
            if ( y1 > ly || y2 < ly) return false;
        } else if ( y1 < ly || y2 > ly) {
            return false;
        }
        if ( z1 <= z2) {
            return z1 <= lz && z2 >= lz;
        } else return z1 >= lz && z2 <= lz;
    }

    public static boolean isInsideArea(Pool pool, Location loc, int buffer) {
        return isInsideArea(pool.getCorner1(), pool.getCorner2(), loc, buffer);
    }

    public static boolean isInsideArea(List<String> area, Location loc, int buffer) {
        return isInsideArea(IntCoordinate.getFromString(area.get(0)), IntCoordinate.getFromString(area.get(1)), loc, buffer);
    }

    public static boolean isInsideArea(IntCoordinate corner1, IntCoordinate corner2, Location loc, int buffer) {
        final int x1 = corner1.getX(), x2 = corner2.getX(), lx = loc.getBlockX();
        final int y1 = corner1.getY(), y2 = corner2.getY(), ly = loc.getBlockY();
        final int z1 = corner1.getZ(), z2 = corner2.getZ(), lz = loc.getBlockZ();
        if (x1 <= x2) {
            if (x1 > lx + buffer || x2 + buffer < lx)
                return false;
        } else if (x1 + buffer < lx || x2 > lx + buffer) {
            return false;
        }
        if (y1 <= y2) {
            if (y1 > ly + buffer || y2 + buffer < ly)
                return false;
        } else if (y1 + buffer < ly || y2 > ly + buffer) {
            return false;
        }
        if (z1 <= z2) {
            return z1 <= lz + buffer && z2 + buffer >= lz;
        } else return z1 + buffer >= lz && z2 <= lz + buffer;
    }
}