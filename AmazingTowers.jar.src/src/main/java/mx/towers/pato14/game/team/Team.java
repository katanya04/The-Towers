package mx.towers.pato14.game.team;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Team implements ITeam {
    private final Set<String> players;
    private String prefix;
    private final TeamColor teamColor;
    private int points;
    private final GameTeams gameTeams;
    private boolean eliminated;
    public Team(TeamColor teamColor, String prefix, GameTeams gameTeams) {
        this.prefix = prefix;
        this.teamColor = teamColor;
        this.points = 0;
        this.gameTeams = gameTeams;
        this.eliminated = false;
        this.players = new HashSet<>();
    }

    @Override
    public void changeTeam(Player player) {
        GameInstance gameInstance = this.gameTeams.getGame().getGameInstance();
        Config messages = gameInstance.getConfig(ConfigType.MESSAGES);
        Game game = gameInstance.getGame();
        ITeam currentTeam = game.getTeams().getTeamByPlayer(player.getName());
        if (!this.containsPlayer(player.getName())) { //Si no está ya en ese equipo
            if (!gameInstance.getRules().get(Rule.BALANCED_TEAMS)
                    || this.getNumPlayers() == game.getTeams().getLowestTeamPlayers(currentTeam)) {
                this.addPlayer(player.getName());
                if (game.getGameState().equals(GameState.GAME))
                    gameInstance.getGame().spawn(player);
                player.sendMessage(Utils.getColor(messages.getString("selectTeam")
                        .replace("{Color}", this.getTeamColor().getColor())
                        .replace("{Team}", this.getTeamColor().getName(game.getGameInstance()))));
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                player.closeInventory();
            } else {
                player.sendMessage(Utils.getColor(messages.getString("unbalancedTeam")));
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
            }
        } else {
            player.sendMessage(Utils.getColor(messages.getString("alreadyJoinedTeam")
                    .replace("{Color}", this.getTeamColor().getColor())
                    .replace("{Team}", this.getTeamColor().getName(game.getGameInstance()))));
        }
    }

    @Override
    public void respawn(Player player) {
        GameInstance gameInstance = this.gameTeams.getGame().getGameInstance();
        gameInstance.getGame().getStats().setHashStats(player.getName());
        if (this.doPlayersRespawn()) {
            gameInstance.getGame().applyKitToPlayer(player);
            player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS)
                    .getString(Location.SPAWN.getPath(this.getTeamColor()))), PlayerTeleportEvent.TeleportCause.COMMAND);
            player.setGameMode(GameMode.SURVIVAL);
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS)
                    .getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }

    @Override
    public void removePlayer(String playerName) {
        this.players.remove(playerName);
        Prefixes.clearPrefix(Bukkit.getPlayer(playerName));
        ActionItem.getByName("JoinTeam." + teamColor).getParent().updateContents();
        this.gameTeams.updatePlayersAmount();
    }

    @Override
    public void addPlayer(String playerName) {
        ITeam currentTeam;
        if ((currentTeam = this.gameTeams.getTeamByPlayer(playerName)) != null)
            currentTeam.removePlayer(playerName);
        this.players.add(playerName);
        Prefixes.setPrefix(Bukkit.getPlayer(playerName), prefix);
        ActionItem.getByName("JoinTeam." + teamColor).getParent().updateContents();
        this.gameTeams.updatePlayersAmount();
    }

    @Override
    public int getNumPlayers() {
        return this.players.size();
    }

    @Override
    public int getNumOnlinePlayers() {
        return this.getOnlinePlayers().size();
    }

    @Override
    public int getNumAlivePlayers() {
        return this.doPlayersRespawn() ? getNumOnlinePlayers() :
                this.getOnlinePlayers().stream().filter(o -> o.getGameMode() != GameMode.SPECTATOR).collect(Collectors.toSet()).size();
    }

    @Override
    public Set<Player> getOnlinePlayers() {
        return this.players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull)
                .filter(o -> o.getWorld().equals(this.gameTeams.getGame().getGameInstance().getWorld())).collect(Collectors.toSet());
    }

    @Override
    public boolean containsPlayer(String name) {
        return this.players.contains(name);
    }

    @Override
    public boolean containsOnlinePlayer(Player player) {
        return containsPlayer(player.getName());
    }

    @Override
    public TeamColor getTeamColor() {
        return this.teamColor;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void scorePoint(boolean bedwarsStyle) {
        if (bedwarsStyle)
            this.points--;
        else
            this.points++;
    }

    @Override
    public void eliminateTeam() {
        Player player;
        GameInstance gameInstance = this.gameTeams.getGame().getGameInstance();
        for (String playerName : this.players) {
            if ((player = Bukkit.getPlayer(playerName)) != null) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())));
                player.sendMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("goldenGoal.eliminated"));
            }
        }
        this.gameTeams.updatePlayersAmount();
        this.eliminated = true;
    }

    @Override
    public boolean isEliminated() {
        return this.eliminated;
    }

    @Override
    public boolean doPlayersRespawn() {
        GameInstance gameInstance = this.gameTeams.getGame().getGameInstance();
        return (!gameInstance.getRules().get(Rule.BEDWARS_STYLE) || this.points != 0) && !isEliminated();
    }

    @Override
    public void reset() {
        this.players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(Prefixes::clearPrefix);
        this.players.clear();
        this.points = 0;
        this.eliminated = false;
    }

    @Override
    public void updatePrefix() {
        this.players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(o -> Prefixes.setPrefix(o, this.prefix));
    }
}