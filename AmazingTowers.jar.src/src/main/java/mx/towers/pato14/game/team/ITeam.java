package mx.towers.pato14.game.team;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ITeam extends Comparable<ITeam> {
    void changeTeam(Player player);
    void respawn(Player player);
    void removePlayer(String playerName);
    void addPlayer(String playerName);
    int getNumPlayers();
    int getNumOnlinePlayers();
    int getNumAlivePlayers();
    Set<String> getPlayers();
    Set<Player> getOnlinePlayers();
    Set<Player> getAlivePlayers();
    boolean containsPlayer(String name);
    boolean containsOnlinePlayer(Player player);
    TeamColor getTeamColor();
    void setPrefix(String prefix);
    String getPrefix();
    int getPoints();
    int getLives();
    void setPoints(int points);
    void setLives(int lives);
    void scorePoint();
    void gotScored();
    void eliminateTeam();
    boolean isEliminated();
    boolean doPlayersRespawn();
    void reset();
    void updatePrefix();
    void clear();
}
