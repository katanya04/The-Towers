package mx.towers.pato14.game;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.events.EventsManager;
import mx.towers.pato14.game.tasks.Finish;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.team.LobbyItems;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.utils.Book;
import mx.towers.pato14.game.utils.Move;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Game {
    private final AmazingTowers plugin;
    private final LobbyItems lobbyItems;
    private final GameTeams teams;
    private final Start gameStart;
    private final Finish finish;
    private final StatisticsPlayer stats;
    private final Move detectionMove;
    private Book bookItem;
    private final GameInstance gameInstance;
    private GameState gameState;
    private final int numberOfTeams;

    public Game(GameInstance game) {
        this.gameInstance = game;
        this.plugin = game.getPlugin();
        this.gameState = GameState.LOBBY;
        this.lobbyItems = new LobbyItems(this);
        this.numberOfTeams = game.getConfig(ConfigType.CONFIG).getInt("General.teams");;
        (new EventsManager(getPlugin())).registerEvents();
        getPlugin().getServer().getPluginManager().registerEvents(getItem(), getPlugin());
        this.teams = new GameTeams(this);
        this.gameStart = new Start(this);
        this.finish = new Finish(this);
        this.stats = new StatisticsPlayer();
        this.detectionMove = new Move(this);
        if (game.getConfig(ConfigType.BOOK).getBoolean("book.enabled")) {
            this.bookItem = new Book(this);
        }
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

    public LobbyItems getItem() {
        return this.lobbyItems;
    }

    public Move getDetectionMove() {
        return this.detectionMove;
    }

    private AmazingTowers getPlugin() {
        return this.plugin;
    }
    public GameInstance getGameInstance() {
        return gameInstance;
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
    public int getNumberOfTeams() {
        return numberOfTeams;
    }
}


