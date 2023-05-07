package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class TimeListener implements Listener {
    private final AmazingTowers plugin;

    public TimeListener(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTime(WeatherChangeEvent e) {
        if (this.plugin.getGameInstance(e.getWorld()).getConfig(ConfigType.CONFIG).getBoolean("Options.weather_cancel") &&
                e.toWeatherState())
            e.setCancelled(true);
    }
}


