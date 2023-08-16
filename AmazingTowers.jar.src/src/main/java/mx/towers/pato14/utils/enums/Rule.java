package mx.towers.pato14.utils.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Rule {
    GRIEF(false, new ItemStack(Material.DIAMOND_PICKAXE)),
    PROTECT_POINT(true, new ItemStack(Material.BARRIER)),
    EMERALD(true, new ItemStack(Material.EMERALD)),
    REDSTONE(true, new ItemStack(Material.REDSTONE)),
    COAL(true, new ItemStack(Material.COAL)),
    LAPISLAZULI(true, new ItemStack(Material.INK_SACK, 1, (short) 4)),
    BALANCED_TEAMS(true, new ItemStack(Material.WOOL, 1, (short) 14)),
    BOW(true, new ItemStack(Material.BOW)),
    IRON_ARMOR(true, new ItemStack(Material.IRON_CHESTPLATE)),
    WATER(true, new ItemStack(Material.WATER_BUCKET)),
    POTS_AND_APPLE(true, new ItemStack(Material.POTION)),
    ENDER_PEARL(true, new ItemStack(Material.ENDER_PEARL)),
    TNT(true, new ItemStack(Material.TNT)),
    ENCHANTS(true, new ItemStack(Material.ENCHANTED_BOOK)),
    KITS(true, new ItemStack(Material.IRON_SWORD)),
    BEDWARS_STYLE(false, new ItemStack(Material.BED));
    private final boolean defaultState;
    private final ItemStack icon;

    Rule(boolean defaultState, ItemStack icon) {
        this.defaultState = defaultState;
        this.icon = icon;
    }
    public boolean getDefaultState() {
        return this.defaultState;
    }

    public ItemStack getIcon() {
        return icon;
    }
}