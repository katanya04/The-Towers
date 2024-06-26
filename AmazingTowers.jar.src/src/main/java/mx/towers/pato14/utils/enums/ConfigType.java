package mx.towers.pato14.utils.enums;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ConfigType {
    CONFIG(TowersWorldInstance.class),
    LOCATIONS(TowersWorldInstance.class),
    MESSAGES(TowersWorldInstance.class),
    SCOREBOARD(TowersWorldInstance.class),
    KITS(GameInstance.class),
    GAME_SETTINGS(GameInstance.class);
    private final Class<? extends TowersWorldInstance> aClass;
    ConfigType(Class<? extends TowersWorldInstance> aClass) {
        this.aClass = aClass;
    }
    public static List<ConfigType> getValues(Class<? extends TowersWorldInstance> instanceClass) {
        return Arrays.stream(values()).filter(o -> o.aClass.isAssignableFrom(instanceClass)).collect(Collectors.toList());
    }
    /*public <T> T get(String path, TowersWorldInstance instance, Class<T> clazz) {
        Object toret;
        toret = instance.getConfig(this).get(path);
        if (clazz.isInstance(toret))
            return (T) toret;
        toret = AmazingTowers.getConfig(this).get(path);
        if (clazz.isInstance(toret))
            return (T) toret;
        return (T) Config.getFromDefault(path, Utils.macroCaseToCamelCase(this.name()) + ".yml");
    }*/
}