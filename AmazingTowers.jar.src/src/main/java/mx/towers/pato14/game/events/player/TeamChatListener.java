package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.stream.Collectors;

public class TeamChatListener implements Listener {
    @EventHandler
    public static void onChat(AsyncPlayerChatEvent e) {
        final TowersWorldInstance instance = AmazingTowers.getInstance(e.getPlayer());
        if (instance == null || !instance.getConfig(ConfigType.CONFIG).getBoolean("options.chat.enabled"))
            return;
        e.setCancelled(true);
        String name = e.getPlayer().getName();
        String msg = e.getMessage();
        Collection<Player> players = null;
        if (e.getPlayer().hasPermission("towers.chat.color"))
            msg = Utils.getColor(msg);
        if (msg.startsWith("!!") && AmazingTowers.getGlobalConfig().getBoolean("globalChat.activated")) {
            msg = Utils.getColor(AmazingTowers.getGlobalConfig().getString("globalChat.format")
                    .replace("%vault_prefix%", SetupVault.getPrefixRank(e.getPlayer())).replace("%player%", name))
                    .replace("%instance_name%", instance.getConfig(ConfigType.CONFIG).getString("name"))
                    .replace("%msg%", msg).replaceFirst("!!", "");
            players = AmazingTowers.getAllOnlinePlayers();
        } else if (instance instanceof LobbyInstance || (instance instanceof GameInstance && ((GameInstance) instance).getGame() == null)) {
            msg = Utils.getColor(instance.getConfig(ConfigType.CONFIG).getString("options.chat.format.defaultChat")
                       .replace("%vault_prefix%", SetupVault.getPrefixRank(e.getPlayer())).replace("%player%", name))
                    .replace("%msg%", msg);
            players = instance.getWorld().getPlayers();
        } else if (instance instanceof GameInstance) {
            GameInstance gameInstance = (GameInstance) instance;
            Team team = gameInstance.getGame().getTeams().getTeamByPlayer(name);
            if (gameInstance.getGame().getGameState().equals(GameState.LOBBY) || gameInstance.getGame().getGameState().equals(GameState.PREGAME) || team == null || gameInstance.getGame().getTeams().containsNoRespawnPlayer(e.getPlayer().getName())) {
                msg = Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("options.chat.format.defaultChat")
                        .replace("%vault_prefix%", SetupVault.getPrefixRank(e.getPlayer())).replace("%player%", name))
                        .replace("%msg%", msg);
                players = instance.getWorld().getPlayers();
            } else if (msg.startsWith("!")) {
                msg = Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("options.chat.format.globalChat")
                        .replace("%team_color%", team.getTeamColor().getColor()).replace("%player%", name)
                        .replace("%msg%", msg).replaceFirst("!", ""));
                players = instance.getWorld().getPlayers();
            } else {
                msg = Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("options.chat.format.teamChat")
                        .replace("%team_color%", team.getTeamColor().getColor()).replace("%team_prefix%", team.getPrefixTeam())
                        .replace("%player%", name).replace("%msg%", msg));
                players = instance.getWorld().getPlayers().stream().filter(o -> team.containsPlayerOnline(o.getName())).collect(Collectors.toList());
            }
        }
        assert players != null;
        for (Player player : players)
            player.sendMessage(msg);
    }
}


