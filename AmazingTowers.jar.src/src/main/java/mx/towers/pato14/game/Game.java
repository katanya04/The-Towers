package mx.towers.pato14.game;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.game.kits.Kits;
import mx.towers.pato14.game.tasks.Finish;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.items.LobbyItems;
import mx.towers.pato14.game.tasks.Timer;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.utils.Book;
import mx.towers.pato14.game.tasks.Move;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class Game {
    private final AmazingTowers plugin;
    private final LobbyItems lobbyItems;
    private final GameTeams teams;
    private final Start gameStart;
    private final Finish finish;
    private final Timer timer;
    private final StatisticsPlayer stats;
    private final Move detectionMove;
    private Book bookItem;
    private final GameInstance gameInstance;
    private GameState gameState;
    private final Kits kits;
    private final HashMap<HumanEntity, Kit> playersSelectedKit;
    private boolean goldenGoal;
    private boolean bedwarsStyle;

    public Game(GameInstance game) {
        this.gameInstance = game;
        this.plugin = game.getPlugin();
        this.gameState = GameState.LOBBY;
        this.kits = new Kits(game.getConfig(ConfigType.KITS), plugin.capitalismExists());
        this.playersSelectedKit = new HashMap<>();
        this.teams = new GameTeams(this);
        this.lobbyItems = new LobbyItems(this);
        getPlugin().getServer().getPluginManager().registerEvents(getLobbyItems(), getPlugin());
        this.gameStart = new Start(this);
        this.timer = new Timer(this);
        this.finish = new Finish(this);
        this.stats = new StatisticsPlayer();
        this.detectionMove = new Move(this);
        this.bedwarsStyle = false;
        this.goldenGoal = false;
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

    public LobbyItems getLobbyItems() {
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
}