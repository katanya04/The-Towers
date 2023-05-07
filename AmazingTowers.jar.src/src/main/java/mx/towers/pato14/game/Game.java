package mx.towers.pato14.game;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.events.EventsManager;
import mx.towers.pato14.game.tasks.Finish;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.team.Item;
import mx.towers.pato14.game.team.TeamGame;
import mx.towers.pato14.game.utils.Book;
import mx.towers.pato14.game.utils.Move;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private final AmazingTowers plugin;
    private final Item item;
    private final TeamGame teams;
    private final Start gameStart;
    private final Finish finish;
    private final StatisticsPlayer stats;
    private final Move detectionMove;
    private Book bookItem;
    private final GameInstance gameInstance;

    public Game(GameInstance game) {
        this.gameInstance = game;
        this.plugin = game.getPlugin();
        GameState.setState(GameState.LOBBY);
        this.item = new Item(this);
        (new EventsManager(getPlugin())).registerEvents();
        getPlugin().getServer().getPluginManager().registerEvents(getItem(), getPlugin());
        this.teams = new TeamGame(this);
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

    public TeamGame getTeams() {
        return this.teams;
    }

    public Start getStart() {
        return this.gameStart;
    }

    public Finish getFinish() {
        return this.finish;
    }

    public Item getItem() {
        return this.item;
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
        return Bukkit.getServer().getWorld(this.gameInstance.getName()).getPlayers();
    }
}


