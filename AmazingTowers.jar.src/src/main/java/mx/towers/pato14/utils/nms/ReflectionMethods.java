
package mx.towers.pato14.utils.nms;

import mx.towers.pato14.utils.Utils;
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
        } catch (ClassNotFoundException ex) {
            Utils.reportException("NMS class not found \"" + name + "\"", ex);
            return null;
        }
    }

    private static Class<?> getBukkitClass(String name, String packet) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + packet + "." + name);
        } catch (ClassNotFoundException ex) {
            Utils.reportException("Bukkit class not found \"" + name + "\"", ex);
            return null;
        }
    }

    private static void setField(Object change, String name, Object to) {
        try {
            Field field = change.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(change, to);
            field.setAccessible(false);
        } catch (Exception e) {
            Utils.reportException("Error while changing the value of the field " + name + " in " + change.getClass(), e);
        }
    }

    private static void sendPacketFn(Player player, Object packet) {
        try {
            sendPacket.invoke(playerConnection.get(getHandle.invoke(CraftPlayer.cast(player))), packet);
        } catch (Exception e) {
            Utils.reportException("Error while sending packet " + packet.getClass().getCanonicalName(), e);
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
            Utils.reportException("Error while initializing reflection methods", ex);
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
            Utils.reportException("Exception while sending title to player", ex);
        }
    }

    static public Map<String, String> tabTeam = new HashMap<>();

    public static void setTabStyle(String playerName, String prefix, String suffix, int priority, Collection<? extends Player> receivers) {
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
            setField(packet, "g", Collections.singletonList(playerName));
            if (receivers != null)
                for (Player p : receivers)
                    sendPacketFn(p, packet);
            tabTeam.put(playerName, teamName);
        } catch (Exception e) {
            Utils.reportException("Exception while setting player name in tablist", e);
        }
    }

    public static void clearTabStyle(String playerName, Collection<? extends Player> receivers) {
        if (!tabTeam.containsKey(playerName))
            tabTeam.put(playerName, "nothing");
        String teamName = tabTeam.get(playerName);
        try {
            Object packet = PacketPlayOutScoreboardTeamConstructor.newInstance();
            setField(packet, "a", teamName);
            setField(packet, "b", teamName);
            setField(packet, "e", "ALWAYS");
            setField(packet, "h", 1);
            setField(packet, "g", Collections.singletonList(playerName));
            if (receivers != null)
                for (Player p : receivers)
                    sendPacketFn(p, packet);
            tabTeam.put(playerName, teamName);
        } catch (Exception e) {
            Utils.reportException("Exception while resetting player name in tablist", e);
        }
    }
}