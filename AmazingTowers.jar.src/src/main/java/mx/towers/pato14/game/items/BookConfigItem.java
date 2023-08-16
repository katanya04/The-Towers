package mx.towers.pato14.game.items;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;
import java.util.Map;

public abstract class BookConfigItem extends MenuItem { //Item that opens a book that contains a part of a config file or other data that can be modified by the player
    private final ConfigType configType;
    private final GameInstance gameInstance;
    private final String path;
    private final ItemStack book;
    public BookConfigItem(ItemStack icon, ConfigType configType, GameInstance gameInstance, String path) {
        super(icon);
        this.configType = configType;
        this.gameInstance = gameInstance;
        this.path = path;
        this.book = getBook();
    }
    @Override
    public void openMenu(HumanEntity player) {
        gameInstance.getPlugin().getNms().open((Player) player, this);
    }
    private String getText() {
        StringBuilder toret = new StringBuilder();
        Map<String, Object> configText = gameInstance.getConfig(configType).getConfigurationSection(path).getValues(true);
        for (Map.Entry<String, Object> value : configText.entrySet()) {
            if (value.getValue() instanceof String) {
                toret.append(value.getKey()).append(": ").append(value.getValue());
            }
            else if (value.getValue() instanceof List) {
                toret.append(value.getKey()).append(": ");
                for (String s : (List<String>) value.getValue()) {
                    toret.append(s).append("\n");
                }
            }
        }
        return toret.toString();
    }

    public ItemStack getBook() {
        ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
        BookMeta metaBook = (BookMeta) book.getItemMeta();
        metaBook.addPage(getText());
        book.setItemMeta(metaBook);
        return book;
    }
}
