package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;
import org.inventivetalent.bossbar.EntityBossBar;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Timer {
    private boolean activated;
    private int time;
    private final Map<String, BossBar> bossBars;
    private BukkitRunnable timerTask;
    private final String name;
    private TextComponent title;
    public Timer(GameInstance gameInstance) {
        this.name = gameInstance.getName();
        this.activated = getActivated(gameInstance);
        this.time = getTime(gameInstance);
        this.bossBars = new HashMap<>();
    }
    private boolean getActivated(GameInstance gameInstance) {
        return Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("timer.activated"));
    }

    public int getTime(GameInstance gameInstance) {
        try {
            return Utils.stringTimeToInt(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("timer.time").split(":"));
        } catch (Exception ex) {
            gameInstance.getConfig(ConfigType.GAME_SETTINGS).set("timer.time", "30:00");
            AmazingTowers.getPlugin().sendConsoleMessage("Error while reading timer's time. Set to default value (30:00)", MessageType.ERROR);
        }
        return 1800;
    }

    public void update(GameInstance gameInstance) {
        this.activated = getActivated(gameInstance);
        this.time = getTime(gameInstance);
        if (gameInstance.getGame().getGameState() == GameState.GAME) {
            timerTask.cancel();
            removeBossBar();
            timerStart();
        }
    }

    private BossBar createBossBar(Player player) {
        BossBar toret  = BossBarAPI.addBar(player, this.title,
                BossBarAPI.Color.PURPLE, // Unused
                BossBarAPI.Style.PROGRESS, // Unused
                1.0f, // Progress (0.0 - 1.0)
                time, // Timeout
                2L // Timeout-interval Unused
        );
        toret.setProperty(BossBarAPI.Property.CREATE_FOG, false);
        toret.setProperty(BossBarAPI.Property.DARKEN_SKY, false);
        toret.setProperty(BossBarAPI.Property.PLAY_MUSIC, false);
        try {
            Field field = EntityBossBar.class.getDeclaredField("minHealth");
            field.setAccessible(true);
            field.setFloat(toret, -0.1F);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            AmazingTowers.getPlugin().sendConsoleMessage("Error while starting the timer", MessageType.ERROR);
        }
        return toret;
    }

    public void timerStart() {
        this.title = new TextComponent(AmazingTowers.getGameInstance(name).getConfig(ConfigType.GAME_SETTINGS)
                .getString("timer.message").replace("%time%", AmazingTowers.getGameInstance(name)
                        .getConfig(ConfigType.GAME_SETTINGS).getString("timer.time")));
        this.title.setColor(ChatColor.LIGHT_PURPLE);
        for (Player player : AmazingTowers.getGameInstance(name).getGame().getPlayers()) {
            bossBars.put(player.getName(), createBossBar(player));
        }
        (timerTask = new BukkitRunnable() {
            public void run() {
                if (!activated || AmazingTowers.getGameInstance(name).getGame().getGameState().equals(GameState.FINISH)) {
                    removeBossBar();
                    cancel();
                    return;
                }
                if (time <= 0) {
                    removeBossBar();
                    AmazingTowers.getGameInstance(name).getGame().getFinish().goldenGoal();
                    cancel();
                    return;
                }
                time--;
                for (BossBar bossBar : bossBars.values()) {
                    bossBar.setMessage(ChatColor.LIGHT_PURPLE + AmazingTowers.getGameInstance(name).getConfig(ConfigType.GAME_SETTINGS).getString("timer.message").replace("%time%", Utils.intTimeToString(time)));
                    bossBar.setProgress((float) time / getTime(AmazingTowers.getGameInstance(name)));
                }
            }
        }).runTaskTimer(AmazingTowers.getPlugin(), 20L, 20L);
    }

    private void removeBossBar() {
        for (Map.Entry<String, BossBar> bossBar : bossBars.entrySet()) {
            Player player = Bukkit.getPlayer(bossBar.getKey());
            if (player == null)
                bossBars.remove(bossBar.getKey());
            else
                bossBar.getValue().removePlayer(player);
        }
    }

    public void removeBossBar(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null)
            bossBars.get(playerName).removePlayer(player);
    }

    public void addPlayer(Player player) {
        bossBars.put(player.getName(), createBossBar(player));
    }

    public Map<String, BossBar> getBossBars() {
        return bossBars;
    }

    public void reset() {
        removeBossBar();
        for (BossBar bossBar : bossBars.values()) {
            bossBar.setProgress(-10);
        }
        bossBars.clear();
    }

    public boolean isActivated() {
        return activated;
    }
}
