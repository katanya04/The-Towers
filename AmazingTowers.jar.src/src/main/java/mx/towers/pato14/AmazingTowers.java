package mx.towers.pato14;

import java.io.File;
import java.util.*;

import mx.towers.pato14.commands.TowerCommand;
import mx.towers.pato14.game.events.EventsManager;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.cofresillos.SelectCofresillos;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.mysql.Connexion;
import mx.towers.pato14.utils.nms.*;
import mx.towers.pato14.utils.placeholders.Expansion;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.wand.Wand;
import mx.towers.pato14.utils.wand.WandListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AmazingTowers extends JavaPlugin {

    private static AmazingTowers plugin;
    private static HashMap<String, GameInstance> games;
    private NMS nms;
    private Wand wand;
    private Config globalConfig;
    public Connexion con;

    public void onLoad() {
        if (!getDescription().getAuthors().contains("Pato14")) {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void onEnable() {
        plugin = this;
        games = new HashMap<>();

        if (!setupNMS()) {
            sendConsoleMessage("§cYour server version is not compatible with this plugin!", MessageType.ERROR);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (getServer().getPluginManager().getPlugin("NametagEdit") == null) {
            sendConsoleMessage("§cNot detected the 'NameTagEdit' plugin, disabling AmazingTowers", MessageType.ERROR);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        createFolderBackup();

        getCommand("towers").setExecutor(new TowerCommand());

        this.globalConfig = new Config(this, "globalConfig.yml", true);
        int numberOfInstances = this.globalConfig.getInt("options.instances.amount");

        for (int i = 0; i < numberOfInstances; i++)
            games.put("TheTowers" + (i + 1), new GameInstance(this,"TheTowers" + (i + 1)));

        if (getGlobalConfig().getBoolean("options.mysql.active")) {
            try {
                this.con = new Connexion();
                this.con.Connect();
                this.con.CreateTable();
                sendConsoleMessage("§aSuccessfully connected to the database", MessageType.INFO);
            } catch (Exception e) {
                sendConsoleMessage("§cCouldn't connect to the database", MessageType.ERROR);
            }
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Expansion(this).register();
        }
        enabledPlugin();
    }

    public void onDisable() {
        for (GameInstance gameInstance : games.values()) {
            if (gameInstance.getGame() == null) {
                World world = gameInstance.getWorld();
                if (world != null)
                    world.save();
            }
        }
    }

    private void enabledPlugin() {
        (new EventsManager(getPlugin())).registerEvents();
        this.wand = new Wand();
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        getServer().getPluginManager().registerEvents(new SelectCofresillos(this), this);
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            sendConsoleMessage("§aServer§f: §f" + version, MessageType.INFO);
        } catch (ArrayIndexOutOfBoundsException a) {
            sendConsoleMessage("Unknown server version", MessageType.ERROR);
        }
        sendConsoleMessage("§a-----§f-§a-----§f-§a-----§f-§a-----§f-§a-----", MessageType.INFO);
        sendConsoleMessage("§aPlugin§f: §aEnabled§a successfully§f!", MessageType.INFO);
        sendConsoleMessage("§aVersion§f: " + getDescription().getVersion(), MessageType.INFO);
        sendConsoleMessage("§aAuthor§f: §f[§aPato14§f, §aMarco2124§f]", MessageType.INFO);
        sendConsoleMessage("§a-----§f-§a-----§f-§a-----§f-§a-----§f-§a-----", MessageType.INFO);
        boolean worldUnset = false;
        for (GameInstance gameInstance : games.values()) {
            if (gameInstance.hasWorldAssociated()) {
                sendConsoleMessage("§f§l" + gameInstance.getName() + "§f locations needed to be set: ", MessageType.INFO);
                sendConsoleMessage(gameInstance.getDetectoreishon().getLocationsNeededString(true), MessageType.INFO);
            } else {
                worldUnset = true;
                sendConsoleMessage("§f§l" + gameInstance.getName() + "§f world doesn't exist yet.", MessageType.INFO);
                sendConsoleMessage("To create it, run /tt createWorld " + gameInstance.getName(), MessageType.INFO);
            }
            if (worldUnset) {
                sendConsoleMessage("To create all missing worlds, run /tt createWorld all", MessageType.INFO);
            }
        }
    }

    private boolean setupNMS() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException a) {
            sendConsoleMessage("Unknown server version", MessageType.ERROR);
            return false;
        }
        if (version.hashCode() == -1156422964) {
            if (!version.equals("v1_8_R3"))
                return (this.nms != null);
            this.nms = new V1_8_R3();
        }
        return (this.nms != null);
    }

    public NMS getNms() {
        return this.nms;
    }

    public static String getColor(String st) {
        return ChatColor.translateAlternateColorCodes('&', st);
    }

    public Wand getWand() {
        return this.wand;
    }

    public static AmazingTowers getPlugin() {
        return plugin;
    }

    public GameInstance getGameInstance(Entity e) {
        String worldName = e.getWorld().getName();
        return games.get(worldName);
    }

    public GameInstance getGameInstance(Block e) {
        String worldName = e.getWorld().getName();
        return games.get(worldName);
    }

    public GameInstance getGameInstance(World w) {
        String worldName = w.getName();
        return games.get(worldName);
    }

    public static GameInstance getGameInstance(String worldName) {
        return games.get(worldName);
    }

    public Config getGlobalConfig() {
        return globalConfig;
    }

    public void createFolderBackup() {  //Crear la carpeta "backup"
        File folder = new File(plugin.getDataFolder(), "backup");
        if (!folder.exists() &&
                folder.mkdirs()) {
            sendConsoleMessage("The backup folder was created successfully", MessageType.INFO);
        }
    }

    public static HashMap<String, GameInstance> getGameInstances() {
        return games;
    }

    public void sendConsoleMessage(String msg, MessageType messageType) {
        getServer().getConsoleSender().sendMessage(messageType.getPrefix() + msg);
    }

    public boolean capitalismExists() {
        return SetupVault.getVaultEconomy() != null;
    }

    public GameInstance checkForInstanceToTp(Player player) {
        for (GameInstance gameInstance : games.values()) {
            if (!gameInstance.canJoin(player))
                continue;
            if (gameInstance.getGame() == null)
                continue;
            if (gameInstance.getWorld() == null)
                continue;
            if (gameInstance.getGame().getGameState() == GameState.FINISH)
                continue;
            return gameInstance;
        }
        return null;
    }

    public void resetGameInstance(GameInstance gameInstance) {
        String name = gameInstance.getName();
        games.remove(name);
        games.put(name, new GameInstance(this, name));
    }
}


