package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.locations.Locations;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Generators {
    private final String worldName;
    private List<Map<String, Object>> generators;
    public Generators(String worldName) {
        this.worldName = worldName;
    }

    public void startGenerators() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
        String path = Location.GENERATOR.getPath();
        this.generators = locations.getList(path) == null ? new ArrayList<>() :
                locations.getMapList(path).stream().map(o -> o.entrySet().stream().collect(
                        Collectors.toMap(p -> p.getKey().toString(), q -> (Object) q.getValue()))).collect(Collectors.toList());
        (new BukkitRunnable() {
            public void run() {
                GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
                if (!gameInstance.getGame().getGameState().matchIsBeingPlayed ||
                        !Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("generators.activated"))) {
                    cancel();
                    return;
                }
                for (Map<String, Object> item : generators) {
                    gameInstance.getWorld().dropItemNaturally(Locations.getLocationFromString(item.get("coords").toString()),
                            Utils.getItemsFromObj(item.get("item"), 1)[0]);
                }
            }
        }).runTaskTimer(AmazingTowers.getPlugin(), 0L,
                (Integer.parseInt(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("generators.waitTimeSeconds")) * 20L));
    }
}
