package mx.towers.pato14.game.kits;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.exceptions.ParseItemException;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Damageable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class Kits {
    private final List<Kit> kits = new ArrayList<>();
    private final HashMap<String, List<Kit>> temporalBoughtKits;
    private final String instanceName;

    public Kits(GameInstance gameInstance) {
        this.instanceName = gameInstance.getInternalName();
        ConfigurationSection kitsConfig = gameInstance.getConfig(ConfigType.KITS);
        for (String entry : kitsConfig.getConfigurationSection("Kits").getKeys(false)) {
            Object value = kitsConfig.get("Kits." + entry);
            if (!(value instanceof ConfigurationSection))
                continue;
            addKit((ConfigurationSection) value);
        }
        this.temporalBoughtKits = new HashMap<>();
    }

    private Kit getKitFromConfig(ConfigurationSection kit) {
        Kit toret = null;
        ItemStack[] armor = getItems(kit, "armor", 4);
        ItemStack[] hotbar = getItems(kit, "hotbar", 9);
        try {
            if (AmazingTowers.capitalismExists()) {
                toret = new Kit(kit.getName(), armor, hotbar, Integer.parseInt(kit.getString("price")),
                        Boolean.parseBoolean(kit.getString("permanent")), setIcon(ReflectionMethods.deserializeItemStack(kit.getString("iconInMenu")), true, kit));
            } else
                toret = new Kit(kit.getName(), armor, hotbar, setIcon(ReflectionMethods.deserializeItemStack(kit.getString("iconInMenu")), false, kit));
        } catch (ParseItemException e) {
            Utils.sendConsoleMessage("Error while parsing the icon item of the kit \"" + kit.get("name") + "\"", MessageType.ERROR);
        }
        return toret;
    }

    private void addKit(ConfigurationSection kitConfigSection) {
        Kit kit = getKitFromConfig(kitConfigSection);
        if (kit != null)
            kits.add(kit);
    }

    private static ItemStack setIcon(ItemStack item, boolean addLore, ConfigurationSection kit) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.getColor("§r&l" + kit.getName()));
        if (addLore) {
            List<String> lore = new ArrayList<>();
            lore.add(kit.getString("price") + " coins");
            lore.add(Boolean.parseBoolean(kit.getString("permanent")) ? "Usos ilimitados" : "Comprar 1 uso");
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack[] getItems(ConfigurationSection kit, String name, int size) {
        ItemStack[] toret = new ItemStack[size];
        String[] itemsArray = kit.getString(name).split(";");
        if (itemsArray.length == size) {
            for (int i = 0; i < size; i++) {
                try {
                    ItemStack item = ReflectionMethods.deserializeItemStack(itemsArray[i]);
                    if (item != null && item.getType().getMaxDurability() != 0) {
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.spigot().setUnbreakable(true);
                        item.setItemMeta(itemMeta);
                    }
                    toret[i] = item;
                } catch (ParseItemException e) {
                    Utils.sendConsoleMessage("Error while parsing " + name + " in the kit \"" + kit.get("name") + "\", position " + i, MessageType.ERROR);
                }
            }
        } else
            Utils.sendConsoleMessage("Error while parsing " + name + " in the kit \"" + kit.get("name") + "\", incorrect size", MessageType.ERROR);
        return toret;
    }

    public Kit[] getIcons() {
        return this.kits.stream().map(Kit::getIconInMenu).toArray(Kit[]::new);
    }

    public Kit getDefaultKit() {
        return this.kits.get(0);
    }

    public Kit get(ItemStack iconInMenu) {
        for (Kit kit : kits) {
            if (kit.getIconInMenu().equals(iconInMenu))
                return kit;
        }
        return null;
    }

    public boolean playerHasKit(String playerName, Kit kit) {
        if (kit.isPermanent()) {
            String kitsBought = this.kitsConfig().getString("Buyers." + playerName);
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
        String current = this.kitsConfig().getString("Buyers." + playerName);
        this.kitsConfig().set("Buyers." + playerName, current == null || current.isEmpty() ?
                kit.getName() : ";" + kit.getName());
        this.kitsConfig().saveConfig();

    }

    private Config kitsConfig() {
        return AmazingTowers.getGameInstance(this.instanceName).getConfig(ConfigType.KITS);
    }

    public void resetTemporalBoughtKits() {
        this.temporalBoughtKits.clear();
    }

    public void updateKits(String path) { // kits;Kits.Default remove or kits;Kits.Default.prize or kits;Kits.Joseile add
        if (path.split("\\.").length > 2)
            path = path.substring(0, path.lastIndexOf("."));
        String[] pathSpaces = path.split(" ");
        String[] pathSemiColon = pathSpaces[0].split(";");
        if (pathSpaces.length == 1 || pathSpaces[1].equalsIgnoreCase("remove")) {
            Kit kitToRemove = kits.stream().filter(o -> o.getName().equals(pathSemiColon[1].split("\\.")[1])).collect(Collectors.toList()).get(0);
            kits.remove(kitToRemove);
            AmazingTowers.getGameInstance(instanceName).getGame().getPlayersSelectedKit().entrySet().forEach(o -> {
                if (kitToRemove.equals(o.getValue()))
                    o.setValue(null);
            });
        }
        if (pathSpaces.length == 1 || pathSpaces[1].equalsIgnoreCase("add"))
            addKit(kitsConfig().getConfigurationSection(pathSemiColon[1]));
        AmazingTowers.getGameInstance(instanceName).getHotbarItems().getSelectKit().setContents(getIcons());
    }
}