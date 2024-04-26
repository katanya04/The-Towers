package mx.towers.pato14.game;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.game.kits.Kits;
import mx.towers.pato14.game.tasks.*;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.game.team.Prefixes;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.game.refill.RefillTask;
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
    private final RefillTask refill;
    private final Generators generators;
    private final CaptainsPhase captainsPhase;

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
        this.refill = new RefillTask(game);
        this.generators = new Generators(game.getWorld().getName());
        this.captainsPhase = new CaptainsPhase(game);
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
    public CaptainsPhase getCaptainsPhase() {
        return captainsPhase;
    }
    public HashMap<HumanEntity, Kit> getPlayersSelectedKit() {
        return playersSelectedKit;
    }

    public void applyKitToPlayer(Player player) {
        Kit kit = this.getPlayersSelectedKit().get(player);
        if (kit == null || !this.getGameInstance().getRules().get(Rule.KITS))
            getKits().getDefaultKit().applyKitToPlayer(player);
        else
            kit.applyKitToPlayer(player);
    }

    public Timer getTimer() {
        return timer;
    }

    public RefillTask getRefill() {
        return refill;
    }

    public Generators getGenerators() {
        return generators;
    }

    public void endMatch() {
        switch (gameState) {
            case GAME:
                getFinish().endMatchOrGoldenGoal();
                break;
            case EXTRA_TIME:
                getFinish().endMatch();
                break;
            default:
                break;
        }
    }

    public void reset() {
        this.gameState = GameState.LOBBY;
        this.kits.resetTemporalBoughtKits();
        this.playersSelectedKit.clear();
        this.gameStart.reset();
        this.finish.setSeconds(this.getGameInstance().getConfig(ConfigType.CONFIG).getInt("options.timerEndSeconds") + 1);
        this.teams.reset();
        this.stats.clear();
        if (this.timer.getBossBars() != null && !this.timer.getBossBars().isEmpty())
            this.timer.removeAllBossBars();
        this.timer.update(this.getGameInstance());
    }

    public void spawn(Player player) {
        Utils.resetPlayer(player);
        ITeam team = this.getTeams().getTeamByPlayer(player.getName());
        if (team == null || gameState == GameState.LOBBY || gameState == GameState.PREGAME) {
            player.setGameMode(GameMode.ADVENTURE);
            this.getGameInstance().getHotbar().apply(player);
            player.teleport(Locations.getLocationFromString(this.getGameInstance().getConfig(ConfigType.LOCATIONS)
                    .getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
        } else
            team.respawn(player);
    }

    public void joinGame(Player player) {
        spawn(player);
        if (this.teams != null)
            this.teams.updatePrefixes();
        ITeam team = this.getTeams().getTeamByPlayer(player.getName());
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
        final ITeam playerTeam = this.getTeams().getTeamByPlayer(player.getName());
        Prefixes.clearPrefix(player.getName());
        this.teams.updatePlayersAmount();
        switch (this.getGameState()) {
            case LOBBY:
            case PREGAME:
                if (playerTeam != null)
                    playerTeam.removePlayer(player.getName());
                break;
            case GAME:
            case EXTRA_TIME:
                if (this.getTimer().isActivated())
                    this.getTimer().removeBossBar(player);
                if (playerTeam == null)
                    break;
                if (playerTeam.getNumAlivePlayers() <= 0)
                    Utils.checkForTeamWin(this.getGameInstance());
                break;
        }
    }

    public void start() {
        this.setGameState(GameState.PREGAME);
        this.getStart().gameStart();
    }
}