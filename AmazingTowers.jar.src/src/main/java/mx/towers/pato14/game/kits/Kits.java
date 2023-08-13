package mx.towers.pato14.game.kits;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.MessageType;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Kits {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();
    private final Config kitsConfig;
    private final List<Kit> kits = new ArrayList<>();
    private final HashMap<String, List<Kit>> temporalBoughtKits;

    public Kits(Config kitsConfig, boolean capitalismExists) {
        this.kitsConfig = kitsConfig;
        for (Map<String, String> kit : kitsConfig.getMapList("Kits").stream().map(o -> (Map<String, String>) o).collect(Collectors.toList())) {
            ItemStack[] armor = getItems(kit, "armor", 4);
            ItemStack[] hotbar = getItems(kit, "hotbar", 9);
            try {
                if (capitalismExists) {
                    kits.add(new Kit(kit.get("name"), armor, hotbar, Integer.parseInt(kit.get("price")),
                            Boolean.parseBoolean(kit.get("permanent")), plugin.getNms().deserializeItemStack(kit.get("iconInMenu"))));
                } else {
                    kits.add(new Kit(kit.get("name"), armor, hotbar, plugin.getNms().deserializeItemStack(kit.get("iconInMenu"))));
                }
            } catch (MojangsonParseException e) {
                plugin.sendConsoleMessage("Error while parsing the icon item of the kit \"" + kit.get("name") + "\"", MessageType.ERROR);
            }
        }
        this.temporalBoughtKits = new HashMap<>();
    }

    private ItemStack[] getItems(Map<String, String> kit, String name, int size) {
        ItemStack[] toret = new ItemStack[size];
        String[] itemsArray = kit.get(name).split(";");
        if (itemsArray.length == size) {
            for (int i = 0; i < size; i++) {
                try {
                    toret[i] = plugin.getNms().deserializeItemStack(itemsArray[i]);
                } catch (MojangsonParseException e) {
                    plugin.sendConsoleMessage("Error while parsing " + name + " in the kit \"" + kit.get("name") + "\", position " + i, MessageType.ERROR);
                }
            }
        } else
            plugin.sendConsoleMessage("Error while parsing " + name + " in the kit \"" + kit.get("name") + "\", incorrect size", MessageType.ERROR);
        return toret;
    }

    public ItemStack[] getIcons() {
        return this.kits.stream().map(Kit::getIconInMenu).toArray(ItemStack[]::new);
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
            String kitsBought = this.kitsConfig.getString("Buyers." + playerName);
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
        String current = this.kitsConfig.getString("Buyers." + playerName);
        this.kitsConfig.set("Buyers." + playerName, current == null || current.isEmpty() ?
                kit.getName() : ";" + kit.getName());
        this.kitsConfig.saveConfig();

    }
}


