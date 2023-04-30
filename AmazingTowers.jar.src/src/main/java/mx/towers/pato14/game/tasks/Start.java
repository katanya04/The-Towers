package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.events.protect.CofresillosListener;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.plugin.PluginA;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Start {
    private AmazingTowers at = AmazingTowers.getPlugin();
    private int seconds = this.at.getConfig().getInt("Options.gameStart.timer-start");
    private boolean stop = false;
    private boolean hasStarted = false;
    private boolean runFromCommand = false;

    public void gameStart() {
        hasStarted = true;
        (new BukkitRunnable() {
            public void run() {
                if (Start.this.seconds == 0) {
                    GameState.setState(GameState.GAME);
                    cancel();
                    Start.this.teleportPlayers();
                    Start.this.startGenerators();
                    CofresillosListener.getChests();
                    Start.this.at.getUpdates().getRefill().iniciarRefill();
                    Start.this.at.getUpdates().updateScoreboardAll();
                    Start.this.at.getGame().getDetectionMove().MoveDetect();
                    return;
                }
                if (!runFromCommand && Bukkit.getOnlinePlayers().size() < Start.this.at.getConfig().getInt("Options.gameStart.min-players")) {
                    cancel();
                    GameState.setState(GameState.LOBBY);
                    Bukkit.broadcastMessage(Start.this.at.getColor(Start.this.at.getMessages().getString("messages.gameStart.necessaryPlayers")));
                    Start.this.at.getUpdates().updateScoreboardAll();
                    return;
                }
                if (Start.this.seconds % 10 == 0 || Start.this.seconds <= 5) {
                    Bukkit.broadcastMessage(Start.this.at.getColor(Start.this.at.getMessages().getString("messages.gameStart.start")
                            .replace("{count}", String.valueOf(Start.this.seconds))
                            .replace("{seconds}", Start.this.getSeconds())));
                }
                if (Start.this.seconds <= 5 &&
                        Start.this.at.getMessages().getBoolean("messages.gameStart.title.enabled")) {
                    String title = Start.this.at.getColor(Start.this.at.getMessages().getString("messages.gameStart.title.title-5seconds").replace("{count}", String.valueOf(Start.this.seconds)));
                    String subtitle = Start.this.at.getColor(Start.this.at.getMessages().getString("messages.gameStart.title.subtitle-5seconds").replace("{count}", String.valueOf(Start.this.seconds)));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Start.this.at.getNms().sendTitle(player, title, subtitle, 0, 50, 20);
                    }
                }
                Start.this.at.getUpdates().updateScoreboardAll();
                if (!Start.this.stop) Start.this.seconds = Start.this.seconds - 1;
            }
        }).runTaskTimer((Plugin) this.at, 0L, 20L);
    }

    private void teleportPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Dar.darItemsJoinTeam(player);
        }
    }

    private String getSeconds() {
        return (this.seconds == 1) ? this.at.getMessages().getString("messages.gameStart.second") : this.at.getMessages().getString("messages.gameStart.seconds");
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
        (new BukkitRunnable() {
            public void run() {
                if (GameState.isState(GameState.FINISH)) {
                    cancel();
                    return;
                }
                Bukkit.getWorld("TheTowers").dropItemNaturally(Locations.getLocationFromString(Start.this.at.getLocations().getString(Locationshion.IRON_GENERATOR.getLocationString())), new ItemStack(Material.IRON_INGOT, 1));
                Bukkit.getWorld("TheTowers").dropItemNaturally(Locations.getLocationFromString(Start.this.at.getLocations().getString(Locationshion.XPBOTTLES_GENERATOR.getLocationString())), new ItemStack(Material.EXP_BOTTLE, 1));
                if (Start.this.at.getConfig().getBoolean("Options.generator_lapislazuli")) {
                    Bukkit.getWorld("TheTowers").dropItemNaturally(Locations.getLocationFromString(Start.this.at.getLocations().getString(Locationshion.LAPISLAZULI_GENERATOR.getLocationString())), new ItemStack(Material.INK_SACK, 1, (short) 4));
                }
            }
        }).runTaskTimer((Plugin) this.at, 0L, (this.at.getConfig().getInt("Options.generator_timePerSecond") * 20));
    }
}


