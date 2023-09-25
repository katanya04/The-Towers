package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.mysql.FindOneCallback;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.stats.Rank;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
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
    private final String name;
    public Finish(GameInstance gameInstance) {
        this.name = gameInstance.getName();
        seconds = gameInstance.getConfig(ConfigType.CONFIG).getInt("options.timerEndSeconds") + 1;
        bungeecord = AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled");
    }
    public void Fatality(final TeamColor teamColor) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(name);
        if (!gameInstance.getGame().getGameState().equals(GameState.FINISH)) {
            gameInstance.getGame().setGameState(GameState.FINISH);
        }
        for (String p: gameInstance.getGame().getStats().getPlayerStats().keySet()) {
            gameInstance.getGame().getStats().addOne(p, StatType.GAMES_PLAYED);
            if (gameInstance.getGame().getTeams().getTeam(teamColor).containsPlayer(p))
                gameInstance.getGame().getStats().addOne(p, StatType.WINS);
        }
        StatisticsPlayer stats = gameInstance.getGame().getStats();
        sendTitle(teamColor);
        (new BukkitRunnable() {
            public void run() {
                if (Finish.this.seconds == 0) {
                    cancel();
                    return;
                }
                if (Finish.this.seconds == 1) {
                    (new BukkitRunnable() {
                        public void run() {
                            gameInstance.setReadyToJoin(false);
                            GameInstance gameToTp;
                            for (Player player : gameInstance.getGame().getPlayers()) {
                                if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.sendPlayerToAnotherInstanceAtTheEnd")
                                        && (gameToTp = AmazingTowers.checkForInstanceToTp(player)) != null) {
                                    Utils.tpToWorld(gameToTp.getWorld(), gameInstance.getGame().getPlayers().toArray(new Player[0]));
                                    Dar.joinGameLobby(player);
                                } else {
                                    if (bungeecord) {
                                        player.teleport(Locations.getLocationFromString(gameInstance.getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
                                        Dar.bungeecordTeleport(player);
                                        if (gameInstance.getGame().getPlayers().isEmpty()) {
                                            run();
                                            return;
                                        }
                                        cancel();
                                        return;
                                    }
                                    player.kickPlayer(AmazingTowers.getColor(AmazingTowers.getGameInstance(player).getConfig(ConfigType.MESSAGES).getString("kickPlayersAtEndOfMatch")
                                            .replace("{Color}", teamColor.getColor())
                                            .replace("{Team}", teamColor.getName(gameInstance))
                                            .replace("%newLine%", "\n")));
                                }
                            }
                            Bukkit.unloadWorld(gameInstance.getName(), false);
                            gameInstance.reset();
                        }
                    }).runTaskLater(Finish.this.plugin, 60L);
                    if (gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("serverRestart.enabled")) {
                        gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("serverRestart.message"), true);
                    }
                }
                if (Finish.this.seconds == 9) {
                    Comparator<Stats> byKills = Comparator.comparingInt((Stats o) -> o.getStat(StatType.KILLS));
                    List<Map.Entry<String, Stats>> killsSorted =
                            stats.getPlayerStats().entrySet().stream()
                                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(byKills))).collect(Collectors.toList());
                    String topFiveKills = AmazingTowers.getColor(getTopFive(killsSorted, StatType.KILLS));
                    for (Player player : gameInstance.getGame().getPlayers()) {
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
                    for (Player player : gameInstance.getGame().getPlayers()) {
                        player.sendMessage("\n");
                        player.sendMessage(topFivePoints);
                        if (!topFivePoints.contains(player.getName()) && stats.getPlayerStats().containsKey(player.getName())) {
                            String msg = AmazingTowers.getColor(getPosition(pointsSorted, player.getName(), StatType.POINTS));
                            player.sendMessage(msg);
                        }
                    }
                } else if (Finish.this.seconds == 4) {
                    for (Player player : gameInstance.getGame().getPlayers()) {
                        if (stats.getPlayerStats().containsKey(player.getName()))
                            player.sendMessage("\n§lRango: ");
                    }
                } else if (Finish.this.seconds == 3) {
                    for (Player player : gameInstance.getGame().getPlayers()) {
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
                for (Player player : gameInstance.getGame().getPlayers()) {
                    if (gameInstance.getGame().getTeams().getTeam(teamColor).containsPlayer(player.getName()) &&
                            player.getGameMode() != GameMode.SPECTATOR) {
                        Finish.this.fireworks(player, teamColor.getColorEnum());
                    }
                }
                Finish.this.seconds = Finish.this.seconds - 1;
            }
        }).runTaskTimer(this.plugin, 0L, 20L);
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.rewards.vault") &&
                SetupVault.getVaultEconomy() != null) {
            for (Player player : gameInstance.getGame().getPlayers()) {
                if (gameInstance.getGame().getTeams().getTeam(teamColor).containsPlayer(player.getName())) {
                    AmazingTowers.getGameInstance(player).getVault().setReward(player, RewardsEnum.WIN);
                } else if (gameInstance.getGame().getTeams().getTeamByPlayer(player.getName()) != null)
                    AmazingTowers.getGameInstance(player).getVault().setReward(player, RewardsEnum.LOSER_TEAM);
            }
        }
        if (AmazingTowers.isConnectedToDatabase()) {
            FindOneCallback.updatePlayersDataAsync(gameInstance.getGame().getStats().getPlayerStats(), this.plugin, result -> {});
        }
    }

    private void fireworks(Player pl, Color color) {
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
        GameInstance gameInstance = AmazingTowers.getGameInstance(name);
        Config messages = gameInstance.getConfig(ConfigType.MESSAGES);
        if (messages.getBoolean("win.titles.enabled")) {
            String Title = AmazingTowers.getColor(messages.getString("win.titles.winTitle")
                    .replace("{Color}", teamColor.getColor())
                    .replace("{Team}", teamColor.getName(gameInstance).toUpperCase()));
            String Subtitle = AmazingTowers.getColor(messages.getString("win.titles.winSubTitle"));
            for (Player player : gameInstance.getGame().getPlayers()) {
                ReflectionMethods.sendTitle(player, Title, Subtitle, 10, 100, 20);
            }
        }
        gameInstance.broadcastMessage(messages.getString("win.chatMessage")
                .replace("{Color}", teamColor.getColor())
                .replace("{Team}", teamColor.getName(gameInstance)), true);
    }
    private String getTopFive(List<Map.Entry<String, Stats>> list, StatType stat) {
        Game game = AmazingTowers.getGameInstance(name).getGame();
        Iterator <Map.Entry<String, Stats>> listIterator = list.iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("&lTop ").append(stat.getText()).append("\n&r");
        int i;
        for (i = 0; i < 5 && listIterator.hasNext(); i++) {
            Map.Entry<String, Stats> current = listIterator.next();
            sb.append(game.getTeams().getTeamByPlayer(current.getKey()).getTeamColor().getColor());
            sb.append((i + 1)).append(". ").append(current.getKey()).append(" - ").append(current.getValue().getStat(stat)).append("\n");
            sb.append("&r");
        }
        return sb.toString();
    }
    private String getPosition(List<Map.Entry<String, Stats>> list, String p, StatType stat) {
        Game game = AmazingTowers.getGameInstance(name).getGame();
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
        sb.append(game.getTeams().getTeamByPlayer(p).getTeamColor().getColor());
        int value = current == null ? 0 : current.getValue().getStat(stat);
        sb.append(i).append(". ").append(p).append(" - ").append(value).append("\n");
        return sb.toString();
    }

    public void goldenGoal() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(name);
        if (gameInstance.getRules().get(Rule.BEDWARS_STYLE)) {
            for (Team team : gameInstance.getGame().getTeams().getTeams()) {
                if (team.respawnPlayers()) {
                    team.setPoints(0);
                    String title = AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("scorePoint.title.noRespawnTitle"));
                    for (Player pl : team.getListOnlinePlayers()) {
                        pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 1.f);
                        if (gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("scorePoint.title.enabled"))
                            ReflectionMethods.sendTitle(pl, title, "", 0, 50, 20);
                        else
                            pl.sendMessage(title);
                    }
                }
                gameInstance.getWorld().spawnEntity(Locations.getLocationFromString(gameInstance
                        .getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())), EntityType.ENDER_DRAGON);
            }
        } else {
            List<Team> mostPoints = new ArrayList<>();
            int mostPointsInt = gameInstance.getGame().getTeams().getTeams().get(0).getPoints();
            for (Team team : gameInstance.getGame().getTeams().getTeams()) {
                if (team.getPoints() >= mostPointsInt) {
                    if (team.getPoints() > mostPointsInt) {
                        mostPoints.clear();
                        mostPointsInt = team.getPoints();
                    }
                    mostPoints.add(team);
                }
            }
            if (mostPoints.size() == 1) {
                gameInstance.getGame().getFinish().Fatality(mostPoints.get(0).getTeamColor());
            } else {
                gameInstance.getGame().setGoldenGoal(true);
                String title = AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("goldenGoal.titles.title"));
                String subTitle = AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("goldenGoal.titles.subTitle"));
                for (Team team : gameInstance.getGame().getTeams().getTeams()) {
                    if (mostPoints.contains(team)) {
                        for (Player pl : team.getListOnlinePlayers()) {
                            if (gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("goldenGoal.titles.enabled"))
                                ReflectionMethods.sendTitle(pl, title, subTitle, 0, 50, 20);
                            else {
                                pl.sendMessage(title);
                                pl.sendMessage(subTitle);
                            }
                            pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 1.f);
                        }
                    } else {
                        team.eliminateTeam();
                    }
                }
            }
        }
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}


