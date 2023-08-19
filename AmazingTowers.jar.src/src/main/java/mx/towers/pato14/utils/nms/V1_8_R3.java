
package mx.towers.pato14.utils.nms;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.exceptions.ParseItemException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
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
import java.util.Map;

import static mx.towers.pato14.game.items.BookMenuItem.getColorOfValue;

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

    @Override
    public String serializeItemStack(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack netItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = new NBTTagCompound();
        netItemStack.save(tag);
        return tag.toString();
    }

    @Override
    public ItemStack deserializeItemStack(String rawItem) throws ParseItemException {
        if (rawItem == null || rawItem.equals("empty"))
            return null;
        NBTTagCompound compound = null;
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
    public ItemStack getBook(BookMenuItem bookItem, Map<String, Object> configText) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        List<IChatBaseComponent> pages;
        try {
            pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(meta);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String color;
        TextComponent[] modifyText = new TextComponent[]{new TextComponent("Click to modify")};
        TextComponent[] removeText = new TextComponent[]{new TextComponent("Click to remove")};
        TextComponent[] addText = new TextComponent[]{new TextComponent("Click to add an entry")};
        List<TextComponent> pageTextComponents = new ArrayList<>();
        for (Map.Entry<String, Object> value : configText.entrySet()) {
            if (value.getValue() instanceof String) {
                color = getColorOfValue((String) value.getValue());
                TextComponent keyAndValue = new TextComponent("§o" + value.getKey() + "§r§0: " + color + value.getValue() + "\n");
                keyAndValue.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + bookItem.getFullPath() + "." + value.getKey()));
                keyAndValue.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, modifyText));
                pageTextComponents.add(keyAndValue);
            } else if (value.getValue() instanceof List) {
                TextComponent key = new TextComponent(value.getKey() + ":\n");
                List<TextComponent> entries = new ArrayList<>();
                if (value.getValue() != null) {
                    for (String s : (List<String>) value.getValue()) {
                        if (s.equalsIgnoreCase("<empty>") && ((List<?>) value.getValue()).size() > 1)
                            continue;
                        TextComponent entry = new TextComponent("- " + s + "\n");
                        if (!s.equalsIgnoreCase("<empty>")) {
                            entry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + bookItem.getFullPath() + "." + value.getKey() + ";" + s + " remove"));
                            entry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, removeText));
                        }
                        entries.add(entry);
                    }
                }
                TextComponent entry = new TextComponent("- §2[+]\n§r§0");
                entry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt modifySetting " + bookItem.getFullPath() + "." + value.getKey() + " add"));
                entry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, addText));
                pageTextComponents.add(key);
                pageTextComponents.addAll(entries);
                pageTextComponents.add(entry);
            }
        }

        IChatBaseComponent page = IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(pageTextComponents.toArray(new TextComponent[0])));

        pages.add(page);
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
        Object name = AmazingTowers.getPlugin().getGameInstance(player).getConfig(ConfigType.valueOf(Utils.camelCaseToMacroCase(pathSplit[0]))).get(pathSplit[1]);
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