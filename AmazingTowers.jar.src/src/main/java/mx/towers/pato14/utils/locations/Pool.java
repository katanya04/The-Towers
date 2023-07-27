package mx.towers.pato14.utils.locations;

import mx.towers.pato14.game.team.Team;

import java.util.List;

public class Pool {
    private final Team team;
    private final IntCoordinate corner1;
    private final IntCoordinate corner2;
    public Pool(Team teamColor, List<String> location) {
        this.team = teamColor;
        this.corner1 = IntCoordinate.getFromString(location.get(0));
        this.corner2 = IntCoordinate.getFromString(location.get(1));
    }
    public IntCoordinate getCorner1() {
        return corner1;
    }
    public IntCoordinate getCorner2() {
        return corner2;
    }
    public void setCorner1(int x, int y, int z) {
        this.corner1.setCoordinate(x, y, z);
    }
    public void setCorner2(int x, int y, int z) {
        this.corner2.setCoordinate(x, y, z);
    }
    public Team getTeam() {
        return team;
    }
}
