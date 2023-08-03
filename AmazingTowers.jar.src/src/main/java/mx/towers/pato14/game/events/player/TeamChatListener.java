package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
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
        GameInstance gameInstance = this.plugin.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.chat.enabled")) {
            e.setCancelled(true);
            String name = e.getPlayer().getName();
            String msg = e.getMessage();
            List<Player> players = gameInstance.getGame().getPlayers();
            if (gameInstance.getGame().getGameState().equals(GameState.LOBBY) || gameInstance.getGame().getGameState().equals(GameState.PREGAME) || !(gameInstance.getGame().getTeams().containsTeamPlayer(name))) {
                for (Player player : players) {
                    player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                            .getString("options.chat.format.defaultChat").replace("%vault_prefix%", gameInstance.getVault()
                                    .getPrefixRank(e.getPlayer())).replace("%player%", name))
                            .replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ?
                                    ChatColor.translateAlternateColorCodes('&', msg) : msg));
                }
            } else if (msg.startsWith("!")) {
                Team team = gameInstance.getGame().getTeams().getTeamByPlayer(e.getPlayer());
                for (Player player : players) {
                    player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG)
                            .getString("options.chat.format.globalChat").replace("%team_color%", team.getTeamColor().getColor())
                            .replace("%player%", name).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ?
                                    ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!", "")));
                }
            } else {
                Team team = gameInstance.getGame().getTeams().getTeamByPlayer(e.getPlayer());
                for (Player player : players) {
                    if (team.containsPlayer(player.getName()))
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


