package mx.towers.pato14;

import java.io.File;
import java.util.*;

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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AmazingTowers extends JavaPlugin {

    private static AmazingTowers plugin;
    private static Map<String, GameInstance> games;
    private NMS nms;
    private Wand wand;
    private Config globalConfig;
    public Conexion con;
    private final HashMap<String, PermissionAttachment> perms = new HashMap<String, PermissionAttachment>();

    public void onLoad() {
        if (!getDescription().getAuthors().contains("Pato14")) {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void onEnable() {
        plugin = this;
        games = new HashMap<>();

        if (!setupNMS()) {
            Bukkit.getConsoleSender().sendMessage("§c[AmazingTowers] Your server version is not compatible with this plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (getServer().getPluginManager().getPlugin("NametagEdit") == null) {
            Bukkit.getConsoleSender().sendMessage("§c[AmazingTowers] Not detected the 'NameTagEdit' plugin, disabling AmazingTowers");
            Bukkit.getPluginManager().disablePlugin((Plugin) this);
            return;
        }

        getCommand("towers").setExecutor(new TowerCommand(this));

        this.globalConfig = new Config(this, "globalConfig.yml", true);
        int numberOfInstances = this.globalConfig.getInt("Options.Instances.amount");
        for (int i = 0; i < numberOfInstances; i++)
            games.put("TheTowers" + (i + 1), new GameInstance(this,"TheTowers" + (i + 1)));

        if (getGlobalConfig().getBoolean("Options.mysql.active")) {
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



    private void enabledPlugin() {
        String prefix = "§f[§aAmazingTowers§f]";
        this.wand = new Wand();
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        getServer().getPluginManager().registerEvents(new SelectCofresillos(this), this);
        String version = null;
        getServer().getConsoleSender().sendMessage("");
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Bukkit.getConsoleSender().sendMessage("§a[AmazingTowers] §aServer§f: §f" + version);
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        getServer().getConsoleSender().sendMessage(prefix + " §a-----§f-§a-----§f-§a------§f-§a-----");
        getServer().getConsoleSender().sendMessage(prefix + " §aPlugin§f: §aEnabled §asuccessfully§f!");
        getServer().getConsoleSender().sendMessage(prefix + " §aVersion§f: §f" + getDescription().getVersion());
        getServer().getConsoleSender().sendMessage(prefix + " §aAuthor§f: §f[§aPato14§f, §aMarco2124§f]");
        getServer().getConsoleSender().sendMessage(prefix + " §a-----§f-§a-----§f-§a------§f-§a-----");
        Bukkit.getConsoleSender().sendMessage(Detectoreishon.getLocatioshionsTruee(true));
        Bukkit.getConsoleSender().sendMessage("");
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

    public static String getColor(String st) {
        return ChatColor.translateAlternateColorCodes('&', st);
    }

    public Wand getWand() {
        return this.wand;
    }

    public static AmazingTowers getPlugin() {
        return plugin;
    }
    public void message(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }
    public HashMap<String, PermissionAttachment> getPermissions() {
        return this.perms;
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
}


