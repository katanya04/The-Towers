package mx.towers.pato14.utils.items;

import org.bukkit.inventory.ItemStack;

public class Skulls {
    public static ItemStack getSkullFromURL(String url) {
        return me.katanya04.anotherguiplugin.utils.Skulls.getSkullFromURL(url);
    }
    public static ItemStack getSkullFromBase64(String base64EncodedString) {
        return me.katanya04.anotherguiplugin.utils.Skulls.getSkullFromBase64(base64EncodedString);
    }

    public static ItemStack getPlayerHead(String playerName) {
        return me.katanya04.anotherguiplugin.utils.Skulls.getPlayerHead(playerName);
    }

    public static void cachePlayerHead(String playerName) {
        getPlayerHead(playerName);
    }

    public static String getPlayerByHead(ItemStack head) {
        return me.katanya04.anotherguiplugin.utils.Skulls.getPlayerByHead(head);
    }
}