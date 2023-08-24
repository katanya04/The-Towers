package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.MessageType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class SaveSettings extends ActionItem {
    public SaveSettings(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8),
                AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.saveSettings.name"))));
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        super.interact(player, gameInstance);
        if (!Utils.hasGlint(this)) {
            Utils.sendMessage(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.saveSettings.noChangesMessage"), MessageType.ERROR, player);
            return;
        }
        gameInstance.getConfig(ConfigType.GAME_SETTINGS).saveConfig();
        Utils.sendMessage(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.saveSettings.saveMessage"), MessageType.INFO, player);
        Utils.removeGlint(this);
        gameInstance.getHotbarItems().getModifyGameSettings().updateMenu();
    }
}
