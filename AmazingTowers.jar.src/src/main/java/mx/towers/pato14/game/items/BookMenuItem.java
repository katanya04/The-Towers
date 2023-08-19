package mx.towers.pato14.game.items;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class BookMenuItem extends MenuItem { //Item that opens a book that contains a part of a config file or other data that can be modified by the player
    private final ConfigType configType;
    private final GameInstance gameInstance;
    private final String path;
    private ItemStack book;
    public BookMenuItem(ItemStack icon, ConfigType configType, GameInstance gameInstance, String path) {
        super(icon);
        this.configType = configType;
        this.gameInstance = gameInstance;
        this.path = path;
        this.book = new ItemStack(Material.WRITTEN_BOOK);
    }
    @Override
    public void openMenu(HumanEntity player) {
        this.book = setBook();
        gameInstance.getPlugin().getNms().openBook((Player) player, book);
    }

    public static String getColorOfValue(String value) {
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

    public ItemStack setBook() {
        return gameInstance.getPlugin().getNms().getBook(this,
                gameInstance.getConfig(configType).getConfigurationSection(path).getValues(true));
    }

    public String getFullPath() {
        return Utils.macroCaseToCamelCase(configType.name()) + ";" + path;
    }

    public void updateSettings(GameInstance gameInstance) {
        Utils.addGlint(gameInstance.getGame().getLobbyItems().getModifyGameSettings().getSaveSettings());
        gameInstance.getGame().getLobbyItems().getModifyGameSettings().updateMenu();
    }
}