package mx.towers.pato14;

import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.scoreboard.ScoreUpdate;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.locations.Detectoreishon;
import mx.towers.pato14.utils.nms.NMS;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.rewards.VaultT;
import mx.towers.pato14.utils.wand.Wand;
import mx.towers.pato14.utils.world.WorldLoad;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
public class GameInstance {
    private static AmazingTowers plugin;
    private final String name;
    private Game game;
    private final Map<String, Config> configs;
    private ScoreUpdate scoreUpdate;
    private VaultT vault;

    public GameInstance(AmazingTowers towers, String name) {
        plugin = towers;
        this.name = name;
        this.configs = new HashMap<>();
        registerConfigs();
        createFolderBackup();
        Detectoreishon.detectoreishonLocations();

        if (Detectoreishon.getLocationsObligatory()) {
            if (getConfig(ConfigType.CONFIG).getBoolean("Options.bungeecord-support.enabled")) {
                plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
            }
            loadWorld(this.name);
            SetupVault.setupVault();
            this.vault = new VaultT(plugin);
            this.game = new Game(plugin);
            this.scoreUpdate = new ScoreUpdate(plugin);
        } else {
            Bukkit.getConsoleSender().sendMessage("Not all the locations have been set in "
                    + name + ". Please set them first.");
        }
    }

    private void registerConfigs() {
        for (ConfigType config : ConfigType.values())
            this.configs.put(config.toString().toLowerCase(), new Config(plugin,
                    config.toString().toLowerCase() + ".yml", true));
    }

    public void loadWorld(String worldName) {
        WorldLoad towers = new WorldLoad(worldName, plugin.getDataFolder().getAbsolutePath() + "/backup/" + worldName, Bukkit.getWorldContainer().getAbsolutePath() + "/" + worldName);
        if (towers.getFileSource().exists()) {
            towers.loadWorld();
        } else {
            Bukkit.getConsoleSender().sendMessage("§c[AmazingTowers] There is no backup for " + name + "!");
        }
    }

    public void createFolderBackup() {
        File folder = new File(plugin.getDataFolder(), "backup");
        if (!folder.exists() &&
                folder.mkdirs()) {
            System.out.println("[AmazingTowers] The backup folder created successfully");
        }
    }

    public VaultT getVault() {
        return this.vault;
    }

    public Game getGame() {
        return this.game;
    }
    public Config getLocations() {
        return this.locations;
    }

    public Config getScoreboard() {
        return this.scoreboard;
    }

    public Config getMessages() {
        return this.messages;
    }

    public Config getBook() {
        return this.book;
    }

    public Config getConfig(ConfigType config) {
        return this.configs.get(config.toString().toLowerCase());
    }

    public ScoreUpdate getUpdates() {
        return this.scoreUpdate;
    }
}