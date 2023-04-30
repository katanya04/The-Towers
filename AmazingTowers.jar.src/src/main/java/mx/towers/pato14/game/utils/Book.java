package mx.towers.pato14.game.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Book {
    private AmazingTowers plugin;
    private ItemStack book;

    public Book(AmazingTowers plugin) {
        this.plugin = plugin;
        createBookItem();
    }

    public void createBookItem() {
        this.book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta metaBook = (BookMeta) this.book.getItemMeta();
        metaBook.setDisplayName(this.plugin.getColor(this.plugin.getBook().getString("book.title")));
        Integer i = Integer.valueOf(0);
        HashMap<Integer, String> pages = new HashMap<>();
        Set<String> str = this.plugin.getBook().getConfigurationSection("book.pages").getKeys(false);
        for (String s : str) {
            try {
                if (i.intValue() < Integer.valueOf(s).intValue()) i = Integer.valueOf(s);
                pages.put(Integer.valueOf(s), getStringPage(this.plugin.getBook().getStringList("book.pages." + s)));
            } catch (NumberFormatException e) {
                Bukkit.getConsoleSender().sendMessage("§cThe page " + s + " don't are a numeric identifier");
            }
        }
        for (Integer j = Integer.valueOf(0); j.intValue() < i.intValue(); j = Integer.valueOf(j.intValue() + 1)) {
            metaBook.addPage(new String[]{pages.get(Integer.valueOf(j.intValue() + 1))});
        }
        this.book.setItemMeta((ItemMeta) metaBook);
    }

    public String getStringPage(List<String> lines) {
        String end = "";
        Integer ai = Integer.valueOf((14 <= lines.size()) ? 14 : lines.size());
        for (int i = 0; i < ai.intValue(); i++) {
            String st = lines.get(i);
            end = String.valueOf(end) + this.plugin.getColor(st) + "\n";
        }
        return end;
    }

    public ItemStack getItem() {
        return this.book;
    }
}


