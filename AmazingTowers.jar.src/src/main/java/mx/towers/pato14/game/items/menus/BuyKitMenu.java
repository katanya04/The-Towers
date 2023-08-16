package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.ChestInventoryItem;
import mx.towers.pato14.game.items.actions.AcceptBuy;
import mx.towers.pato14.game.items.actions.DenyBuy;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BuyKitMenu extends ChestInventoryItem {
    private final HumanEntity player;
    public BuyKitMenu(GameInstance gameInstance, Kit kit, HumanEntity player) {
        super(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.buyKitMenuName")), null, new HashMap<>(), 27);
        HashMap<Integer, ItemStack> contents = new HashMap<>();
        contents.put(4, kit);
        contents.put(21, new AcceptBuy(gameInstance, kit));
        contents.put(23, new DenyBuy(gameInstance));
        this.setContents(contents);
        this.player = player;
    }

    public HumanEntity getPlayer() {
        return player;
    }
}
