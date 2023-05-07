package mx.towers.pato14.utils.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

public class WorldLoad {
    private String name;
    private File source;
    private File target;

    public WorldLoad(String worldName, String sourceFolder, String targetFolder) {
        this.name = worldName;
        this.source = new File(sourceFolder);
        this.target = new File(targetFolder);
    }

    public void loadWorld() {
        File file = new File(getNameWorld());
        if (file.exists()) {                                 //Borra el mundo que estaba de la anterior partida
            Bukkit.unloadWorld(getNameWorld(), false);
            WorldReset.deleteWorld(file);
        }                                                    //Lo sobreescribe con el de backup
        WorldReset.copyWorld(this.source, this.target);
        Bukkit.createWorld(new WorldCreator(getNameWorld()));
    }

    public String getNameWorld() {
        return this.name;
    }

    public File getFileSource() {
        return this.source;
    }

    public File getFileTarget() {
        return this.target;
    }
}


