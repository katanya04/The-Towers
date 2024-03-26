package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        TowersWorldInstance instance = AmazingTowers.getInstance(e.getWorld());
        if (instance == null)
            return;
        if (instance.getConfig(ConfigType.CONFIG).getBoolean("options.cancelWeatherUpdates") && e.toWeatherState())
            e.setCancelled(true);
    }
}


