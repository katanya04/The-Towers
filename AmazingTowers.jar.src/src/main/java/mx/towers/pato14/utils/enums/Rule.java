package mx.towers.pato14.utils.enums;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import me.katanya04.anotherguiplugin.actionItems.ListItem;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    BEDWARS_STYLE(new ItemStack(Material.BED)),
    EXPLOSIVE_CHICKEN(new ItemStack(Material.EGG)),
    HUNGER(new ItemStack(Material.PORK));
    private final ItemStack icon;
    Rule(ItemStack icon) {
        this.icon = icon;
    }
    public ItemStack getIcon() {
        return icon;
    }
    public static void createAllActionItems() {
        for (Rule rule : Rule.values())
            createActionItem(rule);
    }
    public static void createActionItem(Rule rule) {
        List<String> list = Collections.unmodifiableList(Arrays.asList("§cFalse", "§aTrue"));
        ListItem ruleItem = new ListItem(
                Utils.setName(rule.getIcon(), "§r" + Utils.macroCaseToItemName(rule.name())), list,
                player -> AmazingTowers.getGameInstance(player).getRules().get(rule) ? 1 : 0, false, "RuleItem." + rule
        );
        ruleItem.setOnInteract(event -> changeRuleValue(AmazingTowers.getGameInstance(event.getPlayer()), rule));
    }
    public static void changeRuleValue(GameInstance gameInstance, Rule rule) {
        gameInstance.getRules().put(rule, !gameInstance.getRules().get(rule));
        gameInstance.getConfig(ConfigType.GAME_SETTINGS).set("rules." + Utils.macroCaseToCamelCase(rule.name()), gameInstance.getRules().get(rule).toString().toLowerCase());
        gameInstance.setFlagChanges(true);
    }
    public static ItemStack[] getRuleItems() {
        ItemStack[] toret = new ItemStack[Rule.values().length];
        int i = 0;
        for (Rule rule : Rule.values())
            toret[i++] = ActionItem.getByName("RuleItem." + rule).returnPlaceholder();
        return toret;
    }
}