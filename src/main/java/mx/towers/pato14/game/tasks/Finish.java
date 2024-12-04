package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import mx.towers.pato14.utils.rewards.RewardsEnum;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.stats.Rank;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.stats.StatisticsPlayer;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Finish {
    private int seconds;
    private final boolean bungeecord;
    private final String name;

    public Finish(GameInstance gameInstance) {
        this.name = gameInstance.getInternalName();
        seconds = gameInstance.getConfig(ConfigType.CONFIG).getInt("options.timerEndSeconds") + 1;
        bungeecord = AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled");
    }

    public void fatality(final TeamColor winnerTeamColor) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(name);
        gameInstance.getGame().setGameState(GameState.FINISH);
        StatisticsPlayer stats = gameInstance.getGame().getStats();
        stats.increaseOneAll(StatType.GAMES_PLAYED);
        stats.increaseOneConditional(StatType.WINS, player -> gameInstance.getGame().getTeams().getTeam(winnerTeamColor).containsPlayer(player));
        sendTitle(winnerTeamColor);
        gameInstance.getGame().stopEvents();
        List<Player> playersWithStats = gameInstance.getGame().getPlayers().stream()
                .filter(player -> stats.getPlayerStats().containsKey(player.getName())).collect(Collectors.toList());
        (new BukkitRunnable() {
            public void run() {
                switch (Finish.this.seconds) {
                    case 0:
                        cancel();
                        return;
                    case 1:
                        (new BukkitRunnable() {
                            public void run() {
                                GameInstance gameToTp;
                                for (Player player : gameInstance.getGame().getPlayers()) {
                                    if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.sendPlayerToAnotherInstanceAtTheEnd")
                                            && (gameToTp = AmazingTowers.checkForInstanceToTp(player)) != null) {
                                        Utils.tpToWorld(gameToTp.getWorld(), player);
                                    } else {
                                        if (AmazingTowers.getLobby() != null)
                                            Utils.tpToWorld(AmazingTowers.getLobby().getWorld(), player);
                                        else if (bungeecord)
                                            Utils.bungeecordTeleport(player);
                                        else
                                            player.kickPlayer(Utils.getColor(AmazingTowers.getGameInstance(player).getConfig(ConfigType.MESSAGES).getString("kickPlayersAtEndOfMatch")
                                                    .replace("{Color}", winnerTeamColor.getColor())
                                                    .replace("{Team}", winnerTeamColor.getName(gameInstance))
                                                    .replace("%newLine%", "\n")));
                                    }
                                }
                                Bukkit.unloadWorld(gameInstance.getInternalName(), false);
                                gameInstance.reset();
                            }
                        }).runTaskLater(AmazingTowers.getPlugin(), 60L);
                        if (gameInstance.getConfig(ConfigType.MESSAGES).getBoolean("serverRestart.enabled")) {
                            gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("serverRestart.message"), true);
                        }
                        break;
                    case 9:
                        LinkedHashMap<String, Stats> killsSorted = stats.getSorted(StatType.KILLS);
                        String topFiveKills = getTopText(killsSorted, StatType.KILLS);
                        gameInstance.broadcastMessage("\n" + topFiveKills, true);
                        playersWithStats.stream().filter(player -> !topFiveKills.contains(player.getName()))
                                .forEach(player -> player.sendMessage(Utils.getColor(getPositionText(killsSorted, player.getName(), StatType.KILLS))));
                        break;
                    case 6:
                        LinkedHashMap<String, Stats> pointsSorted = stats.getSorted(StatType.POINTS);
                        String topFivePoints = getTopText(pointsSorted, StatType.POINTS);
                        gameInstance.broadcastMessage("\n" + topFivePoints, true);
                        playersWithStats.stream().filter(player -> !topFivePoints.contains(player.getName()))
                                .forEach(player -> player.sendMessage(Utils.getColor(getPositionText(pointsSorted, player.getName(), StatType.POINTS))));
                        break;
                    case 4:
                        playersWithStats.forEach(player -> player.sendMessage("\n§lRango: "));
                        break;
                    case 3:
                        playersWithStats.forEach(player -> {
                                    Rank rank = Rank.getTotalRank(stats.getPlayerStats().get(player.getName()));
                                    player.sendMessage(rank.getColor() + "§l" + rank.name());
                                    player.playSound(player.getLocation(), rank.getSound(), 1.0F, rank.getPitch());
                                });
                        break;
                }
                playersWithStats.stream().filter(player -> gameInstance.getGame().getTeams().getTeam(winnerTeamColor)
                        .containsPlayer(player.getName()) && player.getGameMode() != GameMode.SPECTATOR)
                        .forEach(player -> fireworks(player, winnerTeamColor.getColorEnum()));
                Finish.this.seconds--;
            }
        }).runTaskTimer(AmazingTowers.getPlugin(), 0L, 20L);
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.rewards.vault") &&
                SetupVault.getVaultEconomy() != null) {
            for (Player player : gameInstance.getGame().getPlayers()) {
                if (gameInstance.getGame().getTeams().getTeam(winnerTeamColor).containsPlayer(player.getName())) {
                    AmazingTowers.getGameInstance(player).getVault().giveReward(player, RewardsEnum.WIN);
                } else if (gameInstance.getGame().getTeams().getTeamByPlayer(player.getName()) != null)
                    AmazingTowers.getGameInstance(player).getVault().giveReward(player, RewardsEnum.LOSER_TEAM);
            }
        }
        if (AmazingTowers.isConnectedToDatabase() && gameInstance.getTableName() != null) {
            Bukkit.getScheduler().runTaskAsynchronously(AmazingTowers.getPlugin(), () -> {
                final HashMap<String, Stats> statsMap = stats.getPlayerStats();
                final String tableName = gameInstance.getTableName();
                AmazingTowers.connexion.updateData(statsMap, Collections.singleton(tableName));
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

    private String getTopText(LinkedHashMap<String, Stats> sortedStats, StatType stat) {
        Game game = AmazingTowers.getGameInstance(name).getGame();
        Iterator<Map.Entry<String, Stats>> listIterator = sortedStats.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("&lTop ").append(stat.getText()).append("\n&r");
        for (int i = 0; i < 5 && listIterator.hasNext(); i++) {
            Map.Entry<String, Stats> current = listIterator.next();
            TeamColor team = game.getTeams().getTeamByPlayer(current.getKey()).getTeamColor();
            sb.append(team != null ? team.getColor() : TeamColor.SPECTATOR.getColor());
            sb.append((i + 1)).append(". ").append(current.getKey()).append(" - ").append(current.getValue().getStat(stat)).append("\n");
            sb.append("&r");
        }
        return sb.toString();
    }

    private String getPositionText(LinkedHashMap<String, Stats> sortedStats, String p, StatType stat) {
        Game game = AmazingTowers.getGameInstance(name).getGame();
        StringBuilder sb = new StringBuilder();
        int position = new ArrayList<>(sortedStats.keySet()).indexOf(p);
        int value = sortedStats.get(p).getStat(stat);
        TeamColor team = game.getTeams().getTeamByPlayer(p).getTeamColor();
        sb.append("Tu: ");
        sb.append(team != null ? team.getColor() : TeamColor.SPECTATOR.getColor());
        sb.append(position).append(". ").append(p).append(" - ").append(value).append("\n");
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
            gameInstance.getGame().setGameState(GameState.EXTRA_TIME);
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
        }
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}


