package mx.towers.pato14.utils.nms;

import net.minecraft.server.v1_8_R3.MojangsonParseException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {
    void sendTitle(final Player p0, final String p1, final String p2, final int p3, final int p4, final int p5);
    String serializeItemStack(ItemStack itemStack);
    public ItemStack deserializeItemStack(String rawItem) throws MojangsonParseException;
}


