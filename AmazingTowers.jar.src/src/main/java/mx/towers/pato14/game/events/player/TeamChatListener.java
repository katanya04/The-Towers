package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TeamChatListener implements Listener {
    private AmazingTowers plugin;

    public TeamChatListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (this.plugin.getConfig().getBoolean("Options.chat.enabled")) {
            e.setCancelled(true);
            String name = e.getPlayer().getName();
            String msg = e.getMessage();
            if (GameState.isState(GameState.LOBBY) || GameState.isState(GameState.PREGAME) || !(this.plugin.getGame().getTeams().getRed().containsPlayer(name) || this.plugin.getGame().getTeams().getBlue().containsPlayer(name))) {
                for (Player onlines : Bukkit.getOnlinePlayers()) {
                    onlines.sendMessage(this.plugin.getColor(this.plugin.getConfig().getString("Options.chat.format.normalChat").replace("%prefix%", this.plugin.getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg));
                }
            } else if (msg.startsWith("!")) {
                if (this.plugin.getGame().getTeams().getRed().containsPlayer(name)) {
                    for (Player onlines : Bukkit.getOnlinePlayers()) {
                        onlines.sendMessage(this.plugin.getColor(this.plugin.getConfig().getString("Options.chat.format.redGlobal").replace("%prefix%", this.plugin.getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!", ""));
                    }
                } else if (this.plugin.getGame().getTeams().getBlue().containsPlayer(name)) {
                    for (Player onlines : Bukkit.getOnlinePlayers()) {
                        onlines.sendMessage(this.plugin.getColor(this.plugin.getConfig().getString("Options.chat.format.blueGlobal").replace("%prefix%", this.plugin.getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg).replaceFirst("!", ""));
                    }
                }
            } else if (this.plugin.getGame().getTeams().getRed().containsPlayer(name)) {
                for (Player onlines : Bukkit.getOnlinePlayers()) {
                    if (this.plugin.getGame().getTeams().getBlue().containsPlayer(onlines.getName()))
                        continue;
                    onlines.sendMessage(this.plugin.getColor(this.plugin.getConfig().getString("Options.chat.format.red").replace("%prefix%", this.plugin.getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg));
                }
            } else if (this.plugin.getGame().getTeams().getBlue().containsPlayer(name)) {
                for (Player onlines : Bukkit.getOnlinePlayers()) {
                    if (this.plugin.getGame().getTeams().getRed().containsPlayer(onlines.getName()))
                        continue;
                    onlines.sendMessage(this.plugin.getColor(this.plugin.getConfig().getString("Options.chat.format.blue").replace("%prefix%", this.plugin.getVault().getPrefixRank(e.getPlayer())).replace("%player%", name)).replace("%msg%", e.getPlayer().hasPermission("towers.chat.color") ? ChatColor.translateAlternateColorCodes('&', msg) : msg));
                }
            }
        }
    }
}


