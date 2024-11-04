package mx.towers.pato14.game.team;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface Team extends Comparable<Team> {
    GameInstance getGameInstance();
    void removePlayer(String playerName);
    void addPlayer(String playerName);
    Set<String> getPlayers();
    TeamColor getTeamColor();
    void setPrefix(String prefix);
    String getPrefix();
    int getPoints();
    int getLives();
    void setPoints(int points);
    void setLives(int lives);
    void eliminate();
    boolean isEliminated();
    default Set<Player> getOnlinePlayers() {
        return getPlayers().stream().filter(Objects::nonNull).map(Bukkit::getPlayer).filter(Objects::nonNull)
                .filter(pl -> Objects.equals(pl.getWorld(), getGameInstance().getWorld())).collect(Collectors.toSet());
    }
    default Set<Player> getAlivePlayers() {
        return getOnlinePlayers().stream().filter(pl -> pl.getGameMode() != GameMode.SPECTATOR).collect(Collectors.toSet());
    }
    default boolean containsPlayer(String name) {
        return getPlayers().contains(name);
    }
    default boolean containsPlayer(Player player) {
        return containsPlayer(player.getName());
    }
    default int getNumPlayers() {
        return getPlayers().size();
    }
    default int getNumOnlinePlayers() {
        return getOnlinePlayers().size();
    }
    default int getNumAlivePlayers() {
        return getAlivePlayers().size();
    }
    default void scorePoint() {
        setPoints(getPoints() + 1);
    }
    default void gotScored() {
        setLives(getLives() - 1);
    }
    default void clear() {
        getPlayers().forEach(this::removePlayer);
    }
    default void updatePrefix() {
        getPlayers().forEach(o -> Prefixes.setPrefix(o, getPrefix()));
    }
    default boolean doPlayersRespawn() {
        return (!getGameInstance().getRules().get(Rule.BEDWARS_STYLE) || this.getLives() != 0) && !isEliminated();
    }
    default void reset() {
        getPlayers().forEach(Prefixes::clearPrefix);
        clear();
        setPoints(0);
        setLives(0);
    }

    @Override
    default int compareTo(@NotNull Team o) {
        return getGameInstance().getRules().get(Rule.BEDWARS_STYLE) ?
                this.getLives() - o.getLives() : this.getPoints() - o.getPoints();
    }
}
