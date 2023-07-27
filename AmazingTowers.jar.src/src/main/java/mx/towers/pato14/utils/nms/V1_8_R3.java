
package mx.towers.pato14.utils.nms;

import net.minecraft.server.v1_8_R3.*;
import mx.towers.pato14.utils.plugin.PluginA;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class V1_8_R3 implements NMS
{
    @Override
    public void sendTitle(final Player player, final String Title, final String Subtitle, final int entrada, final int mantener, final int salida) {
        final IChatBaseComponent CTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + Title + "\"}");
        final IChatBaseComponent CSubtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + Subtitle + "\"}");
        final PacketPlayOutTitle stitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CTitle);
        final PacketPlayOutTitle sSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CSubtitle);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(stitle);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(sSubtitle);
        this.ticks(player, entrada, mantener, salida);
    }

    private void ticks(final Player player, final int entrada, final int mantener, final int salida) {
        final PacketPlayOutTitle times = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, entrada, mantener, salida);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(times);
    }

    public String serializeItemStack(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack netItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = new NBTTagCompound();
        netItemStack.save(tag);
        return tag.toString();
    }

    public ItemStack deserializeItemStack(String rawItem) throws MojangsonParseException {
        if (rawItem == null || rawItem.equals("empty"))
            return null;
        NBTTagCompound compound = MojangsonParser.parse(rawItem);
        net.minecraft.server.v1_8_R3.ItemStack netItemStack = net.minecraft.server.v1_8_R3.ItemStack.createStack(compound);
        return CraftItemStack.asBukkitCopy(netItemStack);
    }
}
