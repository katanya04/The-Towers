package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.locations.Locations;
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
        this.name = gameInstance.getInternalName();
        seconds = gameInstance.getConfig(ConfigType.CONFIG).getInt("options.timerEndSeconds") + 1;
        bungeecord = AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled");
    }

    public void fatality(final TeamColor teamColor) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(name);
        if (!gameInstance.getGame().getGameState().equals(GameState.FINISH)) {
            gameInstance.getGame().setGameState(GameState.FINISH);
        }
        for (String p : gameInstance.getGame().getStats().getPlayerStats().keySet()) {
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
                            GameInstance gameToTp;
                            for (Player player : gameInstance.getGame().getPlayers()) {
                                if (Utils.getConfBoolDefaultsIfNull(gameInstance.getConfig(ConfigType.CONFIG), "options.sendPlayerToAnotherInstanceAtTheEnd")
                                        && (gameToTp = AmazingTowers.checkForInstanceToTp(player)) != null) {
                                    Utils.tpToWorld(gameToTp.getWorld(), player);
                                } else {
                                    if (AmazingTowers.getLobby() != null)
                                        Utils.tpToWorld(AmazingTowers.getLobby().getWorld(), player);
                                    else if (bungeecord)
                                        Utils.bungeecordTeleport(player);
                                    else
                                        player.kickPlayer(Utils.getColor(AmazingTowers.getGameInstance(player).getConfig(ConfigType.MESSAGES).getString("kickPlayersAtEndOfMatch")
                                                .replace("{Color}", teamColor.getColor())
                                                .replace("{Team}", teamColor.getName(gameInstance))
                                                .replace("%newLine%", "\n")));
                                }
                            }
                            Bukkit.unloadWorld(gameInstance.getInternalName(), false);
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
                    String topFiveKills = Utils.getColor(getTopFive(killsSorted, StatType.KILLS));
                    for (Player player : gameInstance.getGame().getPlayers()) {
                        player.sendMessage("\n");
                        player.sendMessage(topFiveKills);
                        if (!topFiveKills.contains(player.getName()) && stats.getPlayerStats().containsKey(player.getName())) {
                            String msg = Utils.getColor(getPosition(killsSorted, player.getName(), StatType.KILLS));
                            player.sendMessage(msg);
                        }
                    }
                } else if (Finish.this.seconds == 6) {
                    Comparator<Stats> byPoints = Comparator.comparingInt((Stats o) -> o.getStat(StatType.POINTS));
                    List<Map.Entry<String, Stats>> pointsSorted =
                            stats.getPlayerStats().entrySet().stream()
                                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(byPoints))).collect(Collectors.toList());
                    String topFivePoints = Utils.getColor(getTopFive(pointsSorted, StatType.POINTS));
                    for (Player player : gameInstance.getGame().getPlayers()) {
                        player.sendMessage("\n");
                        player.sendMessage(topFivePoints);
                        if (!topFivePoints.contains(player.getName()) && stats.getPlayerStats().containsKey(player.getName())) {
                            String msg = Utils.getColor(getPosition(pointsSorted, player.getName(), StatType.POINTS));
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
                    AmazingTowers.getGameInstance(player).getVault().giveReward(player, RewardsEnum.WIN);
                } else if (gameInstance.getGame().getTeams().getTeamByPlayer(player.getName()) != null)
                    AmazingTowers.getGameInstance(player).getVault().giveReward(player, RewardsEnum.LOSER_TEAM);
            }
        }
        if (AmazingTowers.isConnectedToDatabase() && gameInstance.getTableName() != null) {
            Bukkit.getScheduler().runTaskAsynchronously(AmazingTowers.getPlugin(), () -> {
                final HashMap<String, Stats> statsMap = stats.getPlayerStats();
                final String tableName = gameInstance.getTableName();
                statsMap.forEach((pl, st) -> AmazingTowers.connexion.updateData(pl, st, tableName));
            });
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
            String Title = Utils.getColor(messages.getString("win.titles.winTitle")
                    .replace("{Color}", teamColor.getColor())
                    .replace("{Team}", teamColor.getName(gameInstance).toUpperCase()));
            String Subtitle = Utils.getColor(messages.getString("win.titles.winSubTitle"));
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
        Iterator<Map.Entry<String, Stats>> listIterator = list.iterator();
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
        Iterator<Map.Entry<String, Stats>> listIterator = list.iterator();
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

    public void endMatch() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(name);
        List<ITeam> winningTeams = gameInstance.getGame().getTeams().getWinningTeams();
        if (winningTeams.size() == 1)
            this.fatality(winningTeams.get(0).getTeamColor());
        else {
            this.fatality(winningTeams.get(new Random().nextInt(winningTeams.size())).getTeamColor());
        }
    }

    public void endMatchOrGoldenGoal() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(name);
        List<ITeam> winningTeams = gameInstance.getGame().getTeams().getWinningTeams();
        if (winningTeams.size() == 1)
            this.fatality(winningTeams.get(0).getTeamColor());
        else {
            gameInstance.getGame().setGoldenGoal(true);
            gameInstance.getGame().getTeams().getTeams().forEach(o -> {
                if (winningTeams.contains(o)) {
                    if (gameInstance.getRules().get(Rule.BEDWARS_STYLE)) {
                        o.setPoints(0);
                        String title = Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("scorePoint.title.noRespawnTitle"));
                        for (Player pl : o.getOnlinePlayers()) {
                            pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 1.f);
                            if (gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("scorePoint.title.enabled"))
                                ReflectionMethods.sendTitle(pl, title, "", 0, 50, 20);
                            else
                                pl.sendMessage(title);
                        }
                    } else {
                        String title = Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("goldenGoal.titles.title"));
                        String subTitle = Utils.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("goldenGoal.titles.subTitle"));
                        for (Player pl : o.getOnlinePlayers()) {
                            pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 1.f);
                            if (gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("goldenGoal.titles.enabled"))
                                ReflectionMethods.sendTitle(pl, title, subTitle, 0, 50, 20);
                            else {
                                pl.sendMessage(title);
                                pl.sendMessage(subTitle);
                            }
                        }
                    }
                } else {
                    o.eliminateTeam();
                }
            });
            gameInstance.getWorld().spawnEntity(Locations.getLocationFromString(gameInstance
                    .getConfig(ConfigType.LOCATIONS).getString(Location.LOBBY.getPath())), EntityType.ENDER_DRAGON);
        }
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}


