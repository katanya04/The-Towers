package mx.towers.pato14.utils.locations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.enums.TeamColor;

public class Detectoreishon {
    private final List<String> nonExistentLocations = new ArrayList<>();
    private final GameInstance gameInstance;
    public Detectoreishon(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    public String getLocationsNeededString(boolean console) {
        StringBuilder names = new StringBuilder();
        Iterator<String> itr = nonExistentLocations.iterator();
        while (itr.hasNext()) {
            names.append(itr.next());
            if (itr.hasNext())
                names.append(", ");
        }
        if (console) {
            return this.nonExistentLocations.isEmpty() ? "§aLocations§f: §f[NONE]" : ("§aLocations§f: §f" + names.toString().toUpperCase());
        }
        return this.nonExistentLocations.isEmpty() ? "§aLocations§f: §f[NONE]" : ("§aLocations§f: §f" + names.toString().toLowerCase());
    }

    public boolean neededLocationsExist() {
        return this.nonExistentLocations.isEmpty();
    }

    public void checkNeededLocationsExistence(int numberOfTeams) {
        for (Location loc : Location.getObligatoryLocations()) {
            if (!loc.needsTeamColor()) {
                if (!loc.exists(this.gameInstance, null))
                    this.nonExistentLocations.add(loc.name().toLowerCase().replace('_', ' '));
            } else {
                for (TeamColor teamColor : TeamColor.getMatchTeams(numberOfTeams))
                    if (!loc.exists(this.gameInstance, teamColor))
                        this.nonExistentLocations.add(teamColor.name().toLowerCase() + " " + loc.name().toLowerCase().replace('_', ' '));
            }
        }
    }
}
