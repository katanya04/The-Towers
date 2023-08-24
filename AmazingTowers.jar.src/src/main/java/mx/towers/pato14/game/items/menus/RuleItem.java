package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RuleItem extends ActionItem {
    private final Rule rule;
    public RuleItem(GameInstance gameInstance, Rule rule) {
        super(Utils.setLore(Utils.setName(rule.getIcon(), "§r" + Utils.macroCaseToItemName(rule.name())), gameInstance.getRules().get(rule) ? "§r§aTrue" : "§r§cFalse"));
        this.rule = rule;
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        super.interact(player, gameInstance);
        gameInstance.getRules().put(rule, !gameInstance.getRules().get(rule));
        gameInstance.getConfig(ConfigType.GAME_SETTINGS).set("rules." + Utils.macroCaseToCamelCase(rule.name()), gameInstance.getRules().get(rule).toString().toLowerCase());
        Utils.setLore(this, gameInstance.getRules().get(rule) ? "§r§aTrue" : "§r§cFalse");
        gameInstance.getHotbarItems().getModifyGameSettings().getSetRules().updateMenu();
        Utils.addGlint(gameInstance.getHotbarItems().getModifyGameSettings().getSaveSettings());
        gameInstance.getHotbarItems().getModifyGameSettings().updateMenu();
    }

    public static ItemStack[] createAllRules(GameInstance gameInstance) {
        List<ItemStack> toret = new ArrayList<>();
        for (Rule rule : Rule.values())
            toret.add(new RuleItem(gameInstance, rule));
        return toret.toArray(new ItemStack[0]);
    }
}
