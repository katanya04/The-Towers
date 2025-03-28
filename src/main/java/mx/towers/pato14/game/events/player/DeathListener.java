package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import mx.towers.pato14.utils.stats.StatType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        final Player player = e.getEntity().getPlayer();
        final GameInstance gameInstance = AmazingTowers.getGameInstance(player);
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (player.isInsideVehicle())
            player.leaveVehicle();
        final Player killer = e.getEntity().getKiller();
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.instantRespawn")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(AmazingTowers.getPlugin(), () -> {
                player.setCanPickupItems(false);
                player.spigot().respawn();
            }, 1L);
        }
        e.setDeathMessage(null);
        final ITeam playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(player.getName());
        final String playerColor = playerTeam == null ? "&f" : playerTeam.getTeamColor().getColor();
        final String finalKill = playerTeam == null || playerTeam.doPlayersRespawn() ? "" : Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("deathMessages.finalKillPrefix"));
        if (killer == null) {
            gameInstance.broadcastMessage(finalKill + gameInstance.getConfig(ConfigType.MESSAGES).getString("deathMessages.unknownKiller")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", playerColor), true);
        } else {
            final ITeam killerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(killer.getName());
            final String killerColor = killerTeam == null ? "&f" : killerTeam.getTeamColor().getColor();
            boolean bowKill = player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE;
            gameInstance.broadcastMessage(finalKill + gameInstance.getConfig(ConfigType.MESSAGES).getString(bowKill ? "deathMessages.knownKillerProjectile" : "deathMessages.knownKiller")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", playerColor)
                    .replace("{ColorKiller}", killerColor)
                    .replace("{Killer}", killer.getName())
                    .replace("{Distance}", (bowKill ? String.valueOf((int) (Math.round(killer.getLocation().distance(player.getLocation())))) : "")), true);
            addRewardsKiller(killer);
            if (Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("itemOnKill.activated"))) {
                killer.getInventory().addItem(Utils.getItemsFromConf(gameInstance.getConfig(ConfigType.GAME_SETTINGS), "itemOnKill.item"));
            }
            killer.playSound(killer.getLocation(), Sound.SUCCESSFUL_HIT, 1f, 1f);
        }
        gameInstance.getGame().getStats().increaseOne(player.getName(), StatType.DEATHS);
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.doNotDropArmorAndTools")) {
            for (ItemStack i : e.getDrops()) {
                if (i.hasItemMeta() && i.getItemMeta().spigot().isUnbreakable() || 
                (i.getType() == Material.GOLDEN_APPLE && i.getDurability() == 0)) {
                i.setType(Material.AIR);
            }
            }
        }
        if (!gameInstance.getGame().getGameState().matchIsBeingPlayed)
            return;
        if (playerTeam != null && !playerTeam.doPlayersRespawn()) {
            player.setGameMode(GameMode.SPECTATOR);
            gameInstance.getScoreUpdates().updateScoreboardAll(false, gameInstance.getWorld().getPlayers());
            if (playerTeam.getNumAlivePlayers() <= 0)
                gameInstance.getGame().getTeams().checkForTeamWin();
        } else
            gameInstance.getScoreUpdates().updateScoreboard(player);
    }

    private void addRewardsKiller(Player killer) {
        AmazingTowers.getGameInstance(killer).getGame().getStats().increaseOne(killer.getName(), StatType.KILLS);
        AmazingTowers.getGameInstance(killer).getScoreUpdates().updateScoreboard(killer);
        AmazingTowers.getGameInstance(killer).getVault().giveReward(killer, RewardsEnum.KILL);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        final GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        final ITeam playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(e.getPlayer().getName());
        if (gameInstance.getGame().getGameState().matchIsBeingPlayed && playerTeam != null) {
            if (playerTeam.doPlayersRespawn() && !playerTeam.isEliminated()) {
                e.setRespawnLocation(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.SPAWN.getPath(playerTeam.getTeamColor()))));
                gameInstance.getGame().applyKitToPlayer(e.getPlayer());
            } else {
                e.setRespawnLocation(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())));
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
            Config settings = gameInstance.getConfig(ConfigType.GAME_SETTINGS);
            if (Boolean.parseBoolean(settings.getString("respawnInvincibility.activated"))) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(AmazingTowers.getPlugin(), () ->
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                        settings.getInt("respawnInvincibility.timeInSeconds") * 20, 5, true, false)),
                1L);
            }
        } else {
            e.setRespawnLocation(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())));
            gameInstance.getGame().spawn(e.getPlayer());
        }
        e.getPlayer().setCanPickupItems(true);
    }
}