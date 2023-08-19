package mx.towers.pato14.utils.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Rule {
    GRIEF(new ItemStack(Material.DIAMOND_PICKAXE)),
    PROTECT_POINT(new ItemStack(Material.BARRIER)),
    EMERALD(new ItemStack(Material.EMERALD)),
    REDSTONE(new ItemStack(Material.REDSTONE)),
    COAL(new ItemStack(Material.COAL)),
    LAPISLAZULI(new ItemStack(Material.INK_SACK, 1, (short) 4)),
    BALANCED_TEAMS(new ItemStack(Material.WOOL, 1, (short) 14)),
    BOW(new ItemStack(Material.BOW)),
    IRON_ARMOR(new ItemStack(Material.IRON_CHESTPLATE)),
    WATER(new ItemStack(Material.WATER_BUCKET)),
    POTS_AND_APPLE(new ItemStack(Material.POTION)),
    ENDER_PEARL(new ItemStack(Material.ENDER_PEARL)),
    TNT(new ItemStack(Material.TNT)),
    ENCHANTS(new ItemStack(Material.ENCHANTED_BOOK)),
    KITS(new ItemStack(Material.IRON_SWORD)),
    BEDWARS_STYLE(new ItemStack(Material.BED));
    private final ItemStack icon;
    Rule(ItemStack icon) {
        this.icon = icon;
    }
    public ItemStack getIcon() {
        return icon;
    }
}