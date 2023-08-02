package mx.towers.pato14.game.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Book {
    private ItemStack book;
    private final Game game;

    public Book(Game game) {
        this.game = game;
        createBookItem();
    }

    public void createBookItem() {
        this.book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta metaBook = (BookMeta) this.book.getItemMeta();
        metaBook.setDisplayName(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.BOOK).getString("book.title")));
        int i = 0;
        HashMap<Integer, String> pages = new HashMap<>();
        Set<String> str = this.game.getGameInstance().getConfig(ConfigType.BOOK).getConfigurationSection("book.pages").getKeys(false);
        for (String s : str) {
            try {
                if (i < Integer.parseInt(s)) i = Integer.parseInt(s);
                pages.put(Integer.valueOf(s), getStringPage(this.game.getGameInstance().getConfig(ConfigType.BOOK).getStringList("book.pages." + s)));
            } catch (NumberFormatException e) {
                game.getGameInstance().getPlugin().sendConsoleMessage("§cThe page " + s + " isn't a numeric identifier");
            }
        }
        for (int j = 0; j < i; j = j + 1) {
            metaBook.addPage(pages.get(j + 1));
        }
        this.book.setItemMeta(metaBook);
    }

    public String getStringPage(List<String> lines) {
        StringBuilder end = new StringBuilder();
        int ai = Math.min(14, lines.size());
        for (int i = 0; i < ai; i++) {
            String st = lines.get(i);
            end.append(AmazingTowers.getColor(st)).append("\n");
        }
        return end.toString();
    }

    public ItemStack getItem() {
        return this.book;
    }
}


