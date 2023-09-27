package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.events.protect.CofresillosListener;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.exceptions.ParseItemException;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Start {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();
    private int seconds;
    private boolean stop = false;
    private boolean hasStarted = false;
    private boolean runFromCommand = false;
    private final String worldName;
    private List<Map<String, String>> generators;

    public Start(GameInstance gameInstance) {
        this.worldName = gameInstance.getWorld().getName();
        this.seconds = gameInstance.getConfig(ConfigType.CONFIG).getInt("options.gameStart.timerStart");
    }

    public void gameStart() {
        hasStarted = true;
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        Game game = gameInstance.getGame();
        (new BukkitRunnable() {
            public void run() {
                if (Start.this.seconds <= 0) {
                    game.setGameState(GameState.GAME);
                    cancel();
                    Start.this.teleportPlayers();
                    Start.this.startGenerators();
                    CofresillosListener.getChests(gameInstance);
                    gameInstance.getGame().getRefill().startRefillTask();
                    gameInstance.getScoreUpdates().updateScoreboardAll();
                    game.getDetectionMove().MoveDetect();
                    game.setBedwarsStyle(gameInstance.getRules().get(Rule.BEDWARS_STYLE));
                    if (Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("timer.activated")))
                        game.getTimer().timerStart();
                    return;
                }
                if (!runFromCommand && gameInstance.getNumPlayers() < gameInstance.getConfig(ConfigType.CONFIG).getInt("options.gameStart.minPlayers")) {
                    game.setGameState(GameState.LOBBY);
                    for (Player p : gameInstance.getWorld().getPlayers()) {
                        p.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("gameStart.notEnoughPlayers")));
                    }
                    setSeconds(20);
                    gameInstance.getScoreUpdates().updateScoreboardAll();
                    hasStarted = false;
                    cancel();
                    return;
                }
                if (Start.this.seconds % 10 == 0 || Start.this.seconds <= 5) {
                    for (Player p : gameInstance.getWorld().getPlayers()) {
                        p.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("gameStart.start")
                                .replace("{count}", String.valueOf(Start.this.seconds))
                                .replace("{seconds}", Start.this.getSeconds())));
                    }
                }
                if (Start.this.seconds <= 5 &&
                        gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("gameStart.title.enabled")) {
                    String title = AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("gameStart.title.titleFiveOrLessSec").replace("{count}", String.valueOf(Start.this.seconds)));
                    String subtitle = AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("gameStart.title.subtitleFiveOrLessSec").replace("{count}", String.valueOf(Start.this.seconds)));
                    for (Player player : gameInstance.getWorld().getPlayers()) {
                        ReflectionMethods.sendTitle(player, title, subtitle, 0, 50, 20);
                    }
                }
                gameInstance.getScoreUpdates().updateScoreboardAll();
                if (!Start.this.stop) Start.this.seconds = Start.this.seconds - 1;
            }
        }).runTaskTimer(this.plugin, 0L, 20L);
    }

    private void teleportPlayers() {
        for (Player player : AmazingTowers.getGameInstance(worldName).getGame().getPlayers()) {
            Dar.joinTeam(player);
        }
    }

    private String getSeconds() {
        return (this.seconds == 1) ? AmazingTowers.getGameInstance(worldName).getConfig(ConfigType.MESSAGES).getString("gameStart.second") : AmazingTowers.getGameInstance(worldName).getConfig(ConfigType.MESSAGES).getString("gameStart.seconds");
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

    @SuppressWarnings("unchecked")
    private void startGenerators() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
        String path = Location.GENERATOR.getPath();
        this.generators = locations.getList(path) == null ? new ArrayList<>() : locations.getMapList(path).stream()
                .map(o -> (Map<String, String>) o).collect(Collectors.toList());
        (new BukkitRunnable() {
            public void run() {
                if (gameInstance.getGame().getGameState().equals(GameState.FINISH)) {
                    cancel();
                    return;
                }
                try {
                    for (Map<String, String> item : generators) {
                        gameInstance.getWorld().dropItemNaturally(Locations.getLocationFromString(item.get("coords")),
                                ReflectionMethods.deserializeItemStack(item.get("item")));
                    }
                } catch (ParseItemException exception) {
                    plugin.sendConsoleMessage("§cError while parsing the generator items!", MessageType.ERROR);
                    cancel();
                }
            }
        }).runTaskTimer(this.plugin, 0L, (gameInstance.getConfig(ConfigType.CONFIG).getInt("options.generatorSpeedInSeconds") * 20L));
    }
}


