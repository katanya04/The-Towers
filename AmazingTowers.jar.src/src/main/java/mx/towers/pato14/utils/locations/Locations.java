package mx.towers.pato14.utils.locations;

import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.Locationshion;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Locations {
    public static String getLocationStringCenter(Location loc, boolean yawPitch) {
        String a = "";
        a = String.valueOf(a) + loc.getWorld().getName();
        a = String.valueOf(a) + ";" + (int) loc.getX() + ".5";
        a = String.valueOf(a) + ";" + (int) loc.getY();
        a = String.valueOf(a) + ";" + (int) loc.getZ() + ".5";
        if (yawPitch) {
            a = String.valueOf(a) + ";" + loc.getYaw();
            a = String.valueOf(a) + ";" + loc.getPitch();
        }
        return a;
    }

    public static String getLocationStringBlock(Location loc) {
        String a = "";
        a = String.valueOf(a) + loc.getWorld().getName();
        a = String.valueOf(a) + ";" + (int) loc.getX();
        a = String.valueOf(a) + ";" + (int) loc.getY();
        a = String.valueOf(a) + ";" + (int) loc.getZ();
        return a;
    }

    public static Location getLocationFromStringConfig(Config config, Locationshion locations) {
        Location loc;
        String[] a = config.getString(locations.getLocationString()).split(";");
        if (a.length >= 6) {
            loc = new Location(Bukkit.getWorld(a[0]), Double.valueOf(a[1]).doubleValue(), Double.valueOf(a[2]).doubleValue(), Double.valueOf(a[3]).doubleValue(), Float.valueOf(a[4]).floatValue(), Float.valueOf(a[5]).floatValue());
        } else {
            loc = new Location(Bukkit.getWorld(a[0]), Double.valueOf(a[1]).doubleValue(), Double.valueOf(a[2]).doubleValue(), Double.valueOf(a[3]).doubleValue());
        }
        return loc;
    }

    public static Location getLocationFromString(String st) {
        Location loc;
        String[] a = st.split(";");
        if (a.length >= 6) {
            loc = new Location(Bukkit.getWorld(a[0]), Double.valueOf(a[1]).doubleValue(), Double.valueOf(a[2]).doubleValue(), Double.valueOf(a[3]).doubleValue(), Float.valueOf(a[4]).floatValue(), Float.valueOf(a[5]).floatValue());
        } else {
            loc = new Location(Bukkit.getWorld(a[0]), Double.valueOf(a[1]).doubleValue(), Double.valueOf(a[2]).doubleValue(), Double.valueOf(a[3]).doubleValue());
        }
        return loc;
    }
}


