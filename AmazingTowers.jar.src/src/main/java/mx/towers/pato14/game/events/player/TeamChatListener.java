package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class TeamChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final TowersWorldInstance instance = AmazingTowers.getInstance(e.getPlayer());
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        if (gameInstance.getGame() == null)
            return;
        if (!gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.chat.enabled"))
            return;
        e.setCancelled(true);
        String name = e.getPlayer().getName();
        String msg = e.getMessage();
        List<Player> players = gameInstance.getGame().getPlayers();
        Team team = gameInstance.getGame().getTeams().getTeamByPlayer(name);
        if (gameInstance.getGame().getGameState().equals(GameState.LOBBY) || gameInstance.getGame().getGameState().equals(GameState.PREGAME) || team == null || gameInstance.getGame().getTeams().containsNoRespawnPlayer(e.getPlayer().getName())) {
            for (Player player : players) {
                player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                                .getString("options.chat.format.defaultChat").replace("%vault_prefix%", gameInstance.getVault()
                                        .getPrefixRank(e.getPlayer())).replace("%player%", name))
                        .replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ?
                                ChatColor.translateAlternateColorCodes('&', msg) : msg));
            }
        } else if (msg.startsWith("!")) {
            for (Player player : players) {
                player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                        .getString("options.chat.format.globalChat").replace("%team_color%", team.getTeamColor().getColor())
                        .replace("%player%", name).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ?
                                ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!", "")));
            }
        } else {
            for (Player player : players) {
                if (team.containsPlayerOnline(player.getName()))
                    player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                            .getString("options.chat.format.teamChat").replace("%team_color%", team.getTeamColor().getColor())
                            .replace("%team_prefix%", team.getPrefixTeam()).replace("%player%", name)
                            .replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ?
                                    ChatColor.translateAlternateColorCodes('&', msg) : msg)));
            }
        }
    }
}


