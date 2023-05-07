package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TeamChatListener implements Listener {
    private final AmazingTowers plugin;

    public TeamChatListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (this.plugin.getGameInstance(e.getPlayer()).getConfig(ConfigType.CONFIG).getBoolean("Options.chat.enabled")) {
            e.setCancelled(true);
            String name = e.getPlayer().getName();
            String msg = e.getMessage();
            if (GameState.isState(GameState.LOBBY) || GameState.isState(GameState.PREGAME) || !(this.plugin.getGameInstance(e.getPlayer()).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(name) || this.plugin.getGameInstance(e.getPlayer()).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(name))) {
                for (Player onlines : this.plugin.getGameInstance(e.getPlayer()).getGame().getPlayers()) {
                    onlines.sendMessage(AmazingTowers.getColor(this.plugin.getConfig().getString("Options.chat.format.normalChat").replace("%prefix%", this.plugin.getGameInstance(e.getPlayer()).getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg));
                }
            } else if (msg.startsWith("!")) {
                if (this.plugin.getGameInstance(e.getPlayer()).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(name)) {
                    for (Player onlines : this.plugin.getGameInstance(e.getPlayer()).getGame().getPlayers()) {
                        onlines.sendMessage(AmazingTowers.getColor(this.plugin.getConfig().getString("Options.chat.format.redGlobal").replace("%prefix%", this.plugin.getGameInstance(e.getPlayer()).getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!", ""));
                    }
                } else if (this.plugin.getGameInstance(e.getPlayer()).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(name)) {
                    for (Player onlines : this.plugin.getGameInstance(e.getPlayer()).getGame().getPlayers()) {
                        onlines.sendMessage(AmazingTowers.getColor(this.plugin.getConfig().getString("Options.chat.format.blueGlobal").replace("%prefix%", this.plugin.getGameInstance(e.getPlayer()).getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!", ""));
                    }
                }
            } else if (this.plugin.getGameInstance(e.getPlayer()).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(name)) {
                for (Player onlines : this.plugin.getGameInstance(e.getPlayer()).getGame().getPlayers()) {
                    if (this.plugin.getGameInstance(e.getPlayer()).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(onlines.getName()))
                        continue;
                    onlines.sendMessage(AmazingTowers.getColor(this.plugin.getConfig().getString("Options.chat.format.red").replace("%prefix%", this.plugin.getGameInstance(e.getPlayer()).getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg));
                }
            } else if (this.plugin.getGameInstance(e.getPlayer()).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(name)) {
                for (Player onlines : this.plugin.getGameInstance(e.getPlayer()).getGame().getPlayers()) {
                    if (this.plugin.getGameInstance(e.getPlayer()).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(onlines.getName()))
                        continue;
                    onlines.sendMessage(AmazingTowers.getColor(this.plugin.getConfig().getString("Options.chat.format.blue").replace("%prefix%", this.plugin.getGameInstance(e.getPlayer()).getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg));
                }
            }
        }
    }
}


