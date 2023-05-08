package mx.towers.pato14.game.team;

import com.nametagedit.plugin.NametagEdit;

import java.util.ArrayList;

import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Team {
    private final TeamColor teamColor;
    private String prefix;
    private final String nameTeam;
    private final ArrayList<String> players;
    private final ArrayList<String> offlinePlayers;
    private int points;

    public Team(String nameTeam) {
        points = 0;
        this.nameTeam = nameTeam;
        this.players = new ArrayList<>();
        this.offlinePlayers = new ArrayList<>();
        this.teamColor = TeamColor.valueOf(nameTeam.toUpperCase());
    }

    public void addPlayer(OfflinePlayer player) {
        this.players.add(player.getName());
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getName());
    }

    public void setNameTagPlayer(Player player) {
        NametagEdit.getApi().setPrefix(player, ChatColor.translateAlternateColorCodes('&', this.prefix));
    }

    public void ClearNameTagPlayer(Player player) {
        NametagEdit.getApi().clearNametag(player);
    }

    public int getSizePlayers() {
        return this.players.size();
    }

    public boolean containsPlayer(String name) {
        return this.players.contains(name);
    }
    public TeamColor getTeamColor() {
        return this.teamColor;
    }
    public String getNameTeam() {
        return this.nameTeam;
    }

    public String getPrefixTeam() {
        return this.prefix;
    }

    public ArrayList<String> getTeam() {
        return this.players;
    }

    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public ArrayList<String> getPlayersOfflineTeam() {
        return this.offlinePlayers;
    }

    public boolean containsOffline(String namePlayer) {
        return this.offlinePlayers.contains(namePlayer);
    }

    public void addOfflinePlayer(String namePlayer) {
        if (!containsOffline(namePlayer)) {
            this.offlinePlayers.add(namePlayer);
        }
    }

    public void removeOfflinePlayer(String namePlayer) {
        if (containsOffline(namePlayer)) {
            this.offlinePlayers.remove(namePlayer);
            this.players.add(namePlayer);
        }
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void sumarPunto() {
        this.points++;
    }
}


