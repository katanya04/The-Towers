package mx.towers.pato14.game.utils;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Move {
    private final Game game;

    public Move(Game game) {
        this.game = game;
    }

    public void MoveDetect() {
        (new BukkitRunnable() {
            public void run() {
                if (!GameState.isState(GameState.GAME)) {
                    cancel();
                    return;
                }
                for (Player player : game.getPlayers()) {
                    if (player.getHealth() > 0.0D) {
                        if (Move.this.game.getTeams().getTeam(TeamColor.RED).containsPlayer(player.getName())) {
                            Move.this.poolBlue(player);
                            continue;
                        }
                        if (Move.this.game.getTeams().getTeam(TeamColor.BLUE).containsPlayer(player.getName())) {
                            Move.this.poolRed(player);
                        }
                    }
                }
            }
        }).runTaskTimer(this.game.getGameInstance().getPlugin(), 0L, this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.detection_playerinPool"));
    }

    private void poolRed(Player player) {
        if (Cuboide.InCuboide(Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.POOL_RED_1.getLocationString())), Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.POOL_RED_2.getLocationString())), player.getLocation())) {
            (this.game.getTeams().getTeam(TeamColor.BLUE)).sumarPunto();
            player.teleport(Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.BLUE_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            Bukkit.broadcastMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.PointsScored-Messages.bluePoint")
                    .replace("{Player}", player.getName())
                    .replace("%PointsRed%", String.valueOf((game.getTeams()).getTeam(TeamColor.RED).getPoints()))
                    .replace("%PointsBlue%", String.valueOf((game.getTeams()).getTeam(TeamColor.BLUE).getPoints()))));
            this.game.getGameInstance().getVault().setReward(player, RewardsEnum.POINT);
            for (Player p : game.getPlayers()) {
                if (game.getTeams().getTeam(TeamColor.BLUE).containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 2.0f);
                } else if (game.getTeams().getTeam(TeamColor.RED).containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                }
            }
            if ((game.getTeams().getTeam(TeamColor.BLUE)).getPoints() >= this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.Points")) {
                game.getFinish().Fatality(TeamColor.BLUE);
                GameState.setState(GameState.FINISH);
            }
            this.game.getGameInstance().getUpdates().updateScoreboardAll();
            this.game.getStats().addOne(player.getName(), StatType.POINTS);
        }
    }

    private void poolBlue(Player player) {
        if (Cuboide.InCuboide(Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.POOL_BLUE_1.getLocationString())), Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.POOL_BLUE_2.getLocationString())), player.getLocation())) {
            (this.game.getTeams().getTeam(TeamColor.RED)).sumarPunto();
            player.teleport(Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.RED_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            Bukkit.broadcastMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.PointsScored-Messages.redPoint")
                    .replace("{Player}", player.getName())
                    .replace("%PointsRed%", String.valueOf((game.getTeams()).getTeam(TeamColor.RED).getPoints()))
                    .replace("%PointsBlue%", String.valueOf((game.getTeams()).getTeam(TeamColor.BLUE).getPoints()))));
            this.game.getGameInstance().getVault().setReward(player, RewardsEnum.POINT);
            for (Player p : game.getPlayers()) {
                if (game.getTeams().getTeam(TeamColor.BLUE).containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                } else if (game.getTeams().getTeam(TeamColor.RED).containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 2.0f);
                }
            }
            if ((this.game.getTeams().getTeam(TeamColor.RED)).getPoints() >= this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.Points")) {
                this.game.getFinish().Fatality(TeamColor.RED);
                GameState.setState(GameState.FINISH);
            }
            this.game.getGameInstance().getUpdates().updateScoreboardAll();
            this.game.getStats().addOne(player.getName(), StatType.POINTS);
        }
    }

    private void checkPool(TeamColor teamColor, Player player) {
        if (Cuboide.InCuboide(Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.POOL_BLUE_1.getLocationString())), Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.POOL_BLUE_2.getLocationString())), player.getLocation())) {
            (this.game.getTeams().getTeam(TeamColor.RED)).sumarPunto();
            player.teleport(Locations.getLocationFromString(this.game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Locationshion.RED_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            Bukkit.broadcastMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.PointsScored-Messages.Point")
                    .replace("{Player}", player.getName())
                    .replace("{Color}", teamColor.getColor())
                    .replace("{Team}", teamColor.getColor())
                    ));
            this.game.getGameInstance().getVault().setReward(player, RewardsEnum.POINT);
            for (Player p : game.getPlayers()) {
                if (game.getTeams().getTeam(TeamColor.BLUE).containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                } else if (game.getTeams().getTeam(TeamColor.RED).containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 2.0f);
                }
            }
            if ((this.game.getTeams().getTeam(TeamColor.RED)).getPoints() >= this.game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("Options.Points")) {
                this.game.getFinish().Fatality(TeamColor.RED);
                GameState.setState(GameState.FINISH);
            }
            this.game.getGameInstance().getUpdates().updateScoreboardAll();
            this.game.getStats().addOne(player.getName(), StatType.POINTS);
        }
    }
}


