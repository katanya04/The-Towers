package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.utils.AreaUtil;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Pool;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Move {
    private final String instanceName;
    private final Pool[] pools;

    public Move(GameInstance gameInstance, Game game) {
        this.instanceName = gameInstance.getInternalName();
        this.pools = new Pool[gameInstance.getNumberOfTeams()];
        setPools(gameInstance, game);
    }

    private void setPools(GameInstance gameInstance, Game game) {
        int i = 0;
        for (ITeam team : game.getTeams().getTeams()) {
            pools[i++] = new Pool(team,
                    gameInstance.getConfig(ConfigType.LOCATIONS).getStringList(Location.POOL.getPath(team.getTeamColor())));
        }
    }

    public void checkPointScore() {
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
                            if (pool.getTeam().containsPlayer(player.getName()) || !pool.getTeam().doPlayersRespawn())
                                continue;
                            checkPool(pool, player, gameInstance);
                        }
                    }
                }
            }
        }).runTaskTimer(gameInstance.getPlugin(), 0L, gameInstance.getConfig(ConfigType.CONFIG).getInt("options.ticksPerPoolsCheck"));
    }

    private void checkPool(Pool pool, Player player, GameInstance gameInstance) {
        if (AreaUtil.isInsideArea(pool, player.getLocation()))
            gameInstance.getGame().getTeams().scorePoint(player, pool.getTeam());
    }

    public Pool[] getPools() {
        return pools;
    }
}