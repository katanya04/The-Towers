package mx.towers.pato14.game;

import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.game.kits.Kits;
import mx.towers.pato14.game.tasks.*;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.cofresillos.RefillTask;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.List;

public class Game {
    private final String name;
    private final GameTeams teams;
    private final Start gameStart;
    private final Finish finish;
    private final Timer timer;
    private final StatisticsPlayer stats;
    private final Move detectionMove;
    private GameState gameState;
    private final Kits kits;
    private final HashMap<HumanEntity, Kit> playersSelectedKit;
    private boolean goldenGoal;
    private boolean bedwarsStyle;
    private final RefillTask refill;
    private final Generators generators;

    public Game(GameInstance game) {
        this.name = game.getInternalName();
        this.gameState = GameState.LOBBY;
        this.kits = new Kits(game);
        this.playersSelectedKit = new HashMap<>();
        this.teams = new GameTeams(game);
        this.gameStart = new Start(game);
        this.timer = new Timer(game);
        this.finish = new Finish(game);
        this.stats = new StatisticsPlayer();
        this.detectionMove = new Move(game, this);
        this.bedwarsStyle = false;
        this.goldenGoal = false;
        this.refill = new RefillTask(game);
        this.generators = new Generators(game.getWorld().getName());
    }

    public StatisticsPlayer getStats() {
        return this.stats;
    }

    public GameTeams getTeams() {
        return this.teams;
    }

    public Start getStart() {
        return this.gameStart;
    }

    public Finish getFinish() {
        return this.finish;
    }

    public Move getDetectionMove() {
        return this.detectionMove;
    }
    public GameInstance getGameInstance() {
        return AmazingTowers.getGameInstance(this.name);
    }

    public List<Player> getPlayers() {
        return this.getGameInstance().getWorld().getPlayers();
    }

    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Kits getKits() {
        return kits;
    }

    public HashMap<HumanEntity, Kit> getPlayersSelectedKit() {
        return playersSelectedKit;
    }

    public void applyKitToPlayer(HumanEntity player) {
        Kit kit = this.getPlayersSelectedKit().get(player);
        if (kit == null || !this.getGameInstance().getRules().get(Rule.KITS))
            getKits().getDefaultKit().applyKitToPlayer(player);
        else
            kit.applyKitToPlayer(player);
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean isGoldenGoal() {
        return goldenGoal;
    }

    public void setGoldenGoal(boolean goldenGoal) {
        this.goldenGoal = goldenGoal;
    }

    public void setBedwarsStyle(boolean bedwarsStyle) {
        this.bedwarsStyle = bedwarsStyle;
        if (bedwarsStyle) {
            for (Team team : this.getTeams().getTeams())
                team.setPoints(Integer.parseInt(this.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("points.livesBedwarsMode")));
        }
    }

    public boolean isBedwarsStyle() {
        return bedwarsStyle;
    }

    public RefillTask getRefill() {
        return refill;
    }

    public Generators getGenerators() {
        return generators;
    }

    public void reset() {
        this.gameState = GameState.LOBBY;
        this.kits.resetTemporalBoughtKits();
        this.playersSelectedKit.clear();
        this.gameStart.setHasStarted(false);
        this.gameStart.setCountDown(this.getGameInstance().getConfig(ConfigType.CONFIG).getInt("options.gameStart.timerStart"));
        this.gameStart.setRunFromCommand(false);
        this.finish.setSeconds(this.getGameInstance().getConfig(ConfigType.CONFIG).getInt("options.timerEndSeconds") + 1);
        this.teams.reset();
        this.stats.clear();
        if (this.timer.getBossBars() != null && !this.timer.getBossBars().isEmpty())
            this.timer.removeAllBossBars();
        this.timer.update(this.getGameInstance());
        this.bedwarsStyle = false;
        this.goldenGoal = false;
    }

    public void spawn(Player player) {
        Utils.resetPlayer(player);
        NametagEdit.getApi().clearNametag(player);
        Team team = this.getTeams().getTeamByPlayer(player.getName());
        if (team == null || gameState == GameState.LOBBY || gameState == GameState.PREGAME) {
            player.setGameMode(GameMode.ADVENTURE);
            this.getGameInstance().getHotbarItems().giveHotbarItems(player);
            NametagEdit.getApi().setPrefix(player, Utils.getColor(TeamColor.SPECTATOR.getColor()));
            player.teleport(Locations.getLocationFromString(this.getGameInstance().getConfig(ConfigType.LOCATIONS)
                    .getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
        } else
            team.joinTeam(player);
    }

    public void joinGame(Player player) {
        spawn(player);
        Team team = this.getTeams().getTeamByPlayer(player.getName());
        switch (this.getGameState()) {
            case LOBBY:
            case PREGAME:
                break;
            case GAME:
                if (this.getTimer().isActivated())
                    this.getTimer().addPlayer(player);
                break;
            default:
                if (team == null || team.isEliminated())
                    player.setGameMode(GameMode.SPECTATOR);
                break;
        }
    }

    public void leave(Player player) {
        final Team playerTeam = this.getTeams().getTeamByPlayer(player.getName());
        switch (this.getGameState()) {
            case LOBBY:
            case PREGAME:
                if (playerTeam != null)
                    playerTeam.removePlayer(player.getName());
                break;
            case GAME:
            case GOLDEN_GOAL:
                if (this.getTimer().isActivated())
                    this.getTimer().removeBossBar(player);
                if (playerTeam == null)
                    break;
                playerTeam.setPlayerState(player.getName(), playerTeam.respawnPlayers() ? PlayerState.OFFLINE : PlayerState.NO_RESPAWN);
                if (playerTeam.getSizeOnlinePlayers() <= 0)
                    Utils.checkForTeamWin(this.getGameInstance());
                break;
        }
    }

    public void start() {
        this.setGameState(GameState.PREGAME);
        this.getStart().gameStart();
    }
}