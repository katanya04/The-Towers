package mx.towers.pato14.game.utils;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Cuboide;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.enums.StatType;
import mx.towers.pato14.utils.enums.Team;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Move {
    private final AmazingTowers a;

    public Move(AmazingTowers plugin) {
        this.a = plugin;
    }

    public void MoveDetect() {
        (new BukkitRunnable() {
            public void run() {
                if (!GameState.isState(GameState.GAME)) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getHealth() > 0.0D) {
                        if (Move.this.a.getGame().getTeams().getRed().containsPlayer(player.getName())) {
                            Move.this.poolBlue(player);
                            continue;
                        }
                        if (Move.this.a.getGame().getTeams().getBlue().containsPlayer(player.getName())) {
                            Move.this.poolRed(player);
                        }
                    }
                }
            }
        }).runTaskTimer((Plugin) this.a, 0L, this.a.getConfig().getInt("Options.detection_playerinPool"));
    }

    private void poolRed(Player player) {
        if (Cuboide.InCuboide(Locations.getLocationFromString(this.a.getLocations().getString(Locationshion.POOL_RED_1.getLocationString())), Locations.getLocationFromString(this.a.getLocations().getString(Locationshion.POOL_RED_2.getLocationString())), player.getLocation())) {
            (this.a.getGame().getTeams()).bluePoints++;
            player.teleport(Locations.getLocationFromString(this.a.getLocations().getString(Locationshion.BLUE_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            Bukkit.broadcastMessage(this.a.getColor(this.a.getMessages().getString("messages.PointsScored-Messages.bluePoint")
                    .replace("{Player}", player.getName())
                    .replace("%PointsRed%", String.valueOf((this.a.getGame().getTeams()).redPoints))
                    .replace("%PointsBlue%", String.valueOf((this.a.getGame().getTeams()).bluePoints))));
            this.a.getVault().setReward(player, RewardsEnum.POINT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (AmazingTowers.getPlugin().getGame().getTeams().getBlue().containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 2.0f);
                } else if (AmazingTowers.getPlugin().getGame().getTeams().getRed().containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                }
            }
            if ((this.a.getGame().getTeams()).bluePoints >= this.a.getConfig().getInt("Options.Points")) {
                this.a.getGame().getFinish().Fatality(Team.BLUE);
                GameState.setState(GameState.FINISH);
            }
            this.a.getUpdates().updateScoreboardAll();
            this.a.getGame().getStats().addOne(player.getName(), StatType.POINTS);
        }
    }

    private void poolBlue(Player player) {
        if (Cuboide.InCuboide(Locations.getLocationFromString(this.a.getLocations().getString(Locationshion.POOL_BLUE_1.getLocationString())), Locations.getLocationFromString(this.a.getLocations().getString(Locationshion.POOL_BLUE_2.getLocationString())), player.getLocation())) {
            (this.a.getGame().getTeams()).redPoints++;
            player.teleport(Locations.getLocationFromString(this.a.getLocations().getString(Locationshion.RED_SPAWN.getLocationString())), PlayerTeleportEvent.TeleportCause.COMMAND);
            Bukkit.broadcastMessage(this.a.getColor(this.a.getMessages().getString("messages.PointsScored-Messages.redPoint")
                    .replace("{Player}", player.getName())
                    .replace("%PointsRed%", String.valueOf((this.a.getGame().getTeams()).redPoints))
                    .replace("%PointsBlue%", String.valueOf((this.a.getGame().getTeams()).bluePoints))));
            this.a.getVault().setReward(player, RewardsEnum.POINT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (AmazingTowers.getPlugin().getGame().getTeams().getBlue().containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                } else if (AmazingTowers.getPlugin().getGame().getTeams().getRed().containsPlayer(p.getDisplayName())) {
                    p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 2.0f);
                }
            }
            if ((this.a.getGame().getTeams()).redPoints >= this.a.getConfig().getInt("Options.Points")) {
                this.a.getGame().getFinish().Fatality(Team.RED);
                GameState.setState(GameState.FINISH);
            }
            this.a.getUpdates().updateScoreboardAll();
            this.a.getGame().getStats().addOne(player.getName(), StatType.POINTS);
        }
    }
}


