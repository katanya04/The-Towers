package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.*;
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
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Finish {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();
    private int seconds;
    private final boolean bungeecord;
    private final Game game;
    public Finish(Game game) {
        this.game = game;
        seconds = game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("options.timerEndSeconds") + 1;
        bungeecord = plugin.getGlobalConfig().getBoolean("options.bungeecord.enabled");
    }
    public void Fatality(final TeamColor teamColor) {
        if (!game.getGameState().equals(GameState.FINISH)) {
            game.setGameState(GameState.FINISH);
        }
        for (String p: game.getStats().getPlayerStats().keySet()) {
            game.getStats().addOne(p, StatType.GAMES_PLAYED);
            if (game.getTeams().getTeam(teamColor).containsPlayer(p))
                game.getStats().addOne(p, StatType.WINS);
        }
        StatisticsPlayer stats = game.getStats();
        sendTitle(teamColor);
        (new BukkitRunnable() {
            public void run() {
                if (Finish.this.seconds == 0) {
                    if (game.getPlayers().size() > 0) {
                        return;
                    }
                    cancel();
                    (new BukkitRunnable() {
                        public void run() {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), game.getGameInstance().getConfig(ConfigType.CONFIG).getString("options.commandRunAtTheEnd"));
                        }
                    }).runTaskLater(Finish.this.plugin, 60L);
                    return;
                }
                if (Finish.this.seconds == 1) {
                    (new BukkitRunnable() {
                        public void run() {
                            if (plugin.getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
                                for (Player player : game.getPlayers()) {
                                    player.teleport(Locations.getLocationFromString(game.getGameInstance().getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
                                    Dar.bungeecordTeleport(player);
                                    if (game.getPlayers().size() == 0) {
                                        run();
                                        return;
                                    }
                                }
                                cancel();
                                return;
                            }
                            for (Player player : game.getPlayers()) {
                                player.kickPlayer(AmazingTowers.getColor(AmazingTowers.getPlugin().getGameInstance(player).getConfig(ConfigType.MESSAGES).getString("messages.kickPlayerinFinish")
                                        .replace("{Color}", teamColor.getColor())
                                        .replace("{Team}", teamColor.getName(game.getGameInstance()))
                                        .replace("%newLine%", "\n")));
                            }
                        }
                    }).runTaskLater(Finish.this.plugin, 60L);
                    if (Finish.this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getBoolean("messages.restart_server.enabled")) {
                        Finish.this.game.getGameInstance().broadcastMessage(Finish.this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.restart_server.message"), true);
                    }
                }
                if (Finish.this.seconds == 9) {
                    Comparator<Stats> byKills = Comparator.comparingInt((Stats o) -> o.getStat(StatType.KILLS));
                    List<Map.Entry<String, Stats>> killsSorted =
                            stats.getPlayerStats().entrySet().stream()
                                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(byKills))).collect(Collectors.toList());
                    String topFiveKills = AmazingTowers.getColor(getTopFive(killsSorted, StatType.KILLS));
                    for (Player player : game.getPlayers()) {
                        player.sendMessage("\n");
                        player.sendMessage(topFiveKills);
                        if (!topFiveKills.contains(player.getName()) && stats.getPlayerStats().containsKey(player.getName())) {
                            String msg = AmazingTowers.getColor(getPosition(killsSorted, player.getName(), StatType.KILLS));
                            player.sendMessage(msg);
                        }
                    }
                } else if (Finish.this.seconds == 6) {
                    Comparator<Stats> byPoints = Comparator.comparingInt((Stats o) -> o.getStat(StatType.POINTS));
                    List<Map.Entry<String, Stats>> pointsSorted =
                            stats.getPlayerStats().entrySet().stream()
                                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(byPoints))).collect(Collectors.toList());
                    String topFivePoints = AmazingTowers.getColor(getTopFive(pointsSorted, StatType.POINTS));
                    for (Player player : game.getPlayers()) {
                        player.sendMessage("\n");
                        player.sendMessage(topFivePoints);
                        if (!topFivePoints.contains(player.getName()) && stats.getPlayerStats().containsKey(player.getName())) {
                            String msg = AmazingTowers.getColor(getPosition(pointsSorted, player.getName(), StatType.POINTS));
                            player.sendMessage(msg);
                        }
                    }
                } else if (Finish.this.seconds == 4) {
                    for (Player player : game.getPlayers()) {
                        if (stats.getPlayerStats().containsKey(player.getName()))
                            player.sendMessage("\n§lRango: ");
                    }
                } else if (Finish.this.seconds == 3) {
                    for (Player player : game.getPlayers()) {
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
                for (Player player : game.getPlayers()) {
                    if (game.getTeams().getTeam(teamColor).containsPlayer(player.getName()) &&
                            player.getGameMode() != GameMode.SPECTATOR) {
                        Finish.this.fuegosArtificiales(player, teamColor.getColorEnum());
                    }
                }
                Finish.this.seconds = Finish.this.seconds - 1;
            }
        }).runTaskTimer(this.plugin, 0L, 20L);
        if (game.getGameInstance().getConfig(ConfigType.CONFIG).getBoolean("options.rewards.vault") &&
                SetupVault.getVaultEconomy() != null) {
            for (Player player : game.getPlayers()) {
                if (game.getTeams().getTeam(teamColor).containsPlayer(player.getName())) {
                    this.plugin.getGameInstance(player).getVault().setReward(player, RewardsEnum.WIN);
                } else if (game.getTeams().containsTeamPlayer(player))
                    this.plugin.getGameInstance(player).getVault().setReward(player, RewardsEnum.LOSER_TEAM);
            }
        }
        if (game.getGameInstance().getConfig(ConfigType.CONFIG).getBoolean("options.mysql.active")) {
            FindOneCallback.updatePlayersDataAsync(game.getStats().getPlayerStats(), this.plugin, result -> {});
        }
    }

    private void fuegosArtificiales(Player pl, Color color) {
        Firework f = pl.getLocation().getWorld().spawn(pl.getLocation(), Firework.class);
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

    private void sendTitle(TeamColor teamColor) {
        Config messages = game.getGameInstance().getConfig(ConfigType.MESSAGES);
        if (messages.getBoolean("messages.Win-Messages.titles.enabled")) {
            String Title = AmazingTowers.getColor(messages.getString("messages.Win-Messages.titles.WinTitle")
                    .replace("{Color}", teamColor.getColor())
                    .replace("{Team}", teamColor.getName(game.getGameInstance()).toUpperCase()));
            String Subtitle = AmazingTowers.getColor(messages.getString("messages.Win-Messages.titles.WinSubTitle"));
            for (Player player : game.getPlayers()) {
                this.plugin.getNms().sendTitle(player, Title, Subtitle, 10, 100, 20);
            }
        }
        game.getGameInstance().broadcastMessage(messages.getString("messages.Win-Messages.Win")
                .replace("{Color}", teamColor.getColor())
                .replace("{Team}", teamColor.getName(game.getGameInstance())), true);
    }

    public int getSeconds() {
        return this.seconds;
    }
    private String getTopFive(List<Map.Entry<String, Stats>> list, StatType stat) {
        Iterator <Map.Entry<String, Stats>> listIterator = list.iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("&lTop ").append(stat.getText()).append("\n&r");
        int i;
        for (i = 0; i < 5 && listIterator.hasNext(); i++) {
            Map.Entry<String, Stats> current = listIterator.next();
            sb.append(game.getTeams().getTeamByPlayerIncludingOffline(current.getKey()).getTeamColor().getColor());
            sb.append((i + 1)).append(". ").append(current.getKey()).append(" - ").append(current.getValue().getStat(stat)).append("\n");
            sb.append("&r");
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
        sb.append(game.getTeams().getTeamByPlayerIncludingOffline(p).getTeamColor().getColor());
        int value = current == null ? 0 : current.getValue().getStat(stat);
        sb.append(i).append(". ").append(p).append(" - ").append(value).append("\n");
        return sb.toString();
    }
}


