package mx.towers.pato14.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.MessageType;
import org.apache.commons.io.FileUtils;
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
            saveDefaultConfig(path.toString());
        else
            saveConfig();
    }

    public Config(Plugin plugin, String nameFile, Boolean defaults) {
        this(plugin, nameFile, defaults, "");
    }

    private void saveDefaultConfig(String path) {
        if (!this.file.exists()) {
            try {
                Files.createDirectories(Paths.get(path));
                FileUtils.copyInputStreamToFile(getPlugin().getResource(getNameFile()), new File(path + "/" + getNameFile()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        loadConfig();
    }

    private void loadConfig() {
        try {
            load(getFile());
        } catch (IOException | InvalidConfigurationException e) {
            AmazingTowers.getPlugin().sendConsoleMessage("Error while loading " + name + " config file", MessageType.ERROR);
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void saveConfig() {
        try {
            save(getFile());
        } catch (IOException e) {
            AmazingTowers.getPlugin().sendConsoleMessage("Error while saving " + name + " config file", MessageType.ERROR);
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


