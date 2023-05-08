package mx.towers.pato14.game.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.kits.KitDefault;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.enums.Team;
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
        for (Team team : Team.getTeams(plugin.getGameInstance(player).getGame().getNumberOfTeams())) {
            player.getInventory().setItem(plugin.getConfig().getInt("Items.item" + team.toString().toLowerCase().replace(team.toString().toLowerCase().charAt(0), team.toString().charAt(0)) + ".position"), plugin.getGameInstance(player).getGame().getItem().getItem(team).getItem());
        }
        if (plugin.getConfig().getBoolean("Options.bungeecord-support.enabled")) {
            player.getInventory().setItem(plugin.getConfig().getInt("Items.itemQuit.position"), plugin.getGameInstance(player).getGame().getItem().getItemQuit().getItem());
        }
        if (plugin.getGameInstance(player).getConfig(ConfigType.BOOK).getBoolean("book.enabled")) {
            player.getInventory().setItem(plugin.getGameInstance(player).getConfig(ConfigType.BOOK).getInt("book.position"), plugin.getGameInstance(player).getGame().getItemBook().getItem());
        }
        removePotion(player);
        NametagEdit.getApi().setPrefix(player, AmazingTowers.getColor(plugin.getConfig().getString("Options.team.default.prefix")));
        player.teleport(Locations.getLocationFromStringConfig(plugin.getGameInstance(player).getConfig(ConfigType.LOCATIONS), Locationshion.LOBBY), PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public static void darItemsJoinTeam(Player player) {
        removePotion(player);
        for (Team team : Team.getMatchTeams(plugin.getGameInstance(player).getGame().getNumberOfTeams())) {
            if (plugin.getGameInstance(player).getGame().getTeams().getTeam(team).containsPlayer(player.getName())) {
                NametagEdit.getApi().clearNametag(player);
                String location = team.name() + "_SPAWN";
                player.teleport(Locations.getLocationFromString(plugin.getGameInstance(player).getConfig(ConfigType.LOCATIONS).getString(Locationshion.valueOf(location).getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.setFoodLevel(20);
                player.setSaturation(5.f);
                player.setGameMode(GameMode.SURVIVAL);
                plugin.getGameInstance(player).getGame().getTeams().getTeam(team).setNameTagPlayer(player);
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
        if (plugin.getConfig().getBoolean("Options.bungeecord-support.enabled")) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(plugin.getConfig().getString("Options.bungeecord-support.server_name"));
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }
}


