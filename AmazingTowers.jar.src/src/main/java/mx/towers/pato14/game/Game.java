package mx.towers.pato14.game;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.game.kits.Kits;
import mx.towers.pato14.game.tasks.Finish;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.tasks.Timer;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.utils.Book;
import mx.towers.pato14.game.tasks.Move;
import mx.towers.pato14.utils.cofresillos.RefillTask;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

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
    private Book bookItem;
    private GameState gameState;
    private final Kits kits;
    private final HashMap<HumanEntity, Kit> playersSelectedKit;
    private boolean goldenGoal;
    private boolean bedwarsStyle;
    private final RefillTask refill;

    public Game(GameInstance game) {
        this.name = game.getName();
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
    }

    public StatisticsPlayer getStats() {
        return this.stats;
    }

    public Book getItemBook() {
        return this.bookItem;
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
                team.setPoints(this.getGameInstance().getConfig(ConfigType.CONFIG).getInt("options.pointsToWin"));
        }
    }

    public boolean isBedwarsStyle() {
        return bedwarsStyle;
    }

    public RefillTask getRefill() {
        return refill;
    }

    public void reset() {
        this.gameState = GameState.LOBBY;
        this.kits.resetTemporalBoughtKits();
        this.playersSelectedKit.clear();
        this.gameStart.setHasStarted(false);
        this.gameStart.setSeconds(this.getGameInstance().getConfig(ConfigType.CONFIG).getInt("options.gameStart.timerStart"));
        this.gameStart.setRunFromCommand(false);
        this.teams.reset();
        this.stats.clear();
        if (this.timer.getBossBars() != null && !this.timer.getBossBars().isEmpty())
            this.timer.reset();
        this.timer.update(this.getGameInstance());
        this.bedwarsStyle = false;
        this.goldenGoal = false;
    }
}