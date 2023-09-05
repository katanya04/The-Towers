package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class GetStringFromItems extends ActionItem {
    public enum SlotType {ARMOR, HAND, FIXED, SKIP}
    private final SlotType slot;
    private final List<Integer> slots;
    public GetStringFromItems(ItemStack itemStack, SlotType slotType, List<Integer> slots) {
        super(itemStack);
        this.slot = slotType;
        this.slots = slots;
    }
    public GetStringFromItems(ItemStack itemStack, SlotType slotType) {
        this(itemStack, slotType, new ArrayList<>());
    }
    public String getItems(HumanEntity player) {
        StringBuilder stringBuilder = new StringBuilder();
        ItemStack[] items = new ItemStack[0];
        switch (slot) {
            case ARMOR:
                items = player.getInventory().getArmorContents();
                break;
            case HAND:
                items = new ItemStack[]{player.getInventory().getItemInHand()};
                break;
            case FIXED:
                items = slots != null && !slots.isEmpty() ? slots.stream().map(o -> player.getInventory().getItem(o)).toArray(ItemStack[]::new)
                        : new ItemStack[]{new ItemStack(Material.AIR)};
                break;
            case SKIP:
                return "<skip_item>";
        }
        for (ItemStack item : items) {
            stringBuilder.append(ReflectionMethods.serializeItemStack(item));
            stringBuilder.append(";");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(";"));
        return stringBuilder.toString();
    }

    public static Map<Integer, ItemStack> getArmorAndHotbar() {
        Map<Integer, ItemStack> toret = new HashMap<>();
        toret.put(21, new GetStringFromItems(Utils.setName(new ItemStack(Material.LEATHER_CHESTPLATE), "§r§lSet to player's current armor"), SlotType.ARMOR));
        toret.put(22, new GetStringFromItems(Utils.setName(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "§r§lClick on any item to set it as that"), SlotType.SKIP));
        toret.put(23, new GetStringFromItems(Utils.setName(new ItemStack(Material.IRON_SWORD), "§r§lSet to player's current row above hotbar"), SlotType.FIXED, Arrays.stream(new Integer[]{27, 28, 29, 30, 31, 32, 33, 34, 35}).collect(Collectors.toList())));
        return toret;
    }
}
