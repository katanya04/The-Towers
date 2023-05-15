package mx.towers.pato14.utils.locations;

import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

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

    public static Location getLocationFromStringConfig(Config config, mx.towers.pato14.utils.enums.Location locations) {
        Location loc;
        String[] a = config.getString(locations.getPath()).split(";");
        if (a.length >= 6) {
            loc = new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]), Float.parseFloat(a[4]), Float.parseFloat(a[5]));
        } else {
            loc = new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]));
        }
        return loc;
    }

    public static Location getLocationFromStringConfig(Config config, mx.towers.pato14.utils.enums.Location locations, TeamColor teamColor) {
        Location loc;
        String[] a = config.getString(locations.getPath(teamColor)).split(";");
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

    public static boolean isInsideProtectedArea(Config locations, Location loc, int extraHeight) {
        ConfigurationSection protectedAreas = locations.getConfigurationSection(mx.towers.pato14.utils.enums.Location.PROTECTED.getPath());
        for (String key : protectedAreas.getKeys(false)){
            String path = mx.towers.pato14.utils.enums.Location.PROTECTED.getPath() + "." + key;
            if (Cuboide.InCuboideExtraHeight(locations.getString(path), loc, extraHeight)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInsidePoolRoom(Config locations, Location loc, int extraHeight) {
        ConfigurationSection protectedAreas = locations.getConfigurationSection("LOCATIONS");
        for (String key : protectedAreas.getKeys(false)){
            if (key.equals("GENERAL"))
                continue;
            String path = "LOCATIONS" + "." + key + ".POOL_ROOM";
            if (Cuboide.InCuboideExtraHeight(locations.getString(path), loc, extraHeight)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInsidePool(Location loc, Pool[] pools, int extraHeight) {
        for (Pool pool : pools) {
            if (Cuboide.InCuboideExtraHeight(pool, loc, extraHeight))
                return true;
        }
        return false;
    }

    public static boolean isValidLocation(Config locations, Location loc, Pool[] pools, boolean protectPointAllowed, boolean griefingAllowed, int extraHeight) {
        return (griefingAllowed || !isInsideProtectedArea(locations, loc, extraHeight))
                && Cuboide.InCuboideExtraHeight(locations.getString(mx.towers.pato14.utils.enums.Location.MAP_BORDER.getPath()), loc, extraHeight)
                && !isInsidePool(loc, pools, extraHeight + 1) && (protectPointAllowed || !isInsidePoolRoom(locations, loc, extraHeight));
    }

    public static boolean isInsideBase(Location loc, GameTeams gameTeams) {
        for (Team team : gameTeams.getTeams()) {
            if (Cuboide.InCuboide(gameTeams.getGame().getGameInstance().getConfig(ConfigType.LOCATIONS).getString(mx.towers.pato14.utils.enums.Location.CHEST_PROTECT.getPath(team.getTeamColor())), loc))
                return true;
        }
        return false;
    }
}


