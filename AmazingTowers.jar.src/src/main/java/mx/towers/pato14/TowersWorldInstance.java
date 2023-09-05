package mx.towers.pato14;

import mx.towers.pato14.game.items.HotbarItems;
import mx.towers.pato14.game.scoreboard.ScoreHelper;
import mx.towers.pato14.game.scoreboard.ScoreUpdate;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class TowersWorldInstance {
    protected final AmazingTowers plugin = AmazingTowers.getPlugin();
    protected final Map<ConfigType, Config> configs;
    protected ScoreUpdate scoreUpdate;
    protected int numPlayers;
    protected final String name;
    protected HotbarItems hotbarItems;
    public TowersWorldInstance(String name, Class<? extends TowersWorldInstance> aClass) {
        this.name = name;
        this.configs = new HashMap<>();
        this.numPlayers = 0;
        registerConfigs(name, aClass);
        SetupVault.setupVault();
        this.scoreUpdate = new ScoreUpdate(this);
        if (plugin.getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        }
    }
    private void registerConfigs(String worldName, Class<? extends TowersWorldInstance> aClass) {
        for (ConfigType config : ConfigType.getValues(aClass))
            this.configs.put(config, new Config(plugin,
                    Utils.macroCaseToCamelCase((aClass.equals(LobbyInstance.class) ? "LOBBY_" : "") + config.name()) + ".yml", true, worldName));
    }
    public World getWorld() {
        return Bukkit.getWorld(name);
    }
    public Config getConfig(ConfigType config) {
        return this.configs.get(config);
    }
    public ScoreUpdate getScoreUpdates() {
        return this.scoreUpdate;
    }
    public AmazingTowers getPlugin() {
        return plugin;
    }
    public void addPlayer() {
        ++numPlayers;
    }
    public void removePlayer() {
        --numPlayers;
    }
    public int getNumPlayers() {
        return numPlayers;
    }
    public String getName() {
        return name;
    }
    public void broadcastMessage(String msg, boolean colorMessage) {
        if (colorMessage)
            msg = AmazingTowers.getColor(msg);
        for (Player player : getWorld().getPlayers()) {
            player.sendMessage(msg);
        }
    }
    public HotbarItems getHotbarItems() {
        return hotbarItems;
    }

    public void playerJoinGame(Player player) {
        this.addPlayer();
        this.getScoreUpdates().createScoreboard(player);
        this.getScoreUpdates().updateScoreboardAll();
        if (AmazingTowers.isConnectedToDatabase()) {
            AmazingTowers.getPlugin().connexion.createAccount(player.getName());
        }
    }

    public void playerLeaveGame(Player player) {
        if (ScoreHelper.hasScore(player))
            ScoreHelper.removeScore(player);
        this.removePlayer();
        this.getScoreUpdates().updateScoreboardAll();
    }

    public void reset() {
        this.configs.clear();
        registerConfigs(name, this.getClass());
    }
}