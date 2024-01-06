package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ListItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class SelectDatabase extends ListItem<String> {
    public SelectDatabase(GameInstance gameInstance) {
        super(Utils.setName(new ItemStack(Material.BEACON), Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.selectStatsDB"))),
                AmazingTowers.connexion.getTables(), AmazingTowers.connexion.getTables().indexOf(gameInstance.getTableName()), true);
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        super.interact(player, instance);
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = ((GameInstance) instance);
        gameInstance.setTableName(getCurrentItem());
        Utils.addGlint(gameInstance.getHotbarItems().getModifyGameSettings().getSaveSettings());
        gameInstance.getHotbarItems().getModifyGameSettings().updateMenu();
    }
}
