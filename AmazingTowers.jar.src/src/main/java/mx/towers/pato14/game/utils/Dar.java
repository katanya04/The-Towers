package mx.towers.pato14.game.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.kits.KitDefault;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

public class Dar {
    private static final AmazingTowers plugin = AmazingTowers.getPlugin();

    public static void DarItemsJoin(Player player, GameMode gameMode) {
        player.setHealth(20.0D);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setFoodLevel(20);
        player.setSaturation(5.f);
        player.setGameMode(gameMode);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        GameInstance gameInstance = plugin.getGameInstance(player);
        for (TeamColor teamColor : TeamColor.getTeams(gameInstance.getNumberOfTeams())) {
            player.getInventory().setItem(teamColor.ordinal(), plugin.getGameInstance(player).getGame().getItem().getItem(teamColor).getItem());
        }
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.bungeecord-support.enabled")) {
            player.getInventory().setItem(gameInstance.getNumberOfTeams(), plugin.getGameInstance(player).getGame().getItem().getItemQuit().getItem());
        }
        if (gameInstance.getConfig(ConfigType.BOOK).getBoolean("book.enabled")) {
            int position = gameInstance.getConfig(ConfigType.BOOK).getInt("book.position");
            player.getInventory().setItem(position > gameInstance.getNumberOfTeams() ? position : gameInstance.getNumberOfTeams() + 1, plugin.getGameInstance(player).getGame().getItemBook().getItem());
        }
        removePotion(player);
        NametagEdit.getApi().setPrefix(player, AmazingTowers.getColor(TeamColor.SPECTATOR.getColor()));
        player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public static void darItemsJoinTeam(Player player) {
        removePotion(player);
        GameInstance gameInstance = plugin.getGameInstance(player);
        for (Team team: gameInstance.getGame().getTeams().getTeams()) {
            if (team.containsPlayer(player.getName())) {
                NametagEdit.getApi().clearNametag(player);
                player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.SPAWN.getPath(team.getTeamColor()))), PlayerTeleportEvent.TeleportCause.COMMAND);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.setFoodLevel(20);
                player.setSaturation(5.f);
                player.setGameMode(GameMode.SURVIVAL);
                team.setNameTagPlayer(player);
                KitDefault.KitDe(player);
                gameInstance.getGame().getStats().setHashStats(player.getName());
            }
        }
    }

    private static void removePotion(Player player) {
        if (!player.getActivePotionEffects().isEmpty()) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }
    }

    public static void bungeecordTeleport(Player player) {
        if (plugin.getGameInstance(player).getConfig(ConfigType.CONFIG).getBoolean("Options.bungeecord-support.enabled")) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(plugin.getGameInstance(player).getConfig(ConfigType.CONFIG).getString("Options.bungeecord-support.server_name"));
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }
}


