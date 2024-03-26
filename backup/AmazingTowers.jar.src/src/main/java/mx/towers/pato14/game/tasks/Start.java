package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Start {
    private int countDown;
    private boolean stop = false;
    private boolean hasStarted = false;
    private boolean runFromCommand = false;
    private final String worldName;

    public Start(GameInstance gameInstance) {
        this.worldName = gameInstance.getWorld().getName();
        this.countDown = gameInstance.getConfig(ConfigType.CONFIG).getInt("options.gameStart.timerStart");
    }

    public void gameStart() {
        hasStarted = true;
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        Game game = gameInstance.getGame();
        (new BukkitRunnable() {
            public void run() {
                if (Start.this.countDown <= 0) {
                    game.setGameState(GameState.GAME);
                    cancel();
                    game.setBedwarsStyle(gameInstance.getRules().get(Rule.BEDWARS_STYLE));
                    Start.this.teleportPlayers();
                    game.getGenerators().startGenerators();
                    game.getRefill().startRefillTask();
                    gameInstance.getScoreUpdates().updateScoreboardAll(false, game.getPlayers());
                    game.getDetectionMove().MoveDetect();
                    if (Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("timer.activated")))
                        game.getTimer().timerStart();
                    return;
                }
                if (!runFromCommand && gameInstance.getNumPlayers() < gameInstance.getConfig(ConfigType.CONFIG).getInt("options.gameStart.minPlayers")) {
                    game.setGameState(GameState.LOBBY);
                    for (Player p : gameInstance.getWorld().getPlayers()) {
                        p.sendMessage(Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("gameStart.notEnoughPlayers")));
                    }
                    setCountDown(20);
                    gameInstance.getScoreUpdates().updateScoreboardAll(false, game.getPlayers());
                    hasStarted = false;
                    cancel();
                    return;
                }
                if (Start.this.countDown % 10 == 0 || Start.this.countDown <= 5) {
                    for (Player p : gameInstance.getWorld().getPlayers()) {
                        p.sendMessage(Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("gameStart.start")
                                .replace("{count}", String.valueOf(Start.this.countDown))
                                .replace("{seconds}", Start.this.getCountDown())));
                    }
                }
                if (Start.this.countDown <= 5 &&
                        gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("gameStart.title.enabled")) {
                    String title = Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("gameStart.title.titleFiveOrLessSec").replace("{count}", String.valueOf(Start.this.countDown)));
                    String subtitle = Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("gameStart.title.subtitleFiveOrLessSec").replace("{count}", String.valueOf(Start.this.countDown)));
                    for (Player player : gameInstance.getWorld().getPlayers()) {
                        ReflectionMethods.sendTitle(player, title, subtitle, 0, 50, 20);
                    }
                }
                gameInstance.getScoreUpdates().updateScoreboardAll(false, game.getPlayers());
                if (!Start.this.stop) Start.this.countDown = Start.this.countDown - 1;
            }
        }).runTaskTimer(AmazingTowers.getPlugin(), 0L, 20L);
    }

    private void teleportPlayers() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        for (Player player : gameInstance.getGame().getPlayers()) {
            gameInstance.getGame().spawn(player);
        }
    }

    private String getCountDown() {
        return (this.countDown == 1) ? AmazingTowers.getGameInstance(worldName).getConfig(ConfigType.MESSAGES).getString("gameStart.second") : AmazingTowers.getGameInstance(worldName).getConfig(ConfigType.MESSAGES).getString("gameStart.seconds");
    }
    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }
    public void stopCount() {
        this.stop = true;
    }
    public void continueCount() {
        this.stop = false;
    }
    public int getIntSeconds() {
        return this.countDown;
    }
    public void continueFromCommand() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        Game game = gameInstance.getGame();
        if (!this.hasStarted) {
            game.setGameState(GameState.PREGAME);
            this.runFromCommand = true;
            this.hasStarted = true;
            this.gameStart();
        }
        this.continueCount();
    }
    public void startFromCommand() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        Game game = gameInstance.getGame();
        if (!this.hasStarted) {
            game.setGameState(GameState.PREGAME);
            this.runFromCommand = true;
            this.hasStarted = true;
            this.gameStart();
        }
        this.continueCount();
        this.setCountDown(0);
    }
    public void reset() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        this.runFromCommand = false;
        this.hasStarted = false;
        this.setCountDown(gameInstance.getConfig(ConfigType.CONFIG).getInt("options.gameStart.timerStart"));
    }
}