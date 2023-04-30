package mx.towers.pato14.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config extends YamlConfiguration {
    private String name;
    private File file;
    private final Plugin plugin;

    public Config(Plugin plugin, String nameFile, Boolean defaults) {
        this.plugin = plugin;
        this.name = nameFile;
        this.file = new File(getPlugin().getDataFolder(), getNameFile());
        if (defaults.booleanValue()) {
            saveDefaultConfig();
        } else {
            saveConfig();
        }
    }

    private void saveDefaultConfig() {
        if (!this.file.exists()) {
            getPlugin().saveResource(this.name, false);
        }
        loadConfig();
    }

    private void loadConfig() {
        try {
            load(getFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void saveConfig() {
        try {
            save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNameFile() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    private Plugin getPlugin() {
        return this.plugin;
    }
}


