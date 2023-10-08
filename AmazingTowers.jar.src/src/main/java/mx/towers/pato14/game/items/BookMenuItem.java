package mx.towers.pato14.game.items;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class BookMenuItem extends MenuItem { //Item that opens a book that contains a part of a config file or other data that can be modified by the player
    protected final ConfigType configType;
    protected final TowersWorldInstance instance;
    protected final String path;
    protected ItemStack book;
    protected int mapKeysAsListDeepLevel;
    protected int mapKeysAsListDeepLevelCopy;
    private final static TextComponent[] modifyText = new TextComponent[]{new TextComponent("Click to modify")};
    private final static TextComponent[] removeText = new TextComponent[]{new TextComponent("Click to remove")};
    private final static TextComponent[] addText = new TextComponent[]{new TextComponent("Click to add an entry")};
    private final Map<Integer, ItemStack> auxiliarContents;

    public BookMenuItem(ItemStack icon, ConfigType configType, TowersWorldInstance instance, String path) {
        this(icon, configType, instance, path, 0);
    }

    public BookMenuItem(ItemStack icon, ConfigType configType, TowersWorldInstance instance, String path, int mapKeysAsListDeepLevel) {
        this(icon, configType, instance, path, mapKeysAsListDeepLevel, new HashMap<>());
    }

    public BookMenuItem(ItemStack icon, ConfigType configType, TowersWorldInstance instance, String path, int mapKeysAsListDeepLevel, Map<Integer, ItemStack> auxiliarContents) {
        super(icon);
        this.configType = configType;
        this.instance = instance;
        this.path = path;
        this.book = new ItemStack(Material.WRITTEN_BOOK);
        this.mapKeysAsListDeepLevel = mapKeysAsListDeepLevel;
        this.mapKeysAsListDeepLevelCopy = mapKeysAsListDeepLevel;
        this.auxiliarContents = auxiliarContents;
    }

    @Override
    public void openMenu(HumanEntity player) {
        this.book = getBook();
        ReflectionMethods.openBook((Player) player, book);
        this.mapKeysAsListDeepLevel = this.mapKeysAsListDeepLevelCopy;
    }

    private static String getColorOfValue(String value) {
        if (value.equalsIgnoreCase("true"))
            return "§a";
        if (value.equalsIgnoreCase("false"))
            return "§c";
        if (Utils.isInteger(value))
            return "§6";
        if (Utils.isDouble(value))
            return "§3";
        else
            return "§8";
    }

    public String getFullPath() {
        return Utils.macroCaseToCamelCase(configType.name()) + ";" + path;
    }

    public String getFullPath(ConfigurationSection configurationSection) {
        return Utils.macroCaseToCamelCase(configType.name()) + ";" + configurationSection.getCurrentPath();
    }

    public void updateSettings(GameInstance gameInstance, String path) {
        Utils.addGlint(gameInstance.getHotbarItems().getModifyGameSettings().getSaveSettings());
        gameInstance.getHotbarItems().getModifyGameSettings().updateMenu();
    }

    protected boolean mapKeysAsList() {
        return mapKeysAsListDeepLevel-- > 0;
    }

    protected int getValueMaxLength() {
        return 15;
    }

    protected ItemStack getBook() {
        Object text = instance.getConfig(configType).get(path);
        if (text instanceof ConfigurationSection)
            return ReflectionMethods.getBook(getMap((ConfigurationSection) text));
        else if (text instanceof List)
            return ReflectionMethods.getBook(getCollection((List<?>) text, path));
        else
            return ReflectionMethods.getBook(Collections.singletonList(new TextComponent(String.valueOf(text))));
    }

    private List<TextComponent> getMap(ConfigurationSection configText) {
        List<TextComponent> toret = new ArrayList<>();
        boolean mapKeysAsList = mapKeysAsList();
        for (Map.Entry<String, ?> entry : configText.getValues(false).entrySet()) {
            TextComponent key = new TextComponent((mapKeysAsList ? "- " : "") + "§o" + entry.getKey() + "§r§0: ");
            toret.add(key);
            if (mapKeysAsList) {
                key.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + this.getFullPath(configText) + "." + entry.getKey() + " remove"));
                key.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, removeText));
            }
            if (entry.getValue() == null)
                continue;
            if (entry.getValue() instanceof List) {
                toret.add(new TextComponent("\n"));
                toret.addAll(getCollection((List<?>) entry.getValue(), this.getFullPath(configText) + "." + entry.getKey()));
            } else if (entry.getValue() instanceof ConfigurationSection) {
                toret.add(new TextComponent("\n"));
                toret.addAll(getMap((ConfigurationSection) entry.getValue()));
            } else {
                String color = getColorOfValue(String.valueOf(entry.getValue()));
                String valueText = entry.getValue().toString().length() > getValueMaxLength() ?
                        entry.getValue().toString().substring(0, Integer.max(0, getValueMaxLength() - 3)) + "§o..." :
                        entry.getValue().toString();
                TextComponent value = new TextComponent(color + valueText + "\n");
                value.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + this.getFullPath(configText) + "." + entry.getKey()));
                value.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, modifyText));
                toret.add(value);
            }
        }
        if (mapKeysAsList) {
            TextComponent addEntry = new TextComponent("- §2[+]§r§0\n");
            addEntry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + this.getFullPath(configText) + " add"));
            addEntry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, addText));
            toret.add(addEntry);
        }
        return toret;
    }

    private List<TextComponent> getCollection(Collection<?> listText, String path) {
        List<TextComponent> toret = new ArrayList<>();
        if (listText.isEmpty()) {
            TextComponent emptyList = new TextComponent("- <empty>\n");
            toret.add(emptyList);
        } else {
            for (Object entry : listText) {
                TextComponent entryStart = new TextComponent("- ");
                toret.add(entryStart);
                if (entry instanceof List) {
                    toret.add(new TextComponent("\n"));
                    toret.addAll(getCollection((List<?>) entry, path)); // ...
                } else if (entry instanceof ConfigurationSection) {
                    toret.add(new TextComponent("\n"));
                    toret.addAll(getMap((ConfigurationSection) entry));
                } else {
                    String string = String.valueOf(entry);
                    TextComponent entryText = new TextComponent(string + "\n");
                    entryText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + path + ";" + string + " remove"));
                    entryText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, removeText));
                    toret.add(entryText);
                }
            }
        }
        TextComponent addEntry = new TextComponent("- §2[+]§r§0\n");
        addEntry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + path + " add"));
        addEntry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, addText));
        toret.add(addEntry);
        return toret;
    }

    public void modifyConfigString(Player player, String path) {
        givePlayerAuxItems(player);
        ReflectionMethods.openAnvilInventory(player, path);
    }

    private void givePlayerAuxItems(Player player) {
        for (Map.Entry<Integer, ItemStack> item : auxiliarContents.entrySet()) {
            player.getInventory().setItem(item.getKey(), item.getValue());
        }
    }

    public Map<Integer, ItemStack> getAuxiliarContents() {
        return auxiliarContents;
    }
    public void removeAuxiliarItemsFromPlayer(HumanEntity player) {
        this.getAuxiliarContents().keySet().forEach(o -> player.getInventory().setItem(o, null));
    }
}