package mx.towers.pato14.utils.nms;

import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.utils.exceptions.ParseItemException;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NMS {
    void sendTitle(final Player p0, final String p1, final String p2, final int p3, final int p4, final int p5);
    String serializeItemStack(ItemStack itemStack);
    ItemStack deserializeItemStack(String rawItem) throws ParseItemException;
    void openBook(Player p, ItemStack book);
    ItemStack getBook(List<TextComponent> pageTextComponents);
    void openAnvilInventory(final Player player, final String path);
}


