package mx.towers.pato14;

import mx.towers.pato14.game.items.HotbarItems;
import mx.towers.pato14.game.scoreboard.ScoreHelper;
import mx.towers.pato14.game.scoreboard.ScoreUpdate;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.mysql.Connexion;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class TowersWorldInstance implements Comparable<TowersWorldInstance> {
    protected final AmazingTowers plugin = AmazingTowers.getPlugin();
    protected final Map<ConfigType, Config> configs;
    protected ScoreUpdate scoreUpdate;
    protected final String internalName;
    protected HotbarItems hotbarItems;
    public TowersWorldInstance(String name, Class<? extends TowersWorldInstance> aClass) {
        this.internalName = name;
        this.configs = new HashMap<>();
        registerConfigs(name, aClass);
        SetupVault.setupVault();
        this.scoreUpdate = new ScoreUpdate(this);
        if (AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        }
    }
    private void registerConfigs(String worldName, Class<? extends TowersWorldInstance> aClass) {
        for (ConfigType config : ConfigType.getValues(aClass))
            this.configs.put(config, new Config(Utils.macroCaseToCamelCase((aClass.equals(LobbyInstance.class) ?
                    "LOBBY_" : "") + config.name()) + ".yml", true, worldName));
    }
    public void reloadAllConfigs() {
        configs.forEach((o,v) -> v.reloadConfig());
    }
    public World getWorld() {
        return Bukkit.getWorld(internalName);
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
    public int getNumPlayers() {
        return getWorld().getPlayers().size();
    }
    public String getInternalName() {
        return internalName;
    }
    public void broadcastMessage(String msg, boolean colorMessage) {
        if (colorMessage)
            msg = Utils.getColor(msg);
        for (Player player : getWorld().getPlayers()) {
            player.sendMessage(msg);
        }
    }
    public HotbarItems getHotbarItems() {
        return hotbarItems;
    }

    public void joinInstance(Player player) {
        this.getScoreUpdates().createScoreboard(player);
        this.getScoreUpdates().updateScoreboardAll();
        if (AmazingTowers.isConnectedToDatabase()) {
            AmazingTowers.connexion.createAccount(player.getName(), Connexion.ALL_TABLES);
        }
    }

    public void leaveInstance(Player player) {
        ScoreHelper.removeScore(player);
        this.getScoreUpdates().updateScoreboardAll();
    }

    public void reset() {
        registerConfigs(internalName, this.getClass());
    }

    @Override
    public int compareTo(@NotNull TowersWorldInstance o) {
        return Integer.compare(this.getNumPlayers(), o.getNumPlayers());
    }
}