package mx.towers.pato14.utils;

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
            if (z1 > lz + ExtraHeight || z2 + ExtraHeight < lz)
                return false;
        } else if (z1 + ExtraHeight < lz || z2 > lz + ExtraHeight) {
            return false;
        }
        return true;
    }
}


