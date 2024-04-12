package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface Prefixes {
    static void setPrefix(Player player, String prefix) {
        ReflectionMethods.setTabStyle(player, prefix, null, 0, player.getWorld().getPlayers());
    }
    static void clearPrefix(Player player) {
        ReflectionMethods.setTabStyle(player, null, null, 0, player.getWorld().getPlayers());
    }
    static String getPrefix(Player player) {
        return ReflectionMethods.tabTeam.get(player.getUniqueId());
    }
    static void updatePrefixes() {
        for (Map.Entry<UUID, String> entry : ReflectionMethods.tabTeam.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            setPrefix(p, entry.getValue());
        }
    }
}
