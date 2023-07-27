package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.events.protect.CofresillosListener;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.locations.Locations;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Start {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();
    private final Game game;
    private int seconds;
    private boolean stop = false;
    private boolean hasStarted = false;
    private boolean runFromCommand = false;
    private final World world;
    private List<Map<String, String>> generators;

    public Start(Game game) {
        this.game = game;
        this.world = game.getGameInstance().getWorld();
        this.seconds = this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.gameStart.timer-start");
    }

    public void gameStart() {
        hasStarted = true;
        (new BukkitRunnable() {
            public void run() {
                if (Start.this.seconds == 0) {
                    game.setGameState(GameState.GAME);
                    cancel();
                    Start.this.teleportPlayers();
                    Start.this.startGenerators();
                    CofresillosListener.getChests(Start.this.game.getGameInstance());
                    Start.this.game.getGameInstance().getUpdates().getRefill().iniciarRefill();
                    Start.this.game.getGameInstance().getUpdates().updateScoreboardGame(game);
                    Start.this.game.getDetectionMove().MoveDetect();
                    return;
                }
                if (!runFromCommand && game.getGameInstance().getNumPlayers() < Start.this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.gameStart.min-players")) {
                    cancel();
                    game.setGameState(GameState.LOBBY);
                    for (Player p : world.getPlayers()) {
                        p.sendMessage(AmazingTowers.getColor(Start.this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.gameStart.necessaryPlayers")));
                    }
                    Start.this.game.getGameInstance().getUpdates().updateScoreboardGame(game);
                    return;
                }
                if (Start.this.seconds % 10 == 0 || Start.this.seconds <= 5) {
                    for (Player p : world.getPlayers()) {
                        p.sendMessage(AmazingTowers.getColor(Start.this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.gameStart.start")
                                .replace("{count}", String.valueOf(Start.this.seconds))
                                .replace("{seconds}", Start.this.getSeconds())));
                    }
                }
                if (Start.this.seconds <= 5 &&
                        Start.this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getBoolean("messages.gameStart.title.enabled")) {
                    String title = AmazingTowers.getColor(Start.this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.gameStart.title.title-5seconds").replace("{count}", String.valueOf(Start.this.seconds)));
                    String subtitle = AmazingTowers.getColor(Start.this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.gameStart.title.subtitle-5seconds").replace("{count}", String.valueOf(Start.this.seconds)));
                    for (Player player : world.getPlayers()) {
                        Start.this.plugin.getNms().sendTitle(player, title, subtitle, 0, 50, 20);
                    }
                }
                Start.this.game.getGameInstance().getUpdates().updateScoreboardGame(game);
                if (!Start.this.stop) Start.this.seconds = Start.this.seconds - 1;
            }
        }).runTaskTimer(this.plugin, 0L, 20L);
    }

    private void teleportPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Dar.darItemsJoinTeam(player);
        }
    }

    private String getSeconds() {
        return (this.seconds == 1) ? this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.gameStart.second") : this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.gameStart.seconds");
    }
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    public void stopCount() {
        this.stop = true;
    }
    public void continueCount() {
        this.stop = false;
    }
    public int getIntSeconds() {
        return this.seconds;
    }
    public boolean hasStarted() {
        return hasStarted;
    }
    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }
    public void setRunFromCommand(boolean runFromCommand) {
        this.runFromCommand = runFromCommand;
    }

    private void startGenerators() {
        Config locations = Start.this.game.getGameInstance().getConfig(ConfigType.LOCATIONS);
        String path = Location.GENERATOR.getPath();
        this.generators = locations.getList(path) == null ? new ArrayList<>() : locations.getMapList(path).stream()
                .map(o -> (Map<String, String>) o).collect(Collectors.toList());
        (new BukkitRunnable() {
            public void run() {
                if (Start.this.game.getGameState().equals(GameState.FINISH)) {
                    cancel();
                    return;
                }
                try {
                    for (Map<String, String> item : generators) {
                        world.dropItemNaturally(Locations.getLocationFromString(item.get("coords")),
                                plugin.getNms().deserializeItemStack(item.get("item")));
                    }
                } catch (MojangsonParseException exception) {
                    Bukkit.getConsoleSender().sendMessage("Error while parsing the generator items!");
                    cancel();
                }
            }
        }).runTaskTimer(this.plugin, 0L, (this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.generator_timePerSecond") * 20L));
    }
}


