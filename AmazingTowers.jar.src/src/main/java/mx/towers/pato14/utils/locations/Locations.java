package mx.towers.pato14.utils.locations;

import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.AreaUtil;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.game.team.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
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
        String path = mx.towers.pato14.utils.enums.Location.PROTECTED.getPath();
        @SuppressWarnings("unchecked")
        List<List<String>> protectedAreas = locations.getList(path) == null ? new ArrayList<>() :
                locations.getList(path).stream().filter(o -> o instanceof List).map(o -> (List<String>) o)
                        .collect(Collectors.toList());
        for (List<String> area : protectedAreas) {
            if (AreaUtil.isInsideArea(area, loc, extraHeight))
                return true;
        }
        return false;
    }

    public static boolean isInsidePoolRoom(Config locations, Location loc, int extraHeight, int numTeams) {
        List<String> poolRoom;
        for (TeamColor teamColor : TeamColor.getMatchTeams(numTeams)) {
            poolRoom = locations.getStringList(mx.towers.pato14.utils.enums.Location.POOL_ROOM.getPath(teamColor));
            if (AreaUtil.isInsideArea(poolRoom, loc, extraHeight))
                return true;
        }
        return false;
    }

    public static boolean isInsidePool(Location loc, Pool[] pools, int extraHeight) {
        for (Pool pool : pools) {
            if (AreaUtil.isInsideArea(pool, loc, extraHeight))
                return true;
        }
        return false;
    }

    public static boolean isValidLocation(Config locations, Location loc, Pool[] pools, boolean protectPointAllowed, boolean griefingAllowed, int extraHeight, int numTeams) {
        return (griefingAllowed || !isInsideProtectedArea(locations, loc, extraHeight))
                && AreaUtil.isInsideArea(locations.getStringList(mx.towers.pato14.utils.enums.Location.MAP_BORDER.getPath()), loc, extraHeight)
                && !isInsidePool(loc, pools, extraHeight + 1) && (protectPointAllowed || !isInsidePoolRoom(locations, loc, extraHeight, numTeams));
    }

    public static boolean isInsideBase(Location loc, GameTeams gameTeams) {
        final Config locations = gameTeams.getGame().getGameInstance().getConfig(ConfigType.LOCATIONS);
        for (Team team : gameTeams.getTeams()) {
            if (AreaUtil.isInsideArea(locations.getStringList(mx.towers.pato14.utils.enums.Location.CHEST_PROTECT.getPath(team.getTeamColor())), loc))
                return true;
        }
        return false;
    }
}