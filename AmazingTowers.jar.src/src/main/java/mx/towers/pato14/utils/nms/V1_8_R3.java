
package mx.towers.pato14.utils.nms;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.exceptions.ParseItemException;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class V1_8_R3 implements NMS
{
    @Override
    public void sendTitle(final Player player, final String Title, final String Subtitle, final int entrada, final int mantener, final int salida) {
        final IChatBaseComponent CTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + Title + "\"}");
        final IChatBaseComponent CSubtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + Subtitle + "\"}");
        final PacketPlayOutTitle sTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CTitle);
        final PacketPlayOutTitle sSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CSubtitle);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(sTitle);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(sSubtitle);
        this.ticks(player, entrada, mantener, salida);
    }

    private void ticks(final Player player, final int entrada, final int mantener, final int salida) {
        final PacketPlayOutTitle times = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, entrada, mantener, salida);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(times);
    }

    @Override
    public String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return "empty";
        net.minecraft.server.v1_8_R3.ItemStack netItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = new NBTTagCompound();
        netItemStack.save(tag);
        return tag.toString();
    }

    @Override
    public ItemStack deserializeItemStack(String rawItem) throws ParseItemException {
        if (rawItem == null || rawItem.equals("empty"))
            return null;
        NBTTagCompound compound;
        try {
            compound = MojangsonParser.parse(rawItem);
        } catch (MojangsonParseException e) {
            throw new ParseItemException();
        }
        net.minecraft.server.v1_8_R3.ItemStack netItemStack = net.minecraft.server.v1_8_R3.ItemStack.createStack(compound);
        return CraftItemStack.asBukkitCopy(netItemStack);
    }

    @Override
    public void openBook(Player p, ItemStack book) { //thx to Juancomaster1998 :)
        p.closeInventory();
        net.minecraft.server.v1_8_R3.ItemStack NMSBook = CraftItemStack.asNMSCopy(book);
        EntityHuman player = ((CraftHumanEntity)p).getHandle();
        org.bukkit.inventory.ItemStack hand = p.getItemInHand();
        try {
            p.setItemInHand(CraftItemStack.asBukkitCopy(NMSBook));
            player.openBook(NMSBook);
        } catch(Exception ex) {
            AmazingTowers.getPlugin().sendConsoleMessage("Error while trying to open book menu", MessageType.ERROR);
        } finally {
            p.setItemInHand(hand);
        }
    }

    @Override
    public ItemStack getBook(List<TextComponent> pageTextComponents) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        List<IChatBaseComponent> pages;
        try {
            pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(meta);
        } catch (Exception e) {
            AmazingTowers.getPlugin().sendConsoleMessage("Error while trying to create a book menu item", MessageType.ERROR);
            return book;
        }
        List<List<TextComponent>> lines = Utils.getLines(pageTextComponents);
        List<TextComponent> page = new ArrayList<>();
        int index = 0;
        for (List<TextComponent> line : lines) {
            if (index++ < 14)
                page.addAll(line);
            else {
                pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(page.toArray(new TextComponent[0]))));
                page = new ArrayList<>(line);
                index = 0;
            }
        }
        if (!page.isEmpty())
            pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(page.toArray(new TextComponent[0]))));
        book.setItemMeta(meta);
        return book;
    }

    @Override
    public void openAnvilInventory(final Player player, final String path) {
        String[] pathSplit = path.split(";");

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        FakeAnvil fakeAnvil = new FakeAnvil(entityPlayer);
        int containerId = entityPlayer.nextContainerCounter();

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage("Repairing"), 0));

        entityPlayer.activeContainer = fakeAnvil;
        entityPlayer.activeContainer.windowId = containerId;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
        entityPlayer.activeContainer = fakeAnvil;
        entityPlayer.activeContainer.windowId = containerId;

        Inventory inv = fakeAnvil.getBukkitView().getTopInventory();
        Object name = AmazingTowers.getGameInstance(player).getConfig(ConfigType.valueOf(Utils.camelCaseToMacroCase(pathSplit[0]))).get(pathSplit[1]);
        inv.setItem(0, Utils.setLore(Utils.setName(new ItemStack(Material.PAPER), name instanceof String ? (String) name : "<entry>"),
                "§r§8" + path));
    }

    public static final class FakeAnvil extends ContainerAnvil {
        public FakeAnvil(EntityHuman entityHuman) {
            super(entityHuman.inventory, entityHuman.world, new BlockPosition(0,0,0), entityHuman);
        }
        @Override
        public boolean a(EntityHuman entityHuman) {
            return true;
        }
    }
}