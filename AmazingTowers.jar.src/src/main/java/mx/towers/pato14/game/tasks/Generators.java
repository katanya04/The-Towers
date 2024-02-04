package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.exceptions.ParseItemException;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Generators {
    private final String worldName;
    private List<Map<String, String>> generators;
    public Generators(String worldName) {
        this.worldName = worldName;
    }

    public void startGenerators() {
        GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
        Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
        String path = Location.GENERATOR.getPath();
        this.generators = locations.getList(path) == null ? new ArrayList<>() :
                locations.getMapList(path).stream().map(o -> o.entrySet().stream().collect(
                        Collectors.toMap(p -> p.getKey().toString(), q -> q.getValue().toString()))).collect(Collectors.toList());
        (new BukkitRunnable() {
            public void run() {
                GameInstance gameInstance = AmazingTowers.getGameInstance(worldName);
                if (gameInstance.getGame().getGameState().equals(GameState.FINISH) ||
                        !Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("generators.activated"))) {
                    cancel();
                    return;
                }
                try {
                    for (Map<String, String> item : generators) {
                        gameInstance.getWorld().dropItemNaturally(Locations.getLocationFromString(item.get("coords")),
                                ReflectionMethods.deserializeItemStack(item.get("item")));
                    }
                } catch (ParseItemException exception) {
                    Utils.sendConsoleMessage("§cError while parsing the generator items!", MessageType.ERROR);
                    cancel();
                }
            }
        }).runTaskTimer(AmazingTowers.getPlugin(), 0L,
                (Integer.parseInt(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString("generators.waitTimeSeconds")) * 20L));
    }
}
