package mx.towers.pato14.game.team;

import com.nametagedit.plugin.NametagEdit;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Team {
    private mx.towers.pato14.utils.enums.Team teamColor;
    private String prefix;
    private String nameTeam;
    private ArrayList<String> team;
    private ArrayList<String> offlinePlayer;

    public Team(String nameTeam) {
        this.nameTeam = nameTeam;
        this.team = new ArrayList<>();
        this.offlinePlayer = new ArrayList<>();
        this.teamColor = mx.towers.pato14.utils.enums.Team.valueOf(nameTeam.toUpperCase());
    }

    public void addPlayer(OfflinePlayer player) {
        this.team.add(player.getName());
    }

    public void removePlayer(Player player) {
        this.team.remove(player.getName());
    }

    public void setNameTagPlayer(Player player) {
        NametagEdit.getApi().setPrefix(player, ChatColor.translateAlternateColorCodes('&', this.prefix));
    }

    public void ClearNameTagPlayer(Player player) {
        NametagEdit.getApi().clearNametag(player);
    }

    public int getSizePlayers() {
        return this.team.size();
    }

    public boolean containsPlayer(String name) {
        return this.team.contains(name);
    }
    public mx.towers.pato14.utils.enums.Team getTeamColor() {
        return this.teamColor;
    }
    public String getNameTeam() {
        return this.nameTeam;
    }

    public String getPrefixTeam() {
        return this.prefix;
    }

    public ArrayList<String> getTeam() {
        return this.team;
    }

    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public ArrayList<String> getPlayersOfflineTeam() {
        return this.offlinePlayer;
    }

    public boolean containsOffline(String namePlayer) {
        return this.offlinePlayer.contains(namePlayer);
    }

    public void addOfflinePlayer(String namePlayer) {
        if (!containsOffline(namePlayer)) {
            this.offlinePlayer.add(namePlayer);
        }
    }

    public void removeOfflinePlayer(String namePlayer) {
        if (containsOffline(namePlayer)) {
            this.offlinePlayer.remove(namePlayer);
            this.team.add(namePlayer);
        }
    }
}


