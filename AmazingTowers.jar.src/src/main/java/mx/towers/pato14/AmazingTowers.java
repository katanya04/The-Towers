package mx.towers.pato14;

import java.io.File;
import java.util.*;

import mx.towers.pato14.commands.TowerCommand;
import mx.towers.pato14.game.events.EventsManager;
import mx.towers.pato14.utils.enums.PermissionLevel;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.cofresillos.SelectCofresillos;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.files.Logger;
import mx.towers.pato14.utils.items.ActionItems;
import mx.towers.pato14.utils.mysql.Connexion;
import mx.towers.pato14.utils.placeholders.Expansion;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.wand.WandCoords;
import mx.towers.pato14.utils.wand.WandListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AmazingTowers extends JavaPlugin {
    private static AmazingTowers plugin;
    private static LobbyInstance lobby;
    private static GameInstance[] games;
    private static HashMap<Player, WandCoords> wands;
    private static Config globalConfig;
    private static Config kitsDefine;
    public static Connexion connexion;
    public static Logger logger;

    @Override
    public void onEnable() {
        plugin = this;

        globalConfig = new Config("globalConfig.yml", true);
        kitsDefine = new Config("kitsDefine.yml", true);
        games = new GameInstance[globalConfig.getInt("options.instances.amount")];

        if (getServer().getPluginManager().getPlugin("NametagEdit") == null) {
            Utils.sendConsoleMessage("§cNot detected the 'NameTagEdit' plugin, disabling AmazingTowers", MessageType.ERROR);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        createBackupsFolder();

        getCommand("towers").setExecutor(new TowerCommand());

        logger = new Logger(globalConfig.getBoolean("options.logger.activated"),
                globalConfig.getBoolean("options.logger.logSQLCalls"),
                Logger.SQLCallType.getOrDefault(globalConfig.getString("options.logger.SQLCallType"), Logger.SQLCallType.WRITE),
                globalConfig.getBoolean("options.logger.logTowersCommand"),
                PermissionLevel.getOrDefault(globalConfig.getString("options.logger.permLevelToLog"), PermissionLevel.ADMIN),
                globalConfig.getInt("options.logger.maxTimeHoursPerFile"));

        if (getGlobalConfig().getBoolean("options.database.active")) {
            connexion = new Connexion(getGlobalConfig().getConfigurationSection("options.database"));
            if (connexion.initialize())
                Utils.sendConsoleMessage("§aSuccessfully connected to the database", MessageType.INFO);
            else
                Utils.sendConsoleMessage("§cCouldn't connect to the database", MessageType.ERROR);
        }

        for (int i = 0; i < games.length; i++)
            games[i] = new GameInstance("TheTowers" + (i + 1));

        if (globalConfig.getBoolean("options.lobby.activated"))
            lobby = new LobbyInstance(globalConfig.getString("options.lobby.worldName"));
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Expansion().register();
        }

        ActionItems.registerItems();
        ActionItems.setHotbarItemsInInstances();

        (new EventsManager(getPlugin())).registerEvents();
        wands = new HashMap<>();
        getServer().getPluginManager().registerEvents(new WandListener(), this);
        getServer().getPluginManager().registerEvents(new SelectCofresillos(), this);
        boolean worldUnset = false;
        for (GameInstance gameInstance : games) {
            if (Utils.checkWorldFolder(gameInstance.getInternalName())) {
                Utils.sendConsoleMessage("§f§l" + gameInstance.getInternalName() + "§f locations needed to be set: " +
                        (gameInstance.hasUnsetRegions() ? gameInstance.getUnsetRegionsString().toUpperCase() : "[NONE]"), MessageType.INFO);
            } else {
                worldUnset = true;
                Utils.sendConsoleMessage("§f§l" + gameInstance.getInternalName() + "§f world doesn't exist yet. To create it, run /tt createWorld " + gameInstance.getInternalName(), MessageType.INFO);
            }
        }
        if (worldUnset)
            Utils.sendConsoleMessage("To create all missing worlds, run /tt createWorld all", MessageType.INFO);

        Utils.sendConsoleMessage("Enabled successfully", MessageType.INFO);
        if (!Bukkit.getVersion().contains("1.8."))
            Utils.sendConsoleMessage("Only 1.8 Minecraft versions are fully supported, there may be errors", MessageType.WARNING);
    }

    @Override
    public void onDisable() {
        logger.closeStream();
        connexion.close();
        Arrays.stream(games).filter(o -> o.getGame() == null).map(TowersWorldInstance::getWorld).filter(Objects::nonNull).forEach(World::save);
    }
    public static WandCoords getWandCoords(Player player) {
        return wands.get(player);
    }

    public static AmazingTowers getPlugin() {
        return plugin;
    }

    public static GameInstance getGameInstance(Entity e) {
        return getGameInstance(e.getWorld().getName());
    }

    public static GameInstance getGameInstance(Block e) {
        return getGameInstance(e.getWorld().getName());
    }

    public static GameInstance getGameInstance(World w) {
        return getGameInstance(w.getName());
    }

    public static GameInstance getGameInstance(String worldName) {
        for (GameInstance gameInstance : games) {
            if (gameInstance.getInternalName().equals(worldName))
                return gameInstance;
        }
        return null;
    }

    public static Config getGlobalConfig() {
        return globalConfig;
    }
    public static Config getKitsDefine() {
        return kitsDefine;
    }

    public static void createBackupsFolder() {  //Crear la carpeta "backup"
        File folder = new File(plugin.getDataFolder(), "backup");
        if (!folder.exists() && folder.mkdirs())
            Utils.sendConsoleMessage("The backup folder was created successfully", MessageType.INFO);
    }

    public static GameInstance[] getGameInstances() {
        return games;
    }

    public static boolean capitalismExists() {
        return SetupVault.getVaultEconomy() != null;
    }

    public static GameInstance checkForInstanceToTp(Player player) {
        for (GameInstance gameInstance : games) {
            if (!gameInstance.canJoin(player) || gameInstance.getGame() == null
                || gameInstance.getWorld() == null || gameInstance.getGame().getGameState() == GameState.FINISH)
                continue;
            return gameInstance;
        }
        return null;
    }

    public static LobbyInstance getLobby() {
        return lobby;
    }

    public static TowersWorldInstance getInstance(Entity entity) {
        return getInstance(entity.getWorld());
    }

    public static TowersWorldInstance getInstance(World world) {
        return world.equals(lobby.getWorld()) ? lobby : getGameInstance(world);
    }

    public static GameInstance getInstanceMostPlayers() {
        return games[0];
    }

    public static Collection<Player> getAllOnlinePlayers() {
        List<Player> playersInGameInstances = new ArrayList<>();
        Arrays.stream(games).filter(o -> o.getWorld() != null).forEach(o -> playersInGameInstances.addAll(o.getWorld().getPlayers()));
        playersInGameInstances.addAll(lobby.getWorld().getPlayers());
        return playersInGameInstances;
    }

    public static boolean isConnectedToDatabase() {
        return connexion.isConnected();
    }
    public static void addPlayerWand(Player player) {
        wands.put(player, new WandCoords());
    }
    public static void removePlayerWand(Player player) {
        wands.remove(player);
    }
}