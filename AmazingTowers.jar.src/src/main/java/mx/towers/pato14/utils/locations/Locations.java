package mx.towers.pato14.utils.locations;

import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.Locationshion;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Locations {
    public static String getLocationStringCenter(Location loc, boolean yawPitch) {
        StringBuilder toret = new StringBuilder();
        toret.append(loc.getWorld().getName());
        toret.append(";").append((int) loc.getX()).append(".5");
        toret.append(";").append((int) loc.getY());
        toret.append(";").append((int) loc.getZ()).append(".5");
        if (yawPitch) {
            toret.append(";").append(loc.getYaw());
            toret.append(";").append(loc.getPitch());
        }
        return toret.toString();
    }

    public static String getLocationStringBlock(Location loc) {
        return loc.getWorld().getName() +
                ";" + (int) loc.getX() +
                ";" + (int) loc.getY() +
                ";" + (int) loc.getZ();
    }

    public static Location getLocationFromStringConfig(Config config, Locationshion locations) {
        Location loc;
        String[] a = config.getString(locations.getLocationString()).split(";");
        if (a.length >= 6) {
            loc = new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]), Float.parseFloat(a[4]), Float.parseFloat(a[5]));
        } else {
            loc = new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]));
        }
        return loc;
    }

    public static Location getLocationFromString(String st) {
        Location loc;
        String[] a = st.split(";");
        if (a.length >= 6) {
            loc = new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]), Float.parseFloat(a[4]), Float.parseFloat(a[5]));
        } else {
            loc = new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]));
        }
        return loc;
    }
}


