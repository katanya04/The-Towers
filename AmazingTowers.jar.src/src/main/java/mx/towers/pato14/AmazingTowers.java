package mx.towers.pato14;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import mx.towers.pato14.commands.TowerCommand;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.scoreboard.ScoreUpdate;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.cofresillos.SelectCofresillos;
import mx.towers.pato14.utils.locations.Detectoreishon;
import mx.towers.pato14.utils.mysql.Conexion;
import mx.towers.pato14.utils.nms.*;
import mx.towers.pato14.utils.placeholders.Expansion;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.rewards.VaultT;
import mx.towers.pato14.utils.wand.Wand;
import mx.towers.pato14.utils.wand.WandListener;
import mx.towers.pato14.utils.world.WorldLoad;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AmazingTowers extends JavaPlugin {

    private static AmazingTowers plugin;
    private Game game;
    private Config locations;
    private Config messages;
    private Config scoreboard;
    private Config config;
    private Config book;
    private NMS nms;
    private ScoreUpdate scoreUpdate;
    private Wand wand;
    private VaultT vault;

    public void onLoad() {
        if (!getDescription().getAuthors().contains("Pato14")) {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void onEnable() {
        plugin = this;
        if (!setupNMS()) {
            Bukkit.getConsoleSender().sendMessage("§c[AmazingTowers] Your server version is not compatible with this plugin!");
            Bukkit.getPluginManager().disablePlugin((Plugin) this);
            return;
        }
        if (getServer().getPluginManager().getPlugin("NametagEdit") == null) {
            Bukkit.getConsoleSender().sendMessage("§c[AmazingTowers] Not detected the 'NameTagEdit' plugin, disabling AmazingTowers");
            Bukkit.getPluginManager().disablePlugin((Plugin) this);
            return;
        }
        createFolderBackup();
        getCommand("towers").setExecutor((CommandExecutor) new TowerCommand(this));
        registerConfigs();
        Detectoreishon.detectoreishonLocations();
        if (Detectoreishon.getLocationsObligatory()) {
            if (getConfig().getBoolean("Options.bungeecord-support.enabled")) {
                getServer().getMessenger().registerOutgoingPluginChannel((Plugin) this, "BungeeCord");
            }
            loadWorld("TheTowers");
            SetupVault.setupVault();
            this.vault = new VaultT(this);
            this.game = new Game(this);
            this.scoreUpdate = new ScoreUpdate(this);
        }
        if (getConfig().getBoolean("Options.mysql.active")) {
            try {
                this.con = new Conexion();
                this.con.Conectar();
                this.con.CreateTable();
                message("Mysql stats is enabled");
            } catch (Exception e) {
                message("connecting to MySql");
            }
        } else {
            message("Mysql stats is disabled");
        }
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Expansion(this).register();
        }
        enabledPlugin();
    }

    private void registerConfigs() {
        this.config = new Config((Plugin) this, "config.yml", Boolean.valueOf(true));
        this.locations = new Config((Plugin) this, "locations.yml", Boolean.valueOf(true));
        this.messages = new Config((Plugin) this, "messages.yml", Boolean.valueOf(true));
        this.scoreboard = new Config((Plugin) this, "scoreboard.yml", Boolean.valueOf(true));
        this.book = new Config((Plugin) this, "book.yml", Boolean.valueOf(true));
    }

    public void loadWorld(String worldName) {
        WorldLoad towers = new WorldLoad(worldName, String.valueOf(getDataFolder().getAbsolutePath()) + "/backup/" + worldName, String.valueOf(Bukkit.getWorldContainer().getAbsolutePath()) + "/" + worldName);
        if (towers.getFileSource().exists()) {
            towers.loadWorld();
        } else {
            Bukkit.getConsoleSender().sendMessage("§c[AmazingTowers] Not exists folder TheTowers in folder Backup!");
            return;
        }
    }

    private void enabledPlugin() {
        String prefix = "§f[§aAmazingTowers§f]";
        this.wand = new Wand();
        getServer().getPluginManager().registerEvents((Listener) new WandListener(this), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new SelectCofresillos(this), (Plugin) this);
        String version = null;
        getServer().getConsoleSender().sendMessage("");
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Bukkit.getConsoleSender().sendMessage("§a[AmazingTowers] §aServer§f: §f" + version);
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        getServer().getConsoleSender().sendMessage("§f[§aAmazingTowers§f] §a-----§f-§a-----§f-§a------§f-§a-----");
        getServer().getConsoleSender().sendMessage("§f[§aAmazingTowers§f] §aPlugin§f: §aEnabled §asuccessfully§f!");
        getServer().getConsoleSender().sendMessage("§f[§aAmazingTowers§f] §aVersion§f: §f" + getDescription().getVersion());
        getServer().getConsoleSender().sendMessage("§f[§aAmazingTowers§f] §aAuthor§f: §f[§aPato14§f, §aMarco2124§f]");
        getServer().getConsoleSender().sendMessage("§f[§aAmazingTowers§f] §a-----§f-§a-----§f-§a------§f-§a-----");
        Bukkit.getConsoleSender().sendMessage(Detectoreishon.getLocatioshionsTruee(true));
        Bukkit.getConsoleSender().sendMessage("");
    }

    public void createFolderBackup() {
        File folder = new File(getDataFolder(), "backup");
        if (!folder.exists() &&
                folder.mkdirs()) {
            System.out.println("[AmazingTowers] The backup folder created successfully");
        }
    }

    private boolean setupNMS() {
        String version = null;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        String str1;
        switch ((str1 = version).hashCode()) {
            case -1156422964:
                if (!str1.equals("v1_8_R3"))
                    break;
                this.nms = (NMS) new V1_8_R3();
                break;
        }
        return (this.nms != null);
    }

    public NMS getNms() {
        return this.nms;
    }

    public String getColor(String st) {
        return ChatColor.translateAlternateColorCodes('&', st);
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

    public Config getConfig() {
        return this.config;
    }

    public ScoreUpdate getUpdates() {
        return this.scoreUpdate;
    }

    public Wand getWand() {
        return this.wand;
    }

    public VaultT getVault() {
        return this.vault;
    }

    public Game getGame() {
        return this.game;
    }

    public static AmazingTowers getPlugin() {
        return plugin;
    }
    public void message(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }
    private final HashMap<String, PermissionAttachment> perms = new HashMap<String, PermissionAttachment>();
    public HashMap<String, PermissionAttachment> getPermissions() {
        return this.perms;
    }
    public Conexion con;
}


