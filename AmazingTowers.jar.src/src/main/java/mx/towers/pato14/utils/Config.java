package mx.towers.pato14.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config extends YamlConfiguration {
    private final String name;
    private final File file;
    private final Plugin plugin;

    public Config(Plugin plugin, String nameFile, Boolean defaults, String worldName) {
        this.plugin = plugin;
        this.name = nameFile;
        StringBuilder path = new StringBuilder();
        path.append(getPlugin().getDataFolder());
        if (!worldName.isEmpty()) {
            path.append("/instances/");
            path.append(worldName);
            path.append("/");
        }
        this.file = new File(path.toString(), getNameFile());
        if (defaults)
            saveDefaultConfig();
        else
            saveConfig();
    }

    public Config(Plugin plugin, String nameFile, Boolean defaults) {
        this(plugin, nameFile, defaults, "");
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


