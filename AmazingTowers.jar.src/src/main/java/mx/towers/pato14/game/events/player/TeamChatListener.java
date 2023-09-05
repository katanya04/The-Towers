package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.rewards.SetupVault;
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
        if (instance == null)
            return;
        if (!instance.getConfig(ConfigType.CONFIG).getBoolean("options.chat.enabled"))
            return;
        e.setCancelled(true);
        String name = e.getPlayer().getName();
        String msg = e.getMessage();
        if (msg.startsWith("!!") && AmazingTowers.getPlugin().getGlobalConfig().getBoolean("globalChat.activated")) {
            for (Player player : AmazingTowers.getAllOnlinePlayers()) {
                player.sendMessage(AmazingTowers.getColor(AmazingTowers.getPlugin().getGlobalConfig()
                                .getString("globalChat.format").replace("%vault_prefix%", SetupVault
                                        .getPrefixRank(e.getPlayer())).replace("%player%", name))
                                .replace("%instance_name%", instance.getConfig(ConfigType.CONFIG).getString("name"))
                        .replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ?
                                ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!!", ""));
            }
            return;
        }
        List<Player> players = instance.getWorld().getPlayers();
        if (instance instanceof LobbyInstance || (instance instanceof GameInstance && ((GameInstance) instance).getGame() == null)) {
            for (Player player : players) {
                player.sendMessage(AmazingTowers.getColor(instance.getConfig(ConfigType.CONFIG)
                                .getString("options.chat.format.defaultChat").replace("%vault_prefix%", SetupVault
                                        .getPrefixRank(e.getPlayer())).replace("%player%", name))
                        .replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ?
                                ChatColor.translateAlternateColorCodes('&', msg) : msg));
            }
        } else if (instance instanceof GameInstance) {
            GameInstance gameInstance = (GameInstance) instance;
            Team team = gameInstance.getGame().getTeams().getTeamByPlayer(name);
            if (gameInstance.getGame().getGameState().equals(GameState.LOBBY) || gameInstance.getGame().getGameState().equals(GameState.PREGAME) || team == null || gameInstance.getGame().getTeams().containsNoRespawnPlayer(e.getPlayer().getName())) {
                for (Player player : players) {
                    player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                                    .getString("options.chat.format.defaultChat").replace("%vault_prefix%", SetupVault
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
}


