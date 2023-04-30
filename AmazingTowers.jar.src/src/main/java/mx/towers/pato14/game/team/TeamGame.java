package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class TeamGame {
    private Team red;
    private Team blue;
    public int redPoints = 0;
    public int bluePoints = 0;
    private final AmazingTowers a;
    private final String PREFIX_RED;
    private final String PREFIX_BLUE;
    private final List<Team> teams = new ArrayList<>();

    public TeamGame(AmazingTowers a) {
        this.a = a;
        this.PREFIX_RED = getPlugin().getConfig().getString("Options.team.red.prefix");
        this.PREFIX_BLUE = getPlugin().getConfig().getString("Options.team.blue.prefix");
        this.red = new Team("red");
        this.red.setPrefix(this.PREFIX_RED);
        this.blue = new Team("blue");
        this.blue.setPrefix(this.PREFIX_BLUE);
        teams.add(red);
        teams.add(blue);
    }

    public void removePlayer(Player player) {
        if (this.red.containsPlayer(player.getName())) {
            this.red.removePlayer(player);
        } else if (this.blue.containsPlayer(player.getName())) {
            this.blue.removePlayer(player);
        }
    }

    public boolean containsTeamPlayer(String namePlayer) {
        if (this.red.containsPlayer(namePlayer))
            return true;
        if (this.blue.containsPlayer(namePlayer)) {
            return true;
        }
        return false;
    }

    public boolean containsTeamPlayer(Player player) {
        if (this.red.containsPlayer(player.getName()))
            return true;
        if (this.blue.containsPlayer(player.getName())) {
            return true;
        }
        return false;
    }

    public Team getRed() {
        return this.red;
    }

    public Team getBlue() {
        return this.blue;
    }
    public Team getTeam(mx.towers.pato14.utils.enums.Team team) {
        for (Team t: teams) {
            if (t.getTeamColor().equals(team))
                return t;
        }
        return null;
    }

    private AmazingTowers getPlugin() {
        return this.a;
    }
}


