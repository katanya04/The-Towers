
package mx.towers.pato14.utils.nms;

import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectionMethods {
    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException var3) {
            Utils.sendConsoleMessage("NMS class not found \"" + name + "\"", MessageType.ERROR);
            return null;
        }
    }

    private static Class<?> getBukkitClass(String name, String packet) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + packet + "." + name);
        } catch (ClassNotFoundException var3) {
            Utils.sendConsoleMessage("Bukkit class not found \"" + name + "\"", MessageType.ERROR);
            return null;
        }
    }

    private static Method a;
    private static Class<?> PacketPlayOutTitle;
    private static Class<?> EnumTitleAction;
    private static Class<?> IChatBaseComponent;
    private static Constructor<?> PacketPlayOutTitleConstructor;
    private static Class<?> CraftPlayer;
    private static Method getHandle;
    private static Class<?> EntityPlayer;
    private static Field playerConnection;
    private static Method sendPacket;
    private static Constructor<?> PacketPlayOutTitleConstructor2;
    private static Class<?> PacketPlayOutScoreboardTeam;
    private static Constructor<?> PacketPlayOutScoreboardTeamConstructor;

    static {
        try {
            a = getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class);
            PacketPlayOutTitle = getNMSClass("PacketPlayOutTitle");
            EnumTitleAction = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            IChatBaseComponent = getNMSClass("IChatBaseComponent");
            PacketPlayOutTitleConstructor = PacketPlayOutTitle.getConstructor(EnumTitleAction, IChatBaseComponent);
            CraftPlayer = getBukkitClass("CraftPlayer", "entity");
            getHandle = CraftPlayer.getDeclaredMethod("getHandle");
            EntityPlayer = getNMSClass("EntityPlayer");
            playerConnection = EntityPlayer.getField("playerConnection");
            sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
            PacketPlayOutTitleConstructor2 = PacketPlayOutTitle.getConstructor(EnumTitleAction, IChatBaseComponent, int.class, int.class, int.class);
            PacketPlayOutScoreboardTeam = getNMSClass("PacketPlayOutScoreboardTeam");
            PacketPlayOutScoreboardTeamConstructor = PacketPlayOutScoreboardTeam.getConstructor();
        } catch (Exception ex) {
            Utils.sendConsoleMessage("Reflection error: " + ex.getClass().getSimpleName(), MessageType.ERROR);
        }
    }

    private static void setField(Object change, String name, Object to) {
        try {
            Field field = change.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(change, to);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendPacketFn(Player player, Object packet) {
        try {
            sendPacket.invoke(playerConnection.get(getHandle.invoke(CraftPlayer.cast(player))), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendTitle(final Player player, final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        try {
            Object CTitle = a.invoke(null, "{\"text\": \"" + title + "\"}");
            Object CSubtitle = a.invoke(null, "{\"text\": \"" + subtitle + "\"}");

            Object sTitle = PacketPlayOutTitleConstructor.newInstance(Objects.requireNonNull(EnumTitleAction).getEnumConstants()[0], CTitle);
            Object sSubtitle = PacketPlayOutTitleConstructor.newInstance(EnumTitleAction.getEnumConstants()[1], CSubtitle);

            sendPacketFn(player, sTitle);
            sendPacketFn(player, sSubtitle);

            Object timesPacket = PacketPlayOutTitleConstructor2.newInstance(EnumTitleAction.getEnumConstants()[2], null, fadeIn, stay, fadeOut);
            sendPacketFn(player, timesPacket);
        } catch (Exception ex) {
            Utils.sendConsoleMessage("Exception while sending title to player", MessageType.ERROR);
        }
    }

    static public Map<UUID, String> tabTeam = new HashMap<>();

    public static void setTabStyle(Player player, String prefix, String suffix, int priority, Collection<? extends Player> receivers) {
        if (prefix == null)
            prefix = "";
        if (suffix == null)
            suffix = "";
        try {
            String teamName = priority + UUID.randomUUID().toString();
            if (teamName.length() > 16)
                teamName = teamName.substring(0, 16);
            Object packet = PacketPlayOutScoreboardTeamConstructor.newInstance();
            setField(packet, "a", teamName);
            setField(packet, "b", teamName);
            setField(packet, "c", prefix);
            setField(packet, "d", suffix);
            setField(packet, "e", "ALWAYS");
            setField(packet, "h", 0);
            setField(packet, "g", Collections.singletonList(player.getName()));
            for (Player p : receivers)
                sendPacketFn(p, packet);
            tabTeam.put(player.getUniqueId(), teamName);
        } catch (Exception e) {
            Utils.sendConsoleMessage("Exception while setting player name in tablist", MessageType.ERROR);
        }
    }

    public static void clearTabStyle(Player player, Collection<? extends Player> receivers) {
        if (!tabTeam.containsKey(player.getUniqueId()))
            tabTeam.put(player.getUniqueId(), "nothing");
        String teamName = tabTeam.get(player.getUniqueId());
        try {
            Object packet = PacketPlayOutScoreboardTeamConstructor.newInstance();
            setField(packet, "a", teamName);
            setField(packet, "b", teamName);
            setField(packet, "e", "ALWAYS");
            setField(packet, "h", 1);
            setField(packet, "g", Collections.singletonList(player.getName()));
            for (Player p : receivers)
                sendPacketFn(p, packet);
            tabTeam.put(player.getUniqueId(), teamName);
        } catch (Exception e) {
            Utils.sendConsoleMessage("Exception while resetting player name in tablist", MessageType.ERROR);
        }
    }
}