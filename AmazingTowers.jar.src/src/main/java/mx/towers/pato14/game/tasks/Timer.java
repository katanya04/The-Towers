package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;
import org.inventivetalent.bossbar.EntityBossBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Timer {
    private final Game game;
    private boolean activated;
    private int time;
    private BossBar bossBar;
    private BukkitRunnable timerTask;
    public Timer(Game game) {
        this.game = game;
        update();
    }
    private boolean getActivated() {
        return Boolean.parseBoolean(game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.activated"));
    }

    public int getTime() {
        try {
            return Utils.stringTimeToInt(game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.time").split(":"));
        } catch (Exception ex) {
            game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).set("timer.time", "30:00");
            AmazingTowers.getPlugin().sendConsoleMessage("Error while reading timer's time. Set to default value (30:00)", MessageType.ERROR);
        }
        return 1800;
    }

    public void update() {
        this.activated = getActivated();
        this.time = getTime();
        if (game.getGameState() == GameState.GAME) {
            timerTask.cancel();
            for (Player player : bossBar.getPlayers())
                bossBar.removePlayer(player);
            timerStart();
        }
    }

    public void timerStart() {
        TextComponent title = new TextComponent(game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.message").replace("%time%", game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.time")));
        title.setColor(ChatColor.LIGHT_PURPLE);
        for (Player player : game.getPlayers()) {
            bossBar = BossBarAPI.addBar(player, title,
                    BossBarAPI.Color.PURPLE, // Unused
                    BossBarAPI.Style.PROGRESS, // Unused
                    1.0f, // Progress (0.0 - 1.0)
                    time, // Timeout
                    2L // Timeout-interval Unused
            );
            bossBar.setProperty(BossBarAPI.Property.CREATE_FOG, false);
            bossBar.setProperty(BossBarAPI.Property.DARKEN_SKY, false);
            bossBar.setProperty(BossBarAPI.Property.PLAY_MUSIC, false);
            try {
                Field field = EntityBossBar.class.getDeclaredField("minHealth");
                field.setAccessible(true);
                field.setFloat(bossBar, -0.1F);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                AmazingTowers.getPlugin().sendConsoleMessage("Error while starting the timer", MessageType.ERROR);
            }
        }
        (timerTask = new BukkitRunnable() {
            public void run() {
                if (!activated || game.getGameInstance().getGame().getGameState().equals(GameState.FINISH)) {
                    removeBossBar();
                    cancel();
                    return;
                }
                if (time <= 0) {
                    removeBossBar();
                    game.getFinish().goldenGoal();
                    cancel();
                    return;
                }
                time--;
                bossBar.setMessage(ChatColor.LIGHT_PURPLE + game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.message").replace("%time%", Utils.intTimeToString(time)));
                bossBar.setProgress((float) time / getTime());
            }
        }).runTaskTimer(AmazingTowers.getPlugin(), 20L, 20L);
    }

    private void removeBossBar() {
        for (Player player : bossBar.getPlayers()) {
            bossBar.removePlayer(player);
        }
    }
}
