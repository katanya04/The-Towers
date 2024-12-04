package mx.towers.pato14.game.gameevents;

import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SetPotionEvents {
    private static final int RUN_EVERY_N_SECONDS = 4;
    public static void setPotionEvents(Game game) {
        clearPotionEvents(game);
        ConfigurationSection config = game.getGameInstance().getConfig(ConfigType.GAME_SETTINGS).getConfigurationSection("effects.effects");
        if (config == null)
            return;
        List<Map<String, String>> potions;
        potions = config.getValues(false).values().stream().map(o -> ((MemorySection) o).getValues(false)
                        .entrySet().stream().filter(e -> e.getValue() instanceof String)
                        .collect(Collectors.toMap(Map.Entry::getKey, v -> ((String) v.getValue()))))
                .collect(Collectors.toList());
        Set<PotionEvent> newPotionEvents = new HashSet<>();
        for (Map<String, String> map : potions) {
            System.out.println(map);
            PotionEffectType effectType = Utils.getPotionEffect(map.get("effectType"));
            if (effectType == null)
                continue;
            int level = Utils.parseIntOrDefault(map.get("level"), 0);
            if (!Utils.isStringTime(map.get("after").split(":")))
                continue;
            long after = Utils.stringTimeToInt(map.get("after").split(":"));
            if (game.getGameState().matchIsBeingPlayed)
                after -= game.getStart().getSecondsSinceStart();
            PotionEffect potion = new PotionEffect(effectType, RUN_EVERY_N_SECONDS + 1, level, false);
            newPotionEvents.add(new PotionEvent(after * 20, new BukkitRunnable() {
                @Override
                public void run() {
                    game.getGameInstance().getWorld().getPlayers().forEach(o -> o.addPotionEffect(potion));
                }
            }, 20 * RUN_EVERY_N_SECONDS));
        }
        game.getEvents().addAll(newPotionEvents);
        if (game.getGameState().matchIsBeingPlayed)
            game.startEvents();
    }
    public static void clearPotionEvents(Game game) {
        game.getEvents().stream().filter(o -> o instanceof PotionEvent).forEach(GameEvent::stop);
        game.getEvents().removeIf(o -> o instanceof PotionEvent);
    }
}
