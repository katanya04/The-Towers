package mx.towers.pato14.game.tasks;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Dar {
    private static final AmazingTowers plugin = AmazingTowers.getPlugin();

    public static void joinMainLobby(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        Utils.resetPlayer(player);
        AmazingTowers.getLobby().getHotbarItems().giveHotbarItems(player);
    }

    public static void joinGameLobby(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        Utils.resetPlayer(player);
        GameInstance gameInstance = AmazingTowers.getGameInstance(player);
        gameInstance.getHotbarItems().giveHotbarItems(player);
        NametagEdit.getApi().setPrefix(player, AmazingTowers.getColor(TeamColor.SPECTATOR.getColor()));
        player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public static void joinTeam(Player player) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(player);
        Team team = gameInstance.getGame().getTeams().getTeamByPlayer(player.getName());
        if (team == null)
            return;
        NametagEdit.getApi().clearNametag(player);
        player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.SPAWN.getPath(team.getTeamColor()))), PlayerTeleportEvent.TeleportCause.COMMAND);
        Utils.resetPlayer(player);
        player.setGameMode(GameMode.SURVIVAL);
        team.setNameTagPlayer(player);
        gameInstance.getGame().applyKitToPlayer(player);
        gameInstance.getGame().getStats().setHashStats(player.getName());

    }

    public static void bungeecordTeleport(Player player) {
        if (AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(AmazingTowers.getGlobalConfig().getString("options.bungeecord.server_name"));
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }
}


