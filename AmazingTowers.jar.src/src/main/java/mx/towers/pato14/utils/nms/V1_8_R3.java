
package mx.towers.pato14.utils.nms;

import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import mx.towers.pato14.utils.plugin.PluginA;
import org.bukkit.Bukkit;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.entity.Player;

public class V1_8_R3 implements NMS
{
    @Override
    public void sendTitle(final Player player, final String Title, final String Subtitle, final int entrada, final int mantener, final int salida) {
        final IChatBaseComponent CTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + Title + "\"}");
        final IChatBaseComponent CSubtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + Subtitle + "\"}");
        final PacketPlayOutTitle stitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CTitle);
        final PacketPlayOutTitle sSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CSubtitle);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)stitle);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)sSubtitle);
        this.ticks(player, entrada, mantener, salida);
    }

    private void ticks(final Player player, final int entrada, final int mantener, final int salida) {
        final PacketPlayOutTitle times = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent)null, entrada, mantener, salida);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)times);
    }
}
