package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.ChestMenuItem;
import mx.towers.pato14.game.items.menus.settings.*;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ModifyGameSettings extends ChestMenuItem {
    private final GameInstance gameInstance;
    private SetRules setRules;
    private SaveSettings saveSettings;
    public ModifyGameSettings(GameInstance gameInstance) {
        super(
                AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.modifyGameSettings.name")),
                Utils.setName(new ItemStack(Material.PAPER), AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.modifyGameSettings.name"))),
                new HashMap<>(),
                27
        );
        this.gameInstance = gameInstance;
        this.setContents(getSettings());
    }

    private Map<Integer, ItemStack> getSettings() {
        HashMap<Integer, ItemStack> toret = new HashMap<>();
        toret.put(10, setRules = new SetRules(this.gameInstance));
        toret.put(3, new SetWhitelist(this.gameInstance));
        toret.put(12, new KickAll(this.gameInstance));
        toret.put(21, new SetBlacklist(this.gameInstance));
        toret.put(7, new StopCount(this.gameInstance));
        toret.put(16, new ContinueCount(this.gameInstance));
        toret.put(25, new StartImmediately(this.gameInstance));
        toret.put(14, new SetTimer(this.gameInstance));
        toret.put(26, saveSettings = new SaveSettings(this.gameInstance));

        return toret;
    }

    public SetRules getSetRules() {
        return setRules;
    }

    public SaveSettings getSaveSettings() {
        return saveSettings;
    }
}