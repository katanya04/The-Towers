package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamChatListener implements Listener {
    public enum ChatScope {
        TEAM, GLOBAL, DEFAULT, SUPER_GLOBAL;

        public static Set<ChatScope> getScopes(Collection<String> scopes) {
            return scopes.stream().map(ChatScope::parse).collect(Collectors.toSet());
        }

        private static ChatScope parse(String scopeName) {
            for (ChatScope chatScope : ChatScope.values())
                if (chatScope.name().equalsIgnoreCase(scopeName))
                    return chatScope;
            return null;
        }
    }

    @EventHandler
    public static void onChat(AsyncPlayerChatEvent e) {
        final TowersWorldInstance instance = AmazingTowers.getInstance(e.getPlayer());
        if (instance == null || !instance.getConfig(ConfigType.CONFIG).getBoolean("options.chat.enabled"))
            return;
        e.setCancelled(true);
        String name = e.getPlayer().getName();
        String msg = e.getMessage();
        Collection<Player> players = null;
        ChatScope chatScope = null;
        if (e.getPlayer().hasPermission("towers.chat.color"))
            msg = Utils.getColor(msg);
        if (msg.startsWith("!!") && AmazingTowers.getGlobalConfig().getBoolean("globalChat.activated")) {
            msg = Utils.getColor(AmazingTowers.getGlobalConfig().getString("globalChat.format")
                    .replace("%vault_prefix%", SetupVault.getPrefixRank(e.getPlayer())).replace("%player%", name))
                    .replace("%instance_name%", instance.getConfig(ConfigType.CONFIG).getString("name"))
                    .replace("%msg%", msg).replaceFirst("!!", "");
            players = AmazingTowers.getAllOnlinePlayers();
            chatScope = ChatScope.SUPER_GLOBAL;
        } else if (instance instanceof LobbyInstance || (instance instanceof GameInstance && ((GameInstance) instance).getGame() == null)) {
            msg = Utils.getColor(instance.getConfig(ConfigType.CONFIG).getString("options.chat.format.defaultChat")
                       .replace("%vault_prefix%", SetupVault.getPrefixRank(e.getPlayer())).replace("%player%", name))
                    .replace("%msg%", msg);
            players = instance.getWorld().getPlayers();
            chatScope = ChatScope.DEFAULT;
        } else if (instance instanceof GameInstance) {
            GameInstance gameInstance = (GameInstance) instance;
            ITeam team = gameInstance.getGame().getTeams().getTeamByPlayer(name);
            if (!gameInstance.getGame().getGameState().matchIsBeingPlayed || team == null || e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                msg = Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("options.chat.format.defaultChat")
                        .replace("%vault_prefix%", SetupVault.getPrefixRank(e.getPlayer())).replace("%player%", name))
                        .replace("%msg%", msg);
                players = instance.getWorld().getPlayers();
                chatScope = ChatScope.DEFAULT;
            } else if (msg.startsWith("!")) {
                msg = Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("options.chat.format.globalChat")
                        .replace("%team_color%", team.getTeamColor().getColor()).replace("%player%", name)
                        .replace("%msg%", msg).replaceFirst("!", "")
                        .replace("%team_prefix%", team.getPrefix()));
                players = instance.getWorld().getPlayers();
                chatScope = ChatScope.GLOBAL;
            } else {
                msg = Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("options.chat.format.teamChat")
                        .replace("%team_color%", team.getTeamColor().getColor()).replace("%team_prefix%", team.getPrefix())
                        .replace("%player%", name).replace("%msg%", msg));
                players = team.getOnlinePlayers();
                chatScope = ChatScope.TEAM;
            }
        }
        assert players != null;
        AmazingTowers.logger.logChat(msg, name, chatScope, instance);
        for (Player player : players)
            player.sendMessage(msg);
    }
}


