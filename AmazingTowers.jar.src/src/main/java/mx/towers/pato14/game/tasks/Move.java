package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.AreaUtil;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.locations.Pool;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import mx.towers.pato14.utils.stats.StatType;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Move {
    private final String instanceName;
    private final Pool[] pools;

    public Move(GameInstance gameInstance, Game game) {
        this.instanceName = gameInstance.getName();
        this.pools = new Pool[gameInstance.getNumberOfTeams()];
        setPools(gameInstance, game);
    }

    private void setPools(GameInstance gameInstance, Game game) {
        int i = 0;
        for (Team team : game.getTeams().getTeams()) {
            pools[i++] = new Pool(team,
                    gameInstance.getConfig(ConfigType.LOCATIONS).getStringList(Location.POOL.getPath(team.getTeamColor())));
        }
    }

    public void MoveDetect() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(instanceName);
        (new BukkitRunnable() {
            public void run() {
                if (!gameInstance.getGame().getGameState().equals(GameState.GAME)) {
                    cancel();
                    return;
                }
                for (Player player : gameInstance.getGame().getPlayers()) {
                    if (player.getHealth() > 0.0D && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        for (Pool pool : Move.this.pools) {
                            if (pool.getTeam().containsPlayer(player.getName()) || !pool.getTeam().respawnPlayers())
                                continue;
                            checkPool(pool, player, gameInstance);
                        }
                    }
                }
            }
        }).runTaskTimer(gameInstance.getPlugin(), 0L, gameInstance.getConfig(ConfigType.CONFIG).getInt("options.ticksPerPoolsCheck"));
    }

    private void checkPool(Pool pool, Player player, GameInstance gameInstance) {
        if (AreaUtil.isInsideArea(pool, player.getLocation())) {
            boolean bedwarsStyle = gameInstance.getGame().isBedwarsStyle();
            Team team = gameInstance.getGame().getTeams().getTeamByPlayer(player.getName());
            Team teamScored = pool.getTeam();
            player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.SPAWN.getPath(team.getTeamColor()))), PlayerTeleportEvent.TeleportCause.COMMAND);
            if (bedwarsStyle)
                teamScored.scorePoint(true);
            else
                team.scorePoint(false);
            gameInstance.getScoreUpdates().updateScoreboardAll();
            gameInstance.getGame().getStats().addOne(player.getName(), StatType.POINTS);
            gameInstance.getVault().giveReward(player, RewardsEnum.POINT);
            gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString(bedwarsStyle ? "scorePoint.pointBedwarsStyle" : "scorePoint.point")
                            .replace("{Player}", player.getName())
                            .replace("{Color}", team.getTeamColor().getColor())
                            .replace("{Team}", team.getTeamColor().getName(gameInstance))
                            .replace("{ColorTeamScored}", teamScored.getTeamColor().getColor())
                            .replace("{TeamScored}", teamScored.getTeamColor().getName(gameInstance))
                    , true);
            for (Player p : gameInstance.getGame().getPlayers()) {
                if (team.containsPlayer(p.getName())) {
                    p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 2.0f);
                } else if (!bedwarsStyle || (teamScored.containsPlayer(p.getName()) && teamScored.getPoints() > 0)) {
                    p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                } else if (teamScored.containsPlayer(p.getName()) && teamScored.getPoints() <= 0) {
                    p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1.0f, 1.0f);
                }
            }
            if (!bedwarsStyle) {
                if (team.getPoints() >= gameInstance.getConfig(ConfigType.CONFIG).getInt("options.pointsToWin") || gameInstance.getGame().isGoldenGoal()) {
                    gameInstance.getGame().getFinish().Fatality(team.getTeamColor());
                    gameInstance.getGame().setGameState(GameState.FINISH);
                }
            } else if (teamScored.getPoints() <= 0) {
                String title = AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("scorePoint.title.noRespawnTitle"));
                for (Player pl : teamScored.getListOnlinePlayers()) {
                    pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 1.f);
                    if (gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("scorePoint.title.enabled"))
                        ReflectionMethods.sendTitle(pl, title, "", 0, 50, 20);
                    else
                        pl.sendMessage(title);
                }
            }
        }
    }

    public Pool[] getPools() {
        return pools;
    }
}