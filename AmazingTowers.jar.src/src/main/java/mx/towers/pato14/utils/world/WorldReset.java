package mx.towers.pato14.utils.world;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class WorldReset {
    public static void copyWorld(File source, File target) {
        try {
            FileUtils.copyDirectory(source, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteWorld(File file) {
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
    }
}


