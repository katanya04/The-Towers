package mx.towers.pato14;

import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.scoreboard.ScoreUpdate;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.locations.Detectoreishon;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.rewards.VaultT;
import mx.towers.pato14.utils.world.WorldLoad;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GameInstance {
    private final AmazingTowers plugin;
    private final String name;
    private Game game;
    private final Map<ConfigType, Config> configs;
    private ScoreUpdate scoreUpdate;
    private VaultT vault;
    private final Map<Rule, Boolean> rules;
    private int numPlayers;

    public GameInstance(AmazingTowers towers, String name) {
        this.plugin = towers;
        this.name = name;
        this.configs = new HashMap<>();
        this.rules = new HashMap<>();
        this.numPlayers = 0;
        setRules();
        registerConfigs();
        createFolderBackup();
        Detectoreishon.detectoreishonLocations();

        if (Detectoreishon.getLocationsObligatory()) {
            if (getConfig(ConfigType.CONFIG).getBoolean("Options.bungeecord-support.enabled")) {
                plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
            }
            loadWorld(this.name);
            SetupVault.setupVault();
            this.vault = new VaultT(this);
            this.game = new Game(this);
            this.scoreUpdate = new ScoreUpdate(this);
        } else {
            Bukkit.getConsoleSender().sendMessage("Not all the locations have been set in "
                    + name + ". Please set them first.");
        }
    }

    private void registerConfigs() {
        for (ConfigType config : ConfigType.values())
            this.configs.put(config, new Config(plugin,
                    config.toString().toLowerCase() + ".yml", true, name));
    }

    public void loadWorld(String worldName) {   //Borra mundo de partida anterior y lo sobreescribe con el de backup
        WorldLoad towers = new WorldLoad(worldName, plugin.getDataFolder().getAbsolutePath() + "/backup/" + worldName, Bukkit.getWorldContainer().getAbsolutePath() + "/" + worldName);
        if (towers.getFileSource().exists()) {
            towers.loadWorld();
        } else {
            Bukkit.getConsoleSender().sendMessage("§c[AmazingTowers] There is no backup for " + name + "!");
        }
    }

    public void createFolderBackup() {  //Crear la carpeta "backup"
        File folder = new File(plugin.getDataFolder(), "backup");
        if (!folder.exists() &&
                folder.mkdirs()) {
            System.out.println("[AmazingTowers] The backup folder was created successfully");
        }
    }

    private void setRules() {   //Sets rules to default values
        for (Rule rule : Rule.values())
            this.rules.put(rule, rule.getCurrentState());
    }
    public String getName() {
        return this.name;
    }
    public VaultT getVault() {
        return this.vault;
    }

    public Game getGame() {
        return this.game;
    }

    public Config getConfig(ConfigType config) {
        return this.configs.get(config);
    }

    public ScoreUpdate getUpdates() {
        return this.scoreUpdate;
    }

    public Map<Rule, Boolean> getRules() {
        return rules;
    }

    public AmazingTowers getPlugin() {
        return plugin;
    }
    public int getNumPlayers() {
        return numPlayers;
    }
    public void addPlayer() {
        numPlayers++;
    }
    public void removePlayer() {
        numPlayers--;
    }
}