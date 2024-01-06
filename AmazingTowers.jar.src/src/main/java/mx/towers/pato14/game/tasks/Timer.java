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
    private final String instanceName;
    public Timer(GameInstance gameInstance) {
        this.instanceName = gameInstance.getInternalName();
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
            Utils.sendConsoleMessage("Error while reading timer's time. Set to default value (30:00)", MessageType.ERROR);
        }
        return 1800;
    }

    public void update(GameInstance gameInstance) {
        this.activated = getActivated(gameInstance);
        this.time = getTime(gameInstance);
        if (gameInstance.getGame().getGameState() == GameState.GAME) {
            removeAllBossBars();
            if (this.activated)
                timerStart();
        }
    }

    private BossBar createBossBar(Player player) {
        BossBar toret  = BossBarAPI.addBar(player, new TextComponent(),
                BossBarAPI.Color.PURPLE, // Unused
                BossBarAPI.Style.PROGRESS, // Unused
                1.0f, // Progress (0.0 - 1.0)
                time, // Timeout
                2L // Timeout-interval Unused
        );
        updateTimer(toret);
        toret.setProperty(BossBarAPI.Property.CREATE_FOG, false);
        toret.setProperty(BossBarAPI.Property.DARKEN_SKY, false);
        toret.setProperty(BossBarAPI.Property.PLAY_MUSIC, false);
        try {
            Field field = EntityBossBar.class.getDeclaredField("minHealth");
            field.setAccessible(true);
            field.setFloat(toret, -0.1F);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Utils.sendConsoleMessage("Error while starting the timer", MessageType.ERROR);
        }

        return toret;
    }

    private void updateTimer(BossBar bossBar) {
        bossBar.setMessage(ChatColor.LIGHT_PURPLE + AmazingTowers.getGameInstance(instanceName).getConfig(ConfigType.GAME_SETTINGS).getString("timer.message").replace("%time%", Utils.intTimeToString(time)));
        bossBar.setProgress((float) time / getTime(AmazingTowers.getGameInstance(instanceName)));
    }
    private void updateAllTimers() {
        for (BossBar bossBar : bossBars.values())
            updateTimer(bossBar);
    }

    public void timerStart() {
        for (Player player : AmazingTowers.getGameInstance(instanceName).getGame().getPlayers()) {
            bossBars.put(player.getName(), createBossBar(player));
        }
        (timerTask = new BukkitRunnable() {
            public void run() {
                if (!activated || AmazingTowers.getGameInstance(instanceName).getGame().getGameState().equals(GameState.FINISH)) {
                    removeAllBossBars();
                    return;
                }
                if (time <= 0) {
                    removeAllBossBars();
                    AmazingTowers.getGameInstance(instanceName).getGame().getFinish().endMatchOrGoldenGoal();
                    return;
                }
                time--;
                updateAllTimers();
            }
        }).runTaskTimer(AmazingTowers.getPlugin(), 20L, 20L);
    }

    public void removeAllBossBars() {
        for (Map.Entry<String, BossBar> bossBar : bossBars.entrySet()) {
            Player player = Bukkit.getPlayer(bossBar.getKey());
            if (player != null) {
                bossBar.getValue().removePlayer(player);
                bossBar.getValue().setProgress(-10);
            }
        }
        bossBars.clear();
        if (timerTask != null)
            try { timerTask.cancel();} catch (IllegalStateException ignored) {}
    }

    public void removeBossBar(Player player) {
        if (player == null)
            return;
        if (bossBars.get(player.getName()) != null) {
            bossBars.get(player.getName()).removePlayer(player);
            bossBars.get(player.getName()).setProgress(-10);
        }
        bossBars.remove(player.getName());
    }

    public void addPlayer(Player player) {
        bossBars.put(player.getName(), createBossBar(player));
    }

    public Map<String, BossBar> getBossBars() {
        return bossBars;
    }

    public boolean isActivated() {
        return activated;
    }
}
