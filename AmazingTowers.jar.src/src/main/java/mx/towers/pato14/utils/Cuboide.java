package mx.towers.pato14.utils;

import mx.towers.pato14.utils.locations.IntCoordinate;
import mx.towers.pato14.utils.locations.Pool;
import org.bukkit.Location;

public class Cuboide {
    public static boolean InCuboide(Location corner1, Location corner2, Location loc) {
        final int x1 = (int) corner1.getX(), x2 = (int) corner2.getX();
        final int y1 = (int) corner1.getY(), y2 = (int) corner2.getY();
        final int z1 = (int) corner1.getZ(), z2 = (int) corner2.getZ();
        final int lx = (int) Math.floor(loc.getX()), ly = (int) Math.floor(loc.getY()), lz = (int) Math.floor(loc.getZ());
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
            if ( z1 > lz || z2 < lz) return false;
        } else if ( z1 < lz || z2 > lz) {
            return false;
        }
        return true;
    }
    public static boolean InCuboide(Pool pool, Location loc) {
        return InCuboide(pool.getCorner1(), pool.getCorner2(),
                new IntCoordinate((int) Math.floor(loc.getX()),(int) Math.floor(loc.getY()),(int) Math.floor(loc.getZ())));
    }

    public static boolean InCuboideExtraHeight(Pool pool, Location loc, int extraHeight) {
        return InCuboideExtraHeight(pool.getCorner1(), pool.getCorner2(),
                new IntCoordinate((int) Math.floor(loc.getX()),(int) Math.floor(loc.getY()),(int) Math.floor(loc.getZ())),
                extraHeight);
    }

    public static boolean InCuboide(IntCoordinate corner1, IntCoordinate corner2, Location loc) {
        return InCuboide(corner1, corner2, new IntCoordinate((int) Math.floor(loc.getX()),(int) Math.floor(loc.getY()),(int) Math.floor(loc.getZ())));
    }

    public static boolean InCuboide(IntCoordinate corner1, IntCoordinate corner2, IntCoordinate loc) {
        final int x1 = corner1.getX(), x2 = corner2.getX(), lx = loc.getX();
        final int y1 = corner1.getY(), y2 = corner2.getY(), ly = loc.getY();
        final int z1 = corner1.getZ(), z2 = corner2.getZ(), lz = loc.getZ();
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
    public static boolean InCuboideExtraHeight(IntCoordinate corner1, IntCoordinate corner2, IntCoordinate loc, int ExtraHeight) {
        final int x1 = corner1.getX(), x2 = corner2.getX(), lx = loc.getX();
        final int y1 = corner1.getY(), y2 = corner2.getY(), ly = loc.getY();
        final int z1 = corner1.getZ(), z2 = corner2.getZ(), lz = loc.getZ();
        if (x1 <= x2) {
            if (x1 > lx + ExtraHeight || x2 + ExtraHeight < lx)
                return false;
        } else if (x1 + ExtraHeight < lx || x2 > lx + ExtraHeight) {
            return false;
        }
        if (y1 <= y2) {
            if (y1 > ly + ExtraHeight || y2 + ExtraHeight < ly)
                return false;
        } else if (y1 + ExtraHeight < ly || y2 > ly + ExtraHeight) {
            return false;
        }
        if (z1 <= z2) {
            return z1 <= lz + ExtraHeight && z2 + ExtraHeight >= lz;
        } else return z1 + ExtraHeight >= lz && z2 <= lz + ExtraHeight;
    }

    public static boolean InCuboideExtraHeight(Location corner1, Location corner2, Location loc, int ExtraHeight) {
        final int x1 = (int) corner1.getX(), x2 = (int) corner2.getX();
        final int y1 = (int) corner1.getY(), y2 = (int) corner2.getY();
        final int z1 = (int) corner1.getZ(), z2 = (int) corner2.getZ();
        final int lx = (int) Math.floor(loc.getX()), ly = (int) Math.floor(loc.getY()), lz = (int) Math.floor(loc.getZ());
        if (x1 <= x2) {
            if (x1 > lx + ExtraHeight || x2 + ExtraHeight < lx)
                return false;
        } else if (x1 + ExtraHeight < lx || x2 > lx + ExtraHeight) {
            return false;
        }
        if (y1 <= y2) {
            if (y1 > ly + ExtraHeight || y2 + ExtraHeight < ly)
                return false;
        } else if (y1 + ExtraHeight < ly || y2 > ly + ExtraHeight) {
            return false;
        }
        if (z1 <= z2) {
            return z1 <= lz + ExtraHeight && z2 + ExtraHeight >= lz;
        } else return z1 + ExtraHeight >= lz && z2 <= lz + ExtraHeight;
    }

    public static boolean InCuboideExtraHeight(String string, Location loc, int extraHeight) {
        return InCuboideExtraHeight(IntCoordinate.getFromString(string + ".1"), IntCoordinate.getFromString(string + ".2"), new IntCoordinate((int) Math.floor(loc.getX()),(int) Math.floor(loc.getY()),(int) Math.floor(loc.getZ())), extraHeight);
    }

    public static boolean InCuboide(String string, Location loc) {
        return InCuboide(IntCoordinate.getFromString(string + ".1"), IntCoordinate.getFromString(string + ".2"), new IntCoordinate((int) Math.floor(loc.getX()),(int) Math.floor(loc.getY()),(int) Math.floor(loc.getZ())));
    }
}


