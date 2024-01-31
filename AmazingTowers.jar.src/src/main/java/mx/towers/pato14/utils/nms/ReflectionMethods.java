
package mx.towers.pato14.utils.nms;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.exceptions.ParseItemException;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public static void sendTitle(final Player player, final String Title, final String Subtitle, final int entrada, final int mantener, final int salida) {
        try {
            Method a = Objects.requireNonNull(getNMSClass("IChatBaseComponent$ChatSerializer")).getMethod("a", String.class);
            Object CTitle = a.invoke(null, "{\"text\": \"" + Title + "\"}");
            Object CSubtitle = a.invoke(null, "{\"text\": \"" + Subtitle + "\"}");

            Class<?> PacketPlayOutTitle = getNMSClass("PacketPlayOutTitle");
            Class<?> EnumTitleAction = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            Constructor<?> PacketPlayOutTitleConstructor = Objects.requireNonNull(PacketPlayOutTitle).getConstructor(EnumTitleAction, getNMSClass("IChatBaseComponent"));
            Object sTitle = PacketPlayOutTitleConstructor.newInstance(Objects.requireNonNull(EnumTitleAction).getEnumConstants()[0], CTitle);
            Object sSubtitle = PacketPlayOutTitleConstructor.newInstance(EnumTitleAction.getEnumConstants()[1], CSubtitle);

            Class<?> CraftPlayer = getBukkitClass("CraftPlayer", "entity");
            assert CraftPlayer != null;
            Method getHandle = CraftPlayer.getDeclaredMethod("getHandle");
            Class<?> EntityPlayer = getNMSClass("EntityPlayer");
            assert EntityPlayer != null;
            Field playerConnection = EntityPlayer.getField("playerConnection");
            Method sendPacket = Objects.requireNonNull(getNMSClass("PlayerConnection")).getMethod("sendPacket", getNMSClass("Packet"));
            sendPacket.invoke(playerConnection.get(getHandle.invoke((CraftPlayer.cast(player)))), sTitle);
            sendPacket.invoke(playerConnection.get(getHandle.invoke((CraftPlayer.cast(player)))), sSubtitle);

            Constructor<?> PacketPlayOutTitleConstructor2 = PacketPlayOutTitle.getConstructor(EnumTitleAction, getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object timesPacket = PacketPlayOutTitleConstructor2.newInstance(EnumTitleAction.getEnumConstants()[2], null, entrada, mantener, salida);
            sendPacket.invoke(playerConnection.get(getHandle.invoke((CraftPlayer.cast(player)))), timesPacket);
        } catch (Exception ex) {
            Utils.sendConsoleMessage("Exception while sending title to player", MessageType.ERROR);
        }
    }

    public static String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return "empty";
        try {
            Class<?> NetItemStack = getNMSClass("ItemStack");
            Class<?> CraftItemStack = getBukkitClass("CraftItemStack", "inventory");
            assert CraftItemStack != null;
            Method asNMSCopy = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
            Object netItemStack = asNMSCopy.invoke(null, itemStack);

            Object tag = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getConstructor().newInstance();
            assert NetItemStack != null;
            NetItemStack.getMethod("save", getNMSClass("NBTTagCompound")).invoke(netItemStack, tag);
            return tag.toString();
        } catch (Exception ex) {
            Utils.sendConsoleMessage("Reflection exception when serializing an item", MessageType.ERROR);
        }
        return null;
    }

    public static ItemStack deserializeItemStack(String rawItem) throws ParseItemException {
        if (rawItem == null || rawItem.equals("empty"))
            return null;
        try {
            Class<?> NBTTagCompound = getNMSClass("NBTTagCompound");
            Class<?> MojangsonParser = getNMSClass("MojangsonParser");
            Class<?> NetItemStack = getNMSClass("ItemStack");

            assert MojangsonParser != null;
            Object compound = MojangsonParser.getMethod("parse", String.class).invoke(null, rawItem);
            assert NetItemStack != null;
            Object netItemStack = NetItemStack.getMethod("createStack", NBTTagCompound).invoke(null, compound);

            Class<?> CraftItemStack = getBukkitClass("CraftItemStack", "inventory");
            assert CraftItemStack != null;
            return (ItemStack) CraftItemStack.getMethod("asBukkitCopy", NetItemStack).invoke(null, netItemStack);
        } catch (Exception e) {
            throw new ParseItemException();
        }
    }

    public static void openBook(Player p, ItemStack book) { //thx to Juancomaster1998 :)
        p.closeInventory();
        ItemStack hand = p.getItemInHand();
        try {
            Class<?> CraftItemStack = getBukkitClass("CraftItemStack", "inventory");
            Class<?> NetItemStack = getNMSClass("ItemStack");
            assert CraftItemStack != null;
            Object NMSBook = CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, book);
            p.setItemInHand((ItemStack) CraftItemStack.getMethod("asBukkitCopy", NetItemStack).invoke(null, NMSBook));

            Class<?> CraftPlayer = getBukkitClass("CraftPlayer", "entity");
            Class<?> EntityPlayer = getNMSClass("EntityPlayer");
            assert CraftPlayer != null;
            Method getHandle = CraftPlayer.getDeclaredMethod("getHandle");
            assert EntityPlayer != null;
            Method openBook = EntityPlayer.getMethod("openBook", NetItemStack);
            openBook.invoke(getHandle.invoke(CraftPlayer.cast(p)), NMSBook);
        } catch (Exception ex) {
            Utils.sendConsoleMessage("Error while trying to open book menu", MessageType.ERROR);
        } finally {
            p.setItemInHand(hand);
        }
    }

    @SuppressWarnings("unchecked")
    public static ItemStack getBook(List<TextComponent> pageTextComponents) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        List<List<TextComponent>> lines = Utils.getLines(pageTextComponents);
        List<TextComponent> page = new ArrayList<>();
        int index = 0;
        try {
            List<Object> pages = (List<Object>) Objects.requireNonNull(getBukkitClass("CraftMetaBook", "inventory")).getDeclaredField("pages").get(meta);

            Method a = Objects.requireNonNull(getNMSClass("IChatBaseComponent$ChatSerializer")).getMethod("a", String.class);

            for (List<TextComponent> line : lines) {
                if (index++ < 14)
                    page.addAll(line);
                else {
                    pages.add(a.invoke(null, ComponentSerializer.toString(page.toArray(new TextComponent[0]))));
                    page = new ArrayList<>(line);
                    index = 0;
                }
            }

            if (!page.isEmpty())
                pages.add(a.invoke(null, ComponentSerializer.toString(page.toArray(new TextComponent[0]))));
        } catch (Exception ex) {
            Utils.sendConsoleMessage("Error while trying to create a book menu", MessageType.ERROR);
        }

        book.setItemMeta(meta);
        return book;
    }

    public static void openAnvilInventory(final Player player, final String path) {
        String[] pathSplit = path.split(";");
        try {
            Class<?> CraftPlayer = getBukkitClass("CraftPlayer", "entity");
            Class<?> EntityPlayer = getNMSClass("EntityPlayer");
            Class<?> EntityHuman = getNMSClass("EntityHuman");
            assert CraftPlayer != null;
            Method getHandle = CraftPlayer.getDeclaredMethod("getHandle");
            Object entityPlayer = getHandle.invoke(CraftPlayer.cast(player));

            Class<?> ContainerAnvil = getNMSClass("ContainerAnvil");
            assert ContainerAnvil != null;
            assert EntityHuman != null;
            Object fakeAnvil = ContainerAnvil.getConstructor(getNMSClass("PlayerInventory"),
                    getNMSClass("World"), getNMSClass("BlockPosition"), EntityHuman)
                    .newInstance(EntityHuman.getField("inventory").get(entityPlayer), EntityHuman.getField("world").get(entityPlayer),
                            Objects.requireNonNull(getNMSClass("BlockPosition")).getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0),
                            entityPlayer);
            ContainerAnvil.getField("checkReachable").setBoolean(fakeAnvil, false);

            assert EntityPlayer != null;
            Field playerConnection = EntityPlayer.getField("playerConnection");
            Class<?> PacketPlayOutOpenWindow = getNMSClass("PacketPlayOutOpenWindow");
            Method sendPacket = Objects.requireNonNull(getNMSClass("PlayerConnection")).getMethod("sendPacket", getNMSClass("Packet"));
            int containerId = (int) EntityPlayer.getMethod("nextContainerCounter").invoke(entityPlayer);
            assert PacketPlayOutOpenWindow != null;
            sendPacket.invoke(playerConnection.get(getHandle.invoke((CraftPlayer.cast(player)))),
                    PacketPlayOutOpenWindow.getConstructor(int.class, String.class, getNMSClass("IChatBaseComponent"), int.class)
                            .newInstance(containerId, "minecraft:anvil", Objects.requireNonNull(getNMSClass("IChatBaseComponent$ChatSerializer")).getMethod("a", String.class).invoke(null, "Repairing"), 0));

            Class<?> Container = getNMSClass("Container");
            Field activeContainer = EntityPlayer.getField("activeContainer");
            activeContainer.set(entityPlayer, fakeAnvil);
            assert Container != null;
            Field windowId =  Container.getField("windowId");
            windowId.setInt(activeContainer.get(entityPlayer), containerId);
            Container.getMethod("addSlotListener", getNMSClass("ICrafting")).invoke(activeContainer.get(entityPlayer), entityPlayer);
            activeContainer.set(entityPlayer, fakeAnvil);
            windowId.setInt(activeContainer.get(entityPlayer), containerId);

            Class<?> CraftInventoryView = getBukkitClass("CraftInventoryView", "inventory");
            assert CraftInventoryView != null;
            Inventory inv = (Inventory) CraftInventoryView.getMethod("getTopInventory").invoke(ContainerAnvil.getMethod("getBukkitView").invoke(fakeAnvil));
            Object name = AmazingTowers.getGameInstance(player).getConfig(ConfigType.valueOf(Utils.camelCaseToMacroCase(pathSplit[0]))).get(pathSplit[1]);
            inv.setItem(0, Utils.setLore(Utils.setName(new ItemStack(Material.PAPER), name instanceof String ? (String) name : "<entry>"),
                    "§r§8" + path));
        } catch (Exception ex) {
            Utils.sendConsoleMessage("Error while trying to open an anvil inventory", MessageType.ERROR);
        }
    }
}