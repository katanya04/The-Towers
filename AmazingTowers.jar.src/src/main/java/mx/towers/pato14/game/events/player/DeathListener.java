package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.kits.KitDefault;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.enums.StatType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Locationshion;
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

public class DeathListener implements Listener {
    private AmazingTowers at = AmazingTowers.getPlugin();

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity().getPlayer();
        Player killer = e.getEntity().getKiller();
        if (killer == null) {
            if (this.at.getGame().getTeams().getBlue().containsPlayer(player.getName())) {
                e.setDeathMessage(this.at.getColor(this.at.getMessages().getString("messages.death-messages.unknownKillerBlue")
                        .replace("{Player}", player.getName())));
            } else if (this.at.getGame().getTeams().getRed().containsPlayer(player.getName())) {
                e.setDeathMessage(this.at.getColor(this.at.getMessages().getString("messages.death-messages.unknownKillerRed")
                        .replace("{Player}", player.getName())));
            } else {
                e.setDeathMessage(this.at.getColor(this.at.getMessages().getString("messages.death-messages.unknownKiller")
                        .replace("{Player}", player.getName())));
            }
        } else {
            if (this.at.getGame().getTeams().getBlue().containsPlayer(killer.getName())) {
                e.setDeathMessage(this.at.getColor(this.at.getMessages().getString("messages.death-messages.killedByBlue")
                        .replace("{Player}", player.getName())
                        .replace("{Killer}", killer.getName())));
            } else if (this.at.getGame().getTeams().getRed().containsPlayer(killer.getName())) {
                e.setDeathMessage(this.at.getColor(this.at.getMessages().getString("messages.death-messages.killedByRed")
                        .replace("{Player}", player.getName())
                        .replace("{Killer}", killer.getName())));
            }
            addRewardsKiller(killer);
        }
        this.at.getGame().getStats().addOne(player.getName(), StatType.DEATHS);
        this.at.getUpdates().updateScoreboard(player);
        if (this.at.getConfig().getBoolean("Options.protect_leatherArmor")) {
            for (ItemStack i : e.getDrops()) {
                if (i.getType() == Material.LEATHER_HELMET || i.getType() == Material.LEATHER_CHESTPLATE || i.getType() == Material.LEATHER_LEGGINGS || i.getType() == Material.LEATHER_BOOTS) {
                    i.setType(Material.AIR);
                }
            }
        }
        if (this.at.getConfig().getBoolean("Options.instant_respawn")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)AmazingTowers.getPlugin(), new Runnable() {
                public void run() {
                    player.setCanPickupItems(false);
                    player.spigot().respawn();
                }
            },  1L);
        }
    }

    private void addRewardsKiller(Player killer) {
        this.at.getGame().getStats().addOne(killer.getName(), StatType.KILLS);
        this.at.getUpdates().updateScoreboard(killer);
        this.at.getVault().setReward(killer, RewardsEnum.KILL);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!GameState.isState(GameState.LOBBY)) {
            if (this.at.getGame().getTeams().getBlue().containsPlayer(e.getPlayer().getName())) {
                e.setRespawnLocation(Locations.getLocationFromString(this.at.getLocations().getString(Locationshion.BLUE_SPAWN.getLocationString())));
                KitDefault.KitDe(e.getPlayer());
            } else if (this.at.getGame().getTeams().getRed().containsPlayer(e.getPlayer().getName())) {
                e.setRespawnLocation(Locations.getLocationFromString(this.at.getLocations().getString(Locationshion.RED_SPAWN.getLocationString())));
                KitDefault.KitDe(e.getPlayer());
            } else {
                e.setRespawnLocation(Locations.getLocationFromString(this.at.getLocations().getString(Locationshion.LOBBY.getLocationString())));
                Dar.DarItemsJoin(e.getPlayer(), GameMode.ADVENTURE);
            }
        } else {
            e.setRespawnLocation(Locations.getLocationFromString(this.at.getLocations().getString(Locationshion.LOBBY.getLocationString())));
            Dar.DarItemsJoin(e.getPlayer(), GameMode.ADVENTURE);
        }
        e.getPlayer().setCanPickupItems(true);
    }
}


