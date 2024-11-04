package mx.towers.pato14.game.team;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.items.Items;
import mx.towers.pato14.utils.items.ItemsEnum;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.GameMode;

import java.util.Set;
import java.util.TreeSet;

public class TeamImpl implements Team {
    private final Set<String> players;
    private String prefix;
    private final TeamColor teamColor;
    private int points;
    private int lives;
    private final GameTeams gameTeams;
    private boolean eliminated;
    public TeamImpl(TeamColor teamColor, String prefix, GameTeams gameTeams, int lives) {
        this.prefix = prefix;
        this.teamColor = teamColor;
        this.points = 0;
        this.lives = lives;
        this.gameTeams = gameTeams;
        this.eliminated = false;
        this.players = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public GameInstance getGameInstance() {
        return gameTeams.getGame().getGameInstance();
    }

    @Override
    public void removePlayer(String playerName) {
        this.players.remove(playerName);
        Prefixes.clearPrefix(playerName);
        Items.updateMenu(ItemsEnum.TEAM_SELECT);
        this.gameTeams.updatePlayersAmount();
    }

    @Override
    public void addPlayer(String playerName) {
        Team currentTeam;
        if ((currentTeam = this.gameTeams.getTeamByPlayer(playerName)) != null)
            currentTeam.removePlayer(playerName);
        this.players.add(playerName);
        Prefixes.setPrefix(playerName, prefix);
        Items.updateMenu(ItemsEnum.TEAM_SELECT);
        this.gameTeams.updatePlayersAmount();
    }

    @Override
    public Set<String> getPlayers() {
        return this.players;
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
    public int getLives() {
        return lives;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void setLives(int lives) {
        this.lives = lives;
    }

    @Override
    public void eliminate() {
        getOnlinePlayers().forEach(pl -> {
            pl.setGameMode(GameMode.SPECTATOR);
            pl.teleport(Locations.getLocationFromString(getGameInstance().getConfig(ConfigType.LOCATIONS)
                    .getString(Location.LOBBY.getPath())));
            pl.sendMessage(getGameInstance().getConfig(ConfigType.MESSAGES).getString("goldenGoal.eliminated"));
        });
        gameTeams.updatePlayersAmount();
        this.eliminated = true;
    }

    @Override
    public boolean isEliminated() {
        return this.eliminated;
    }
}