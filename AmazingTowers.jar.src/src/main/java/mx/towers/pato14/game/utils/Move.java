package mx.towers.pato14.game.utils;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.locations.Pool;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Move {
    private final Game game;
    private final Pool[] pools;

    public Move(Game game) {
        this.game = game;
        this.pools = new Pool[game.getTeams().getTeams().size()];
        setPools();
    }

    public void setPools() {
        for (int i = 0; i < game.getTeams().getTeams().size(); i++) {
            Team current = game.getTeams().getTeams().get(i);
            pools[i] = new Pool(current,
                    this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getStringList(Location.POOL.getPath(current.getTeamColor())));
        }
    }

    public void MoveDetect() {
        (new BukkitRunnable() {
            public void run() {
                if (!Move.this.game.getGameState().equals(GameState.GAME)) {
                    cancel();
                    return;
                }
                for (Player player : game.getPlayers()) {
                    if (player.getHealth() > 0.0D && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        for (Pool pool : pools) {
                            if (pool.getTeam().containsPlayer(player.getName()))
                                return;
                            checkPool(pool, player);
                        }
                    }
                }
            }
        }).runTaskTimer(this.game.getGameInstance().getPlugin(), 0L, this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.detection_playerinPool"));
    }

    private void checkPool(Pool pool, Player player) {
        if (Cuboide.InCuboide(pool, player.getLocation())) {
            Team team = this.game.getTeams().getTeamByPlayer(player);
            team.sumarPunto();
            player.teleport(Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Location.SPAWN.getPath(team.getTeamColor()))), PlayerTeleportEvent.TeleportCause.COMMAND);
            Bukkit.broadcastMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.PointsScored-Messages.Point")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", team.getTeamColor().getColor())
                    .replace("{Team}", team.getTeamColor().getColor())
                    ));
            this.game.getGameInstance().getVault().setReward(player, RewardsEnum.POINT);
            for (Player p : game.getPlayers()) {
                if (team.containsPlayer(p.getName())) {
                    p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 2.0f);
                } else {
                    p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                }
            }
            if (team.getPoints() >= this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.Points")) {
                this.game.getFinish().Fatality(team.getTeamColor());
                game.setGameState(GameState.FINISH);
            }
            this.game.getGameInstance().getUpdates().updateScoreboardGame(game);
            this.game.getStats().addOne(player.getName(), StatType.POINTS);
        }
    }

    public Pool[] getPools() {
        return pools;
    }

}


