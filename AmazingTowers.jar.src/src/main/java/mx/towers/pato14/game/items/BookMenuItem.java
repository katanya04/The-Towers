package mx.towers.pato14.game.items;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
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
    private final static TextComponent[] modifyText = new TextComponent[]{new TextComponent("Click to modify")};
    private final static TextComponent[] removeText = new TextComponent[]{new TextComponent("Click to remove")};
    private final static TextComponent[] addText = new TextComponent[]{new TextComponent("Click to add an entry")};

    public BookMenuItem(ItemStack icon, ConfigType configType, TowersWorldInstance instance, String path) {
        super(icon);
        this.configType = configType;
        this.instance = instance;
        this.path = path;
        this.book = new ItemStack(Material.WRITTEN_BOOK);
    }

    @Override
    public void openMenu(HumanEntity player) {
        this.book = getBook();
        instance.getPlugin().getNms().openBook((Player) player, book);
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

    public void updateSettings(GameInstance gameInstance) {
        Utils.addGlint(gameInstance.getHotbarItems().getModifyGameSettings().getSaveSettings());
        gameInstance.getHotbarItems().getModifyGameSettings().updateMenu();
    }

    protected boolean mapKeysAsList() {
        return false;
    }

    protected ItemStack getBook() {
        Object text = instance.getConfig(configType).get(path);
        if (text instanceof ConfigurationSection)
            return AmazingTowers.getPlugin().getNms().getBook(getMap((ConfigurationSection) text));
        else if (text instanceof List)
            return AmazingTowers.getPlugin().getNms().getBook(getCollection((List<?>) text, path));
        else
            return AmazingTowers.getPlugin().getNms().getBook(Collections.singletonList(new TextComponent(String.valueOf(text))));
    }

    private List<TextComponent> getMap(ConfigurationSection configText) {
        List<TextComponent> toret = new ArrayList<>();
        if (mapKeysAsList()) {
            toret.add(new TextComponent(path.split("\\.")[path.split("\\.").length - 1] + ":"));
            toret.addAll(getCollection(configText.getKeys(false), this.getFullPath(configText)));
        }
        else {
            for (Map.Entry<String, ?> entry : configText.getValues(true).entrySet()) {
                TextComponent key = new TextComponent("§o" + entry.getKey() + "§r§0: ");
                toret.add(key);
                if (entry.getValue() == null)
                    continue;
                if (entry.getValue() instanceof List) {
                    toret.addAll(getCollection((List<?>) entry.getValue(), this.getFullPath(configText) + "." + entry.getKey()));
                } else if (entry.getValue() instanceof ConfigurationSection) {
                    toret.addAll(getMap((ConfigurationSection) entry.getValue()));
                } else {
                    String color = getColorOfValue(String.valueOf(entry.getValue()));
                    TextComponent value = new TextComponent(color + entry.getValue() + "\n");
                    value.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + this.getFullPath(configText) + "." + entry.getKey()));
                    value.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, modifyText));
                    toret.add(value);
                }
            }
        }
        return toret;
    }

    private List<TextComponent> getCollection(Collection<?> listText, String path) {
        List<TextComponent> toret = new ArrayList<>();
        if (listText.isEmpty()) {
            TextComponent emptyList = new TextComponent("\n- <empty>");
            toret.add(emptyList);
        } else {
            for (Object entry : listText) {
                TextComponent entryStart = new TextComponent("\n- ");
                toret.add(entryStart);
                if (entry instanceof List) {
                    toret.addAll(getCollection((List<?>) entry, path)); // ...
                } else if (entry instanceof ConfigurationSection) {
                    toret.addAll(getMap((ConfigurationSection) entry));
                } else {
                    String string = String.valueOf(entry);
                    TextComponent entryText = new TextComponent(string);
                        entryText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + path + ";" + string + " remove"));
                        entryText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, removeText));
                    toret.add(entryText);
                }
            }
        }
        TextComponent addEntry = new TextComponent("\n- §2[+]§r§0");
        addEntry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + path + " add"));
        addEntry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, addText));
        toret.add(addEntry);
        return toret;
    }
}