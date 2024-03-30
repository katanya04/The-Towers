package mx.towers.pato14.game.kits;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Kits {
    private static final HashMap<String, Kit> kits = new HashMap<>();

    static {
        setKits();
    }

    public static Set<String> getKitsNames() {
        return kits.keySet();
    }

    private static void setKits() {
        kits.clear();
        ConfigurationSection kitsConfig = AmazingTowers.getKitsDefine();
        for (String entry : kitsConfig.getConfigurationSection("Kits").getKeys(false)) {
            Object value = kitsConfig.get("Kits." + entry);
            if (!(value instanceof ConfigurationSection))
                continue;
            kits.put(entry, getKitFromConfig((ConfigurationSection) value));
        }
        if (kits.isEmpty())
            kits.put("Default", getKitFromConfig(
                    (ConfigurationSection) Config.getFromDefault("Default", "kitsDefine.yml")));
    }

    private static Kit getKitFromConfig(ConfigurationSection kit) {
        Kit toret;
        ItemStack[] armor = Utils.setUnbreakable(Utils.getItemsFromConf(kit, "armor", 4));
        ItemStack[] hotbar = Utils.setUnbreakable(Utils.getItemsFromConf(kit, "hotbar", 9));
        ItemStack iconInMenu = Utils.getItemsFromConf(kit, "iconInMenu", 1)[0];
        if (AmazingTowers.capitalismExists()) {
            toret = new Kit(kit.getName(), armor, hotbar, Utils.parseIntOrDefault(kit.getString("price"), 0),
                    Utils.parseBoolOrDefault(kit.getString("permanent"), true), setIcon(iconInMenu, true, kit));
        } else
            toret = new Kit(kit.getName(), armor, hotbar, setIcon(iconInMenu, false, kit));
        return toret;
    }

    private static ItemStack setIcon(ItemStack item, boolean addLore, ConfigurationSection kit) {
        Utils.setName(item, Utils.getColor("§r&l" + kit.getName()));
        if (addLore) {
            List<String> lore = new ArrayList<>();
            lore.add(kit.getString("price") + " coins");
            lore.add(Utils.parseBoolOrDefault(kit.getString("permanent"), true) ? "Usos ilimitados" : "Comprar 1 uso");
            Utils.setLore(item, lore);
        }
        return item;
    }

    private final String instanceName;
    private final List<Kit> kitsInThisInstance;
    private final HashMap<String, List<Kit>> temporalBoughtKits;

    public Kits(GameInstance gameInstance) {
        this.instanceName = gameInstance.getInternalName();
        this.kitsInThisInstance = new ArrayList<>();
        this.temporalBoughtKits = new HashMap<>();
        addKits(gameInstance);
    }

    private void addKits(GameInstance gameInstance) {
        this.kitsInThisInstance.clear();
        this.temporalBoughtKits.clear();
        List<String> kitsInInstance = gameInstance.getConfig(ConfigType.KITS).getStringList("KitsInThisInstance");
        kitsInThisInstance.addAll(kits.entrySet().stream().filter(o -> kitsInInstance.contains(o.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toList()));
        if (kitsInThisInstance.isEmpty())
            kitsInThisInstance.add(kits.values().iterator().next());
    }

    public ItemStack[] getKitIcons() {
        return this.kitsInThisInstance.stream().map(Kit::getIconInMenu).toArray(ItemStack[]::new);
    }

    public Kit getDefaultKit() {
        return this.kitsInThisInstance.get(0);
    }

    public static Kit getByIcon(ItemStack iconInMenu) {
        String name = iconInMenu.getItemMeta().getDisplayName();
        for (Kit kit : kits.values()) {
            if (kit.getIconInMenu().getItemMeta().getDisplayName().equals(name))
                return kit;
        }
        return null;
    }

    public boolean playerHasKit(String playerName, Kit kit) {
        if (kit.isPermanent()) {
            String kitsBought = AmazingTowers.getKitsDefine().getString("Buyers." + playerName);
            return kitsBought != null && !kitsBought.isEmpty() && kitsBought.contains(kit.getName());
        } else {
            return temporalBoughtKits.get(playerName) != null && temporalBoughtKits.get(playerName).contains(kit);
        }
    }

    public void addTemporalBoughtKitToPlayer(String playerName, Kit kit) {
        List<Kit> kits = temporalBoughtKits.get(playerName);
        if (kits == null)
            kits = new ArrayList<>();
        kits.add(kit);
        temporalBoughtKits.put(playerName, kits);
    }

    public void addKitToPlayer(String playerName, Kit kit) {
        String current = AmazingTowers.getKitsDefine().getString("Buyers." + playerName);
        AmazingTowers.getKitsDefine().set("Buyers." + playerName, current == null || current.isEmpty() ?
                kit.getName() : ";" + kit.getName());
        AmazingTowers.getKitsDefine().saveConfig();

    }

    public void resetTemporalBoughtKits() {
        temporalBoughtKits.clear();
    }

    public static void updateGlobalKits() {
        setKits();
        Arrays.stream(AmazingTowers.getGameInstances()).forEach(o -> o.getGame().getKits().updateKits());
    }

    public void updateKits() {
        addKits(AmazingTowers.getGameInstance(this.instanceName));
        updateGameKits(AmazingTowers.getGameInstance(this.instanceName));
    }

    public void updateGameKits(GameInstance gameInstance) {
        for (Map.Entry<HumanEntity, Kit> entry : gameInstance.getGame().getPlayersSelectedKit().entrySet()) {
            gameInstance.getGame().getPlayersSelectedKit().put(entry.getKey(),
                    kits.containsKey(entry.getValue().getName()) ?
                            kits.get(entry.getValue().getName()) : getDefaultKit());
        }
    }
}