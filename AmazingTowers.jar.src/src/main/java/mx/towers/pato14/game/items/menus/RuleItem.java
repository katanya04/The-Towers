package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RuleItem extends ActionItem {
    private final Rule rule;
    public RuleItem(GameInstance gameInstance, Rule rule) {
        super(Utils.setName(rule.getIcon(), "§r" + Utils.macroCaseToItemName(rule.name()) + ":" +
                (gameInstance.getRules().get(rule) ? "§a true" : "§c false")));
        this.rule = rule;
    }

    @Override
    public void interact(HumanEntity player, GameInstance gameInstance) {
        super.interact(player, gameInstance);
        gameInstance.getRules().put(rule, !gameInstance.getRules().get(rule));
        gameInstance.getGame().getLobbyItems().getModifyGameSettings().getSetRules().getMenu().setContents(RuleItem.createAllRules(gameInstance));
    }

    public static ItemStack[] createAllRules(GameInstance gameInstance) {
        List<ItemStack> toret = new ArrayList<>();
        for (Rule rule : Rule.values())
            toret.add(new RuleItem(gameInstance, rule));
        return toret.toArray(new ItemStack[0]);
    }
}
