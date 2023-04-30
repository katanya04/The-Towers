package mx.towers.pato14.utils.plugin;

import org.bukkit.plugin.Plugin;

public final class PluginA {
    private static Plugin plugin = null;

    public PluginA(Plugin pluginA) {
        plugin = pluginA;
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}


