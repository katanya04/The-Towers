package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

public class Timer {
    private final Game game;
    private boolean activated;
    private int time;
    private BossBar bossBar;
    public Timer(Game game) {
        this.game = game;
        this.activated = getActivated();
        this.time = getTime();
    }
    private boolean getActivated() {
        return Boolean.parseBoolean(game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.activated"));
    }

    public int getTime() {
        return Utils.stringTimeToInt(game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.time").split(":"));
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isActivated() {
        return activated;
    }

    public void timerStart() {
        TextComponent title = new TextComponent(game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.message").replace("%time%", game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.time")));
        title.setColor(ChatColor.LIGHT_PURPLE);
        for (Player player : game.getPlayers()) {
            bossBar = BossBarAPI.addBar(
                    player,
                    title,
                    BossBarAPI.Color.PURPLE, // Unused
                    BossBarAPI.Style.PROGRESS, // Unused
                    1.0f, // Progress (0.0 - 1.0)
                    time, // Timeout
                    2L // Timeout-interval Unused
            );
            //bossBar.setProperty(BossBarAPI.Property.CREATE_FOG, false);
            //bossBar.setProperty(BossBarAPI.Property.DARKEN_SKY, false);
            //bossBar.setProperty(BossBarAPI.Property.PLAY_MUSIC, false);
        }
        (new BukkitRunnable() {
            public void run() {
                if (game.getGameInstance().getGame().getGameState().equals(GameState.FINISH)) {
                    cancel();
                    return;
                }
                time--;
                bossBar.setMessage(ChatColor.LIGHT_PURPLE + game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getString("timer.message").replace("%time%", Utils.intTimeToString(time)));
                bossBar.setProgress((float) time /getTime());
            }
        }).runTaskTimerAsynchronously(AmazingTowers.getPlugin(), 20L, 20L);
    }
}
