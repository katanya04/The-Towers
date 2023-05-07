package mx.towers.pato14.game.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.kits.KitDefault;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.plugin.PluginA;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public class Dar {
    private static AmazingTowers p = AmazingTowers.getPlugin();

    public static void DarItemsJoin(Player player, GameMode gameMode) {
        player.setHealth(20.0D);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setFoodLevel(20);
        player.setSaturation(5.f);
        player.setGameMode(gameMode);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(p.getConfig().getInt("Items.itemRed.position"), p.getGameInstance(player).getGame().getItem().getItemRedTeam().getItem());
        player.getInventory().setItem(p.getConfig().getInt("Items.itemBlue.position"), p.getGameInstance(player).getGame().getItem().getItemBlueTeam().getItem());
        player.getInventory().setItem(p.getConfig().getInt("Items.itemSpectator.position"), p.getGameInstance(player).getGame().getItem().getItemSpectator().getItem());
        if (p.getConfig().getBoolean("Options.bungeecord-support.enabled")) {
            player.getInventory().setItem(p.getConfig().getInt("Items.itemQuit.position"), p.getGameInstance(player).getGame().getItem().getItemQuit().getItem());
        }
        if (p.getGameInstance(player).getConfig(ConfigType.BOOK).getBoolean("book.enabled")) {
            player.getInventory().setItem(p.getGameInstance(player).getConfig(ConfigType.BOOK).getInt("book.position"), p.getGameInstance(player).getGame().getItemBook().getItem());
        }
        removePotion(player);
        NametagEdit.getApi().setPrefix(player, AmazingTowers.getColor(p.getConfig().getString("Options.team.default.prefix")));
        player.teleport(Locations.getLocationFromStringConfig(p.getGameInstance(player).getConfig(ConfigType.LOCATIONS), Locationshion.LOBBY), PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public static void darItemsJoinTeam(Player player) {
        removePotion(player);
        if (p.getGameInstance(player).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(player.getName())) {
            NametagEdit.getApi().clearNametag(player);
            player.teleport(Locations.getLocationFromString(p.getGameInstance(player).getConfig(ConfigType.LOCATIONS).getString(Locationshion.BLUE_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setFoodLevel(20);
            player.setSaturation(5.f);
            player.setGameMode(GameMode.SURVIVAL);
            p.getGameInstance(player).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).setNameTagPlayer(player);
            KitDefault.KitDe(player);
            p.getGameInstance(player).getGame().getStats().setHashStats(player.getName());
        } else if (p.getGameInstance(player).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(player.getName())) {
            NametagEdit.getApi().clearNametag(player);
            player.teleport(Locations.getLocationFromString(p.getGameInstance(player).getConfig(ConfigType.LOCATIONS).getString(Locationshion.RED_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setFoodLevel(20);
            player.setSaturation(5.f);
            player.setGameMode(GameMode.SURVIVAL);
            p.getGameInstance(player).getGame().getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).setNameTagPlayer(player);
            KitDefault.KitDe(player);
            p.getGameInstance(player).getGame().getStats().setHashStats(player.getName());
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
        if (p.getConfig().getBoolean("Options.bungeecord-support.enabled")) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(p.getConfig().getString("Options.bungeecord-support.server_name"));
            player.sendPluginMessage(p, "BungeeCord", out.toByteArray());
        }
    }
}


