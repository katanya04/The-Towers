package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.ChestInventoryItem;
import mx.towers.pato14.game.items.menus.settings.*;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ModifyGameSettings extends ChestInventoryItem {
    private final Game game;
    private SetRules setRules;
    public ModifyGameSettings(Game game) {
        super(
                AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.modifyGameSettings.name")),
                Utils.setName(new ItemStack(Material.PAPER), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.modifyGameSettings.name"))),
                new HashMap<>(),
                27
        );
        this.game = game;
        this.setContents(getSettings());
    }

    private Map<Integer, ItemStack> getSettings() {
        HashMap<Integer, ItemStack> toret = new HashMap<>();
        toret.put(10, setRules = new SetRules(this.game.getGameInstance()));
        toret.put(12, new SetWhitelist(this.game));
        toret.put(14, new KickAll(game));
        toret.put(7, new StopCount(game));
        toret.put(16, new ContinueCount(game));
        toret.put(25, new StartImmediately(game));
        toret.put(26, new SetTimer(game));

        return toret;
    }

    public SetRules getSetRules() {
        return setRules;
    }
}