package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.enums.StatType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Locationshion;
import mx.towers.pato14.utils.enums.Rank;
import mx.towers.pato14.utils.enums.Team;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.mysql.FindOneCallback;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Finish {
    private final AmazingTowers at = AmazingTowers.getPlugin();
    private int seconds = this.at.getConfig().getInt("Options.timerEndSeconds") + 1;
    private boolean bungeecord = this.at.getConfig().getBoolean("Options.bungeecord-support.enabled");
    public void Fatality(final Team team) {
        if (!GameState.isState(GameState.FINISH)) {
            GameState.setState(GameState.FINISH);
        }
        for (String p: this.at.getGame().getStats().getPlayerStats().keySet()) {
            this.at.getGame().getStats().addOne(p, StatType.GAMES_PLAYED);
            if (this.at.getGame().getTeams().getTeam(team).containsPlayer(p))
                this.at.getGame().getStats().addOne(p, StatType.WINS);
        }
        StatisticsPlayer stats = this.at.getGame().getStats();
        sendTitle(team);
        (new BukkitRunnable() {
            public void run() {
                if (Finish.this.seconds == 0) {
                    if (Bukkit.getOnlinePlayers().size() > 0) {
                        return;
                    }
                    cancel();
                    (new BukkitRunnable() {
                        public void run() {
                            Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), AmazingTowers.getPlugin().getConfig().getString("Options.command"));
                        }
                    }).runTaskLater((Plugin) Finish.this.at, 60L);
                    return;
                }
                if (Finish.this.seconds == 1) {
                    (new BukkitRunnable() {
                        public void run() {
                            if (AmazingTowers.getPlugin().getConfig().getBoolean("Options.bungeecord-support.enabled")) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.teleport(Locations.getLocationFromStringConfig(AmazingTowers.getPlugin().getLocations(), Locationshion.LOBBY), PlayerTeleportEvent.TeleportCause.COMMAND);
                                    Dar.bungeecordTeleport(player);
                                    if (Bukkit.getOnlinePlayers().size() == 0) {
                                        run();
                                        return;
                                    }
                                }
                                cancel();
                                return;
                            }
                            if (team == Team.RED) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.kickPlayer(AmazingTowers.getPlugin().getColor(AmazingTowers.getPlugin().getMessages().getString("messages.kickPlayerinFinishRed"))
                                            .replace("%newLine%", "\n"));
                                }
                            } else {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.kickPlayer(AmazingTowers.getPlugin().getColor(AmazingTowers.getPlugin().getMessages().getString("messages.kickPlayerinFinishBlue"))
                                            .replace("%newLine%", "\n"));
                                }
                            }
                        }
                    }).runTaskLater((Plugin) Finish.this.at, 60L);
                    if (Finish.this.at.getMessages().getBoolean("messages.restart_server.enabled")) {
                        Bukkit.broadcastMessage(Finish.this.at.getColor(Finish.this.at.getMessages().getString("messages.restart_server.message")));
                    }
                }
                if (Finish.this.seconds == 9) {
                    Comparator<Stats> byKills = Comparator.comparingInt((Stats o) -> o.getStat(StatType.KILLS));
                    List<Map.Entry<String, Stats>> killsSorted =
                            stats.getPlayerStats().entrySet().stream()
                                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(byKills))).collect(Collectors.toList());
                    String topFiveKills = getTopFive(killsSorted, StatType.KILLS);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage("\n");
                        player.sendMessage(topFiveKills);
                        if (!topFiveKills.contains(player.getName()) && stats.getPlayerStats().containsKey(player.getName())) {
                            String msg = getPosition(killsSorted, player.getName(), StatType.KILLS);
                            player.sendMessage(msg);
                        }
                    }
                } else if (Finish.this.seconds == 6) {
                    Comparator<Stats> byPoints = Comparator.comparingInt((Stats o) -> o.getStat(StatType.POINTS));
                    List<Map.Entry<String, Stats>> pointsSorted =
                            stats.getPlayerStats().entrySet().stream()
                                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(byPoints))).collect(Collectors.toList());
                    String topFivePoints = getTopFive(pointsSorted, StatType.POINTS);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage("\n");
                        player.sendMessage(topFivePoints);
                        if (!topFivePoints.contains(player.getName()) && stats.getPlayerStats().containsKey(player.getName())) {
                            String msg = getPosition(pointsSorted, player.getName(), StatType.POINTS);
                            player.sendMessage(msg);
                        }
                    }
                } else if (Finish.this.seconds == 4) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (stats.getPlayerStats().containsKey(player.getName()))
                            player.sendMessage("\n§lRango: ");
                    }
                } else if (Finish.this.seconds == 3) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (stats.getPlayerStats().containsKey(player.getName())) {
                            double killDeathRatio = stats.getStat(player.getName(), StatType.DEATHS) == 0 ?
                                    (stats.getStat(player.getName(), StatType.KILLS)) : (stats.getStat(player.getName(), StatType.KILLS)
                                    / (double) stats.getStat(player.getName(), StatType.DEATHS));
                            double points = killDeathRatio * 2.5 + stats.getStat(player.getName(), StatType.POINTS) * 1.5;
                            Rank rank = Rank.getRank(points);
                            player.sendMessage(rank.toText());
                            player.playSound(player.getLocation(), rank.getSound(), 1.0F, rank.getPitch());
                        }
                    }
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (team == Team.RED) {
                        if (Finish.this.at.getGame().getTeams().getRed().containsPlayer(player.getName()) &&
                                player.getGameMode() != GameMode.SPECTATOR) {
                            Finish.this.fuegosArtificiales(player, Color.RED);
                        }
                        continue;
                    }
                    if (Finish.this.at.getGame().getTeams().getBlue().containsPlayer(player.getName()) &&
                            player.getGameMode() != GameMode.SPECTATOR) {
                        Finish.this.fuegosArtificiales(player, Color.BLUE);
                    }
                }
                Finish.this.seconds = Finish.this.seconds - 1;
            }
        }).runTaskTimer((Plugin) this.at, 0L, 20L);
        if (this.at.getConfig().getBoolean("Options.Rewards.vault") &&
                SetupVault.getVaultEconomy() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (team == Team.RED) {
                    if (this.at.getGame().getTeams().getRed().containsPlayer(player.getName())) {
                        if (player.getGameMode() == GameMode.SURVIVAL)
                            this.at.getVault().setReward(player, RewardsEnum.WIN);
                        continue;
                    }
                    if (this.at.getGame().getTeams().getBlue().containsPlayer(player.getName()))
                        this.at.getVault().setReward(player, RewardsEnum.LOSER_TEAM);
                    continue;
                }
                if (this.at.getGame().getTeams().getBlue().containsPlayer(player.getName())) {
                    if (player.getGameMode() == GameMode.SURVIVAL)
                        this.at.getVault().setReward(player, RewardsEnum.WIN);
                    continue;
                }
                if (this.at.getGame().getTeams().getRed().containsPlayer(player.getName())) {
                    this.at.getVault().setReward(player, RewardsEnum.LOSER_TEAM);
                }
            }
        }
        if (this.at.getConfig().getBoolean("Options.mysql.active")) {
            FindOneCallback.updatePlayersDataAsync(this.at.getGame().getStats().getPlayerStats(), this.at,  result -> {});
        }
    }

    private void fuegosArtificiales(Player pl, Color color) {
        Firework f = (Firework) pl.getLocation().getWorld().spawn(pl.getLocation(), Firework.class);
        f.detonate();
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
                .flicker(false)
                .trail(true)
                .with(FireworkEffect.Type.BALL)
                .withColor(color)
                .withFade(color)
                .build());
        fm.setPower(1);
        f.setFireworkMeta(fm);
    }

    private void sendTitle(Team team) {
        if (team == Team.RED) {
            if (this.at.getMessages().getBoolean("messages.Win-Messages.titles.enabled")) {
                String Title = this.at.getColor(this.at.getMessages().getString("messages.Win-Messages.titles.redWinTitle"));
                String Subtitle = this.at.getColor(this.at.getMessages().getString("messages.Win-Messages.titles.redWinSubTitle"));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    this.at.getNms().sendTitle(player, Title, Subtitle, 10, 100, 20);
                }
            }
            Bukkit.broadcastMessage(this.at.getColor(this.at.getMessages().getString("messages.Win-Messages.redWin")));
        } else if (team == Team.BLUE) {
            if (this.at.getMessages().getBoolean("messages.Win-Messages.titles.enabled")) {
                String Title = this.at.getColor(this.at.getMessages().getString("messages.Win-Messages.titles.blueWinTitle"));
                String Subtitle = this.at.getColor(this.at.getMessages().getString("messages.Win-Messages.titles.blueWinSubTitle"));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    this.at.getNms().sendTitle(player, Title, Subtitle, 10, 100, 20);
                }
            }
            Bukkit.broadcastMessage(this.at.getColor(this.at.getMessages().getString("messages.Win-Messages.blueWin")));
        }
    }

    public int getSeconds() {
        return this.seconds;
    }
    private String getTopFive(List<Map.Entry<String, Stats>> list, StatType stat) {
        Iterator <Map.Entry<String, Stats>> listIterator = list.iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("§lTop ").append(stat.getText()).append("\n§r");
        int i;
        for (i = 0; i < 5 && listIterator.hasNext(); i++) {
            Map.Entry<String, Stats> current = listIterator.next();
            if (this.at.getGame().getTeams().getBlue().containsPlayer(current.getKey()))
                sb.append("§1");
            else if (this.at.getGame().getTeams().getRed().containsPlayer(current.getKey()))
                sb.append("§4");
            sb.append((i + 1)).append(". ").append(current.getKey()).append(" - ").append(current.getValue().getStat(stat)).append("\n");
            sb.append("§r");
        }
        return sb.toString();
    }
    private String getPosition(List<Map.Entry<String, Stats>> list, String p, StatType stat) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tu: ");
        Iterator <Map.Entry<String, Stats>> listIterator = list.iterator();
        int i = 0;
        Map.Entry<String, Stats> current = null;
        while (listIterator.hasNext()) {
            i++;
            current = listIterator.next();
            if (current.getKey().equals(p)) break;
        }
        if (this.at.getGame().getTeams().getBlue().containsPlayer(p))
            sb.append("§1");
        else if (this.at.getGame().getTeams().getRed().containsPlayer(p))
            sb.append("§4");
        int value = current == null ? 0 : current.getValue().getStat(stat);
        sb.append(i).append(". ").append(p).append(" - ").append(value).append("\n");
        return sb.toString();
    }
}


