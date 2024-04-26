package mx.towers.pato14.game.team;

import mx.towers.pato14.utils.nms.ReflectionMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface Prefixes {
    static void setPrefix(String playerName, String prefix) {
        Player p = Bukkit.getPlayer(playerName);
        ReflectionMethods.setTabStyle(playerName, prefix, null, prefix.hashCode(), p == null ? null : p.getWorld().getPlayers());
    }
    static void setPrefixPriority(String playerName, String prefix, int priority) {
        Player p = Bukkit.getPlayer(playerName);
        ReflectionMethods.setTabStyle(playerName, prefix, null, priority, p == null ? null : p.getWorld().getPlayers());
    }
    static void clearPrefix(String playerName) {
        Player p = Bukkit.getPlayer(playerName);
        ReflectionMethods.setTabStyle(playerName, null, null, 0, p == null ? null : p.getWorld().getPlayers());
    }
    static String getPrefix(String playerName) {
        return ReflectionMethods.tabTeam.get(playerName);
    }
    static void updatePrefixes() {
        ReflectionMethods.tabTeam.forEach(Prefixes::setPrefix);
    }
}
