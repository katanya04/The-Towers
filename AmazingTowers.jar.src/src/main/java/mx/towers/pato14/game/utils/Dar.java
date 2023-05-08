package mx.towers.pato14.game.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.kits.KitDefault;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

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
        for (TeamColor teamColor : TeamColor.getTeams(gameInstance.getGame().getNumberOfTeams())) {
            player.getInventory().setItem(gameInstance.getConfig(ConfigType.CONFIG).getInt("Items.item" + teamColor.toString().toLowerCase().replace(teamColor.toString().toLowerCase().charAt(0), teamColor.toString().charAt(0)) + ".position"), plugin.getGameInstance(player).getGame().getItem().getItem(teamColor).getItem());
        }
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.bungeecord-support.enabled")) {
            player.getInventory().setItem(gameInstance.getConfig(ConfigType.CONFIG).getInt("Items.itemQuit.position"), plugin.getGameInstance(player).getGame().getItem().getItemQuit().getItem());
        }
        if (gameInstance.getConfig(ConfigType.BOOK).getBoolean("book.enabled")) {
            player.getInventory().setItem(gameInstance.getConfig(ConfigType.BOOK).getInt("book.position"), plugin.getGameInstance(player).getGame().getItemBook().getItem());
        }
        removePotion(player);
        NametagEdit.getApi().setPrefix(player, AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("Options.team.default.prefix")));
        player.teleport(Locations.getLocationFromStringConfig(gameInstance.getConfig(ConfigType.LOCATIONS), Locationshion.LOBBY), PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public static void darItemsJoinTeam(Player player) {
        removePotion(player);
        for (Map.Entry<TeamColor, Team> team: plugin.getGameInstance(player).getGame().getTeams().getTeams().entrySet()) {
            if (team.getValue().containsPlayer(player.getName())) {
                NametagEdit.getApi().clearNametag(player);
                String location = team.getKey().name() + "_SPAWN";
                player.teleport(Locations.getLocationFromString(plugin.getGameInstance(player).getConfig(ConfigType.LOCATIONS).getString(Locationshion.valueOf(location).getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.setFoodLevel(20);
                player.setSaturation(5.f);
                player.setGameMode(GameMode.SURVIVAL);
                team.getValue().setNameTagPlayer(player);
                KitDefault.KitDe(player);
                plugin.getGameInstance(player).getGame().getStats().setHashStats(player.getName());
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


