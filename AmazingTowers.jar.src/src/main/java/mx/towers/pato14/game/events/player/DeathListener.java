package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.kits.KitDefault;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class DeathListener implements Listener {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        final Player player = e.getEntity().getPlayer();
        final Player killer = e.getEntity().getKiller();
        final GameInstance gameInstance = this.plugin.getGameInstance(player);
        final Team playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(player);
        final String playerColor = playerTeam == null ? "&f" : playerTeam.getTeamColor().getColor();
        if (killer == null) {
            e.setDeathMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.death-messages.unknownKiller")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", playerColor)));
        } else {
            final Team killerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(killer);
            final String killerColor = killerTeam == null ? "&f" : killerTeam.getTeamColor().getColor();
            e.setDeathMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.death-messages.knownKiller") //to do, check colors
                    .replace("{Player}", player.getName())
                    .replace("{Color}", playerColor)
                    .replace("{ColorKiller}", killerColor)
                    .replace("{Killer}", killer.getName())));
            addRewardsKiller(killer);
        }
        gameInstance.getGame().getStats().addOne(player.getName(), StatType.DEATHS);
        gameInstance.getUpdates().updateScoreboard(player);
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.protect_leatherArmor")) {
            for (ItemStack i : e.getDrops()) {
                if (i.getType() == Material.LEATHER_HELMET || i.getType() == Material.LEATHER_CHESTPLATE || i.getType() == Material.LEATHER_LEGGINGS || i.getType() == Material.LEATHER_BOOTS) {
                    i.setType(Material.AIR);
                }
            }
        }
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("Options.instant_respawn")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(AmazingTowers.getPlugin(), () -> {
                player.setCanPickupItems(false);
                player.spigot().respawn();
            },  1L);
        }
    }

    private void addRewardsKiller(Player killer) {
        this.plugin.getGameInstance(killer).getGame().getStats().addOne(killer.getName(), StatType.KILLS);
        this.plugin.getGameInstance(killer).getUpdates().updateScoreboard(killer);
        this.plugin.getGameInstance(killer).getVault().setReward(killer, RewardsEnum.KILL);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        final GameInstance gameInstance = this.plugin.getGameInstance(e.getPlayer());
        final Team playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(e.getPlayer());
        if (!gameInstance.getGame().getGameState().equals(GameState.LOBBY) && playerTeam != null) {
            String teamColor = playerTeam.getTeamColor().toString().toUpperCase();
            e.setRespawnLocation(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Locationshion.valueOf(teamColor + "_SPAWN").getLocationString())));
            KitDefault.KitDe(e.getPlayer());
        } else {
            e.setRespawnLocation(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())));
            Dar.DarItemsJoin(e.getPlayer(), GameMode.ADVENTURE);
        }
        e.getPlayer().setCanPickupItems(true);
    }
}


