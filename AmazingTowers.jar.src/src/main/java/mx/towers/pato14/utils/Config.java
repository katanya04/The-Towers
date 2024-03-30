package mx.towers.pato14.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.MessageType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config extends YamlConfiguration {
    private final String name;
    private final File file;

    public Config(String nameFile, Boolean defaults, String worldName) {
        this.name = nameFile;
        StringBuilder path = new StringBuilder();
        path.append(AmazingTowers.getPlugin().getDataFolder());
        if (!worldName.isEmpty()) {
            path.append("/instances/");
            path.append(worldName);
            path.append("/");
        }
        this.file = new File(path.toString(), nameFile);
        if (defaults)
            saveDefaultConfig(path.toString());
        else
            saveConfig();
    }

    public Config(String nameFile, Boolean defaults) {
        this(nameFile, defaults, "");
    }

    private void saveDefaultConfig(String path) {
        if (!this.file.exists()) {
            try {
                Files.createDirectories(Paths.get(path));
                FileUtils.copyInputStreamToFile(AmazingTowers.getPlugin().getResource(name), new File(path + "/" + name));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        loadConfig();
    }

    private void loadConfig() {
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            Utils.sendConsoleMessage("Error while loading " + name + " config file", MessageType.ERROR);
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void saveConfig() {
        try {
            save(file);
        } catch (IOException e) {
            Utils.sendConsoleMessage("Error while saving " + name + " config file", MessageType.ERROR);
        }
    }

    public static Object getFromDefault(String path, String name) {
        InputStream inputStream = AmazingTowers.getPlugin().getResource(name);
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(inputStream);
        return defaultConfig.getString(path);
    }

    public String getFileName() {
        return name;
    }
}


