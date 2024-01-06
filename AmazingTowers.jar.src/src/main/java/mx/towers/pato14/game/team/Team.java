package mx.towers.pato14.game.team;

import com.nametagedit.plugin.NametagEdit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class Team implements Comparable<Team> {
    private final TeamColor teamColor;
    private String prefix;
    private final HashMap<String, PlayerState> players;
    private int points;
    private final GameInstance gameInstance;
    private boolean eliminated;

    public Team(TeamColor teamColor, GameInstance gameInstance) {
        points = 0;
        this.players = new HashMap<>();
        this.teamColor = teamColor;
        this.gameInstance = gameInstance;
        this.eliminated = false;
    }

    public void joinTeam(Player player) {
        this.setNameTagPlayer(player);
        gameInstance.getGame().getStats().setHashStats(player.getName());
        if (this.respawnPlayers()) {
            gameInstance.getGame().applyKitToPlayer(player);
            this.setPlayerState(player.getName(), PlayerState.ONLINE);
            player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS)
                    .getString(Location.SPAWN.getPath(this.getTeamColor()))), PlayerTeleportEvent.TeleportCause.COMMAND);
            player.setGameMode(GameMode.SURVIVAL);
        } else {
            this.setPlayerState(player.getName(), PlayerState.NO_RESPAWN);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS)
                    .getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }

    public void removePlayer(String playerName) {
        this.players.remove(playerName);
        gameInstance.getHotbarItems().getSelectTeam().getItemByTeam(this.teamColor).removePlayerNameToTeamItem(playerName);
        gameInstance.getGame().getTeams().updatePlayersAmount();
    }

    public void addPlayer(String playerName) {
        Team currentTeam;
        if ((currentTeam = gameInstance.getGame().getTeams().getTeamByPlayer(playerName)) != null)
            currentTeam.removePlayer(playerName);
        this.players.put(playerName, PlayerState.ONLINE);
        gameInstance.getHotbarItems().getSelectTeam().getItemByTeam(this.teamColor).addPlayerNameToTeamItem(playerName);
        gameInstance.getGame().getTeams().updatePlayersAmount();
    }

    public void setNameTagPlayer(Player player) {
        NametagEdit.getApi().setPrefix(player, ChatColor.translateAlternateColorCodes('&', this.prefix));
    }

    public int getSizePlayers() {
        return this.players.size();
    }
    public int getSizeOnlinePlayers() {
        int i = 0;
        for (Map.Entry<String, PlayerState> player : players.entrySet()) {
            if (player.getValue() == PlayerState.ONLINE)
                i++;
        }
        return i;
    }

    public boolean containsPlayer(String name) {
        return this.players.get(name) != null;
    }

    public boolean containsPlayerOnline(String name) {
        return this.players.get(name) == PlayerState.ONLINE;
    }
    public TeamColor getTeamColor() {
        return this.teamColor;
    }

    public String getPrefixTeam() {
        return this.prefix;
    }

    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void scorePoint(boolean bedwarsStyle) {
        if (bedwarsStyle)
            this.points--;
        else
            this.points++;
    }

    public boolean respawnPlayers() {
        return !eliminated && (!gameInstance.getRules().get(Rule.BEDWARS_STYLE) || this.getPoints() != 0);
    }

    public void setPlayerState(String playerName, PlayerState playerState) {
        this.players.put(playerName, playerState);
        gameInstance.getGame().getTeams().updatePlayersAmount();
    }

    public PlayerState getPlayerState(String playerName) {
        return this.players.get(playerName);
    }

    public List<Player> getListOnlinePlayers() {
        List<Player> toret = new ArrayList<>();
        for (String playerName : players.keySet()) {
            toret.add(Bukkit.getPlayer(playerName));
        }
        return toret;
    }

    public void eliminateTeam() {
        Player player;
        for (String playerName : players.keySet()) {
            setPlayerState(playerName, PlayerState.NO_RESPAWN);
            if ((player = Bukkit.getPlayer(playerName)) != null) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())));
                player.sendMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("goldenGoal.eliminated"));
            }
        }
        gameInstance.getGame().getTeams().updatePlayersAmount();
        this.eliminated = true;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void reset() {
        for (String playerName : this.players.keySet())
            gameInstance.getHotbarItems().getSelectTeam().getItemByTeam(this.teamColor).removePlayerNameToTeamItem(playerName);
        players.clear();
        points = 0;
        eliminated = false;
    }

    @Override
    public int compareTo(@NotNull Team o) {
        return this.points - o.points;
    }
}