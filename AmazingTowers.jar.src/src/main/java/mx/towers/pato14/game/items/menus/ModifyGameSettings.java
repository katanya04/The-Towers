package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
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
    private final Game game;
    private SetRules setRules;
    private SaveSettings saveSettings;
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
        toret.put(3, new SetWhitelist(this.game.getGameInstance()));
        toret.put(12, new KickAll(this.game.getGameInstance()));
        toret.put(21, new SetBlacklist(this.game.getGameInstance()));
        toret.put(7, new StopCount(game));
        toret.put(16, new ContinueCount(game));
        toret.put(25, new StartImmediately(game));
        toret.put(14, new SetTimer(game));
        toret.put(26, saveSettings = new SaveSettings(this.game.getGameInstance()));

        return toret;
    }

    public SetRules getSetRules() {
        return setRules;
    }

    public SaveSettings getSaveSettings() {
        return saveSettings;
    }
}