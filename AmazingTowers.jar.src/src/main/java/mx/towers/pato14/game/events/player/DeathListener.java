package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import mx.towers.pato14.utils.stats.StatType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        final Player player = e.getEntity().getPlayer();
        final GameInstance gameInstance = AmazingTowers.getGameInstance(player);
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        final Player killer = e.getEntity().getKiller();
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.instantRespawn")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(AmazingTowers.getPlugin(), () -> {
                player.setCanPickupItems(false);
                player.spigot().respawn();
            }, 1L);
        }
        e.setDeathMessage(null);
        final Team playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(player.getName());
        final String playerColor = playerTeam == null ? "&f" : playerTeam.getTeamColor().getColor();
        final String finalKill = playerTeam == null || playerTeam.respawnPlayers() ? "" : Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("deathMessages.finalKillPrefix"));
        if (killer == null) {
            gameInstance.broadcastMessage(finalKill + gameInstance.getConfig(ConfigType.MESSAGES).getString("deathMessages.unknownKiller")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", playerColor), true);
        } else {
            final Team killerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(killer.getName());
            final String killerColor = killerTeam == null ? "&f" : killerTeam.getTeamColor().getColor();
            gameInstance.broadcastMessage(finalKill + gameInstance.getConfig(ConfigType.MESSAGES).getString("deathMessages.knownKiller")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", playerColor)
                    .replace("{ColorKiller}", killerColor)
                    .replace("{Killer}", killer.getName()), true);
            addRewardsKiller(killer);
        }
        gameInstance.getGame().getStats().addOne(player.getName(), StatType.DEATHS);
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.canNotDropLeatherArmor")) {
            for (ItemStack i : e.getDrops()) {
                if (Utils.isLeatherArmor(i.getType())) {
                    i.setType(Material.AIR);
                }
            }
        }
        if (gameInstance.getGame().getGameState() == GameState.FINISH)
            return;
        if (playerTeam != null && !playerTeam.respawnPlayers()) {
            playerTeam.setPlayerState(player.getName(), PlayerState.NO_RESPAWN);
            gameInstance.getScoreUpdates().updateScoreboardAll();
            if (playerTeam.getSizeOnlinePlayers() <= 0)
                Utils.checkForTeamWin(gameInstance);
        } else
            gameInstance.getScoreUpdates().updateScoreboard(player);
    }

    private void addRewardsKiller(Player killer) {
        AmazingTowers.getGameInstance(killer).getGame().getStats().addOne(killer.getName(), StatType.KILLS);
        AmazingTowers.getGameInstance(killer).getScoreUpdates().updateScoreboard(killer);
        AmazingTowers.getGameInstance(killer).getVault().giveReward(killer, RewardsEnum.KILL);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        final GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        final Team playerTeam = gameInstance.getGame().getTeams().getTeamByPlayer(e.getPlayer().getName());
        if ((gameInstance.getGame().getGameState().equals(GameState.GAME) ||
                gameInstance.getGame().getGameState().equals(GameState.GOLDEN_GOAL)) && playerTeam != null) {
            if (playerTeam.respawnPlayers() && !playerTeam.isEliminated()) {
                e.setRespawnLocation(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.SPAWN.getPath(playerTeam.getTeamColor()))));
                gameInstance.getGame().applyKitToPlayer(e.getPlayer());
            } else {
                e.setRespawnLocation(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())));
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
        } else {
            e.setRespawnLocation(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())));
            gameInstance.getGame().spawn(e.getPlayer());
        }
        e.getPlayer().setCanPickupItems(true);
    }
}


