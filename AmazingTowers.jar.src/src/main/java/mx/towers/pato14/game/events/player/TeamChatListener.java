package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class TeamChatListener implements Listener {
    private final AmazingTowers plugin;

    public TeamChatListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        GameInstance currentInstance = this.plugin.getGameInstance(e.getPlayer());
        if (currentInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.chat.enabled")) {
            e.setCancelled(true);
            String name = e.getPlayer().getName();
            String msg = e.getMessage();
            List<Player> players = currentInstance.getGame().getPlayers();
            if (GameState.isState(GameState.LOBBY) || GameState.isState(GameState.PREGAME) || !(currentInstance.getGame().getTeams().containsTeamPlayer(name))) {
                for (Player player : players) {
                    player.sendMessage(AmazingTowers.getColor(currentInstance.getConfig(ConfigType.CONFIG).getString("Options.chat.format.normalChat").replace("%prefix%", currentInstance.getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg));
                }
            } else if (msg.startsWith("!")) {
                String teamColor = currentInstance.getGame().getTeams().getTeamColorByPlayer(e.getPlayer()).name().toLowerCase();
                for (Player player : players) {
                    player.sendMessage(AmazingTowers.getColor(currentInstance.getConfig(ConfigType.CONFIG).getString("Options.chat.format." + teamColor + "Global").replace("%prefix%", currentInstance.getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!", ""));
                }
            } else {
                Team team = currentInstance.getGame().getTeams().getTeamByPlayer(e.getPlayer());
                String teamColor = currentInstance.getGame().getTeams().getTeamColorByPlayer(e.getPlayer()).name().toLowerCase();
                for (Player player : players) {
                    if (team.containsPlayer(player.getName()))
                        player.sendMessage(AmazingTowers.getColor(currentInstance.getConfig(ConfigType.CONFIG).getString("Options.chat.format." + teamColor + "Global").replace("%prefix%", currentInstance.getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!", ""));
                }
            }
        }
    }
}


