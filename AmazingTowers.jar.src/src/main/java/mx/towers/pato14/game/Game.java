package mx.towers.pato14.game;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.events.EventsManager;
import mx.towers.pato14.game.tasks.Finish;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.team.Item;
import mx.towers.pato14.game.team.TeamGame;
import mx.towers.pato14.game.utils.Book;
import mx.towers.pato14.game.utils.Move;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Game {
    private AmazingTowers plugin;
    private Item item;
    private TeamGame teams;
    private Start gameStart;
    private Finish finish;
    private StatisticsPlayer stats;
    private Move detectionMove;
    private Book bookItem;

    public Game(AmazingTowers plugin) {
        this.plugin = plugin;
        GameState.setState(GameState.LOBBY);
        this.item = new Item(getPlugin());
        (new EventsManager(getPlugin())).registerEvents();
        getPlugin().getServer().getPluginManager().registerEvents((Listener) getItem(), (Plugin) getPlugin());
        this.teams = new TeamGame(getPlugin());
        this.gameStart = new Start();
        this.finish = new Finish();
        this.stats = new StatisticsPlayer();
        this.detectionMove = new Move(getPlugin());
        if (getPlugin().getBook().getBoolean("book.enabled")) {
            this.bookItem = new Book(getPlugin());
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
}


