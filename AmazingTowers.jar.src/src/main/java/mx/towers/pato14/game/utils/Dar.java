package mx.towers.pato14.game.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.kits.KitDefault;
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
        player.getInventory().setItem(p.getConfig().getInt("Items.itemRed.position"), p.getGame().getItem().getItemRedTeam().getItem());
        player.getInventory().setItem(p.getConfig().getInt("Items.itemBlue.position"), p.getGame().getItem().getItemBlueTeam().getItem());
        player.getInventory().setItem(p.getConfig().getInt("Items.itemSpectator.position"), p.getGame().getItem().getItemSpectator().getItem());
        if (p.getConfig().getBoolean("Options.bungeecord-support.enabled")) {
            player.getInventory().setItem(p.getConfig().getInt("Items.itemQuit.position"), p.getGame().getItem().getItemQuit().getItem());
        }
        if (p.getBook().getBoolean("book.enabled")) {
            player.getInventory().setItem(p.getBook().getInt("book.position"), p.getGame().getItemBook().getItem());
        }
        removePotion(player);
        NametagEdit.getApi().setPrefix(player, p.getColor(p.getConfig().getString("Options.team.default.prefix")));
        player.teleport(Locations.getLocationFromStringConfig(p.getLocations(), Locationshion.LOBBY), PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public static void darItemsJoinTeam(Player player) {
        removePotion(player);
        if (p.getGame().getTeams().getBlue().containsPlayer(player.getName())) {
            NametagEdit.getApi().clearNametag(player);
            player.teleport(Locations.getLocationFromString(p.getLocations().getString(Locationshion.BLUE_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setFoodLevel(20);
            player.setSaturation(5.f);
            player.setGameMode(GameMode.SURVIVAL);
            p.getGame().getTeams().getBlue().setNameTagPlayer(player);
            KitDefault.KitDe(player);
            p.getGame().getStats().setHashStats(player.getName());
        } else if (p.getGame().getTeams().getRed().containsPlayer(player.getName())) {
            NametagEdit.getApi().clearNametag(player);
            player.teleport(Locations.getLocationFromString(p.getLocations().getString(Locationshion.RED_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setFoodLevel(20);
            player.setSaturation(5.f);
            player.setGameMode(GameMode.SURVIVAL);
            p.getGame().getTeams().getRed().setNameTagPlayer(player);
            KitDefault.KitDe(player);
            p.getGame().getStats().setHashStats(player.getName());
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
            player.sendPluginMessage((Plugin) p, "BungeeCord", out.toByteArray());
        }
    }
}


