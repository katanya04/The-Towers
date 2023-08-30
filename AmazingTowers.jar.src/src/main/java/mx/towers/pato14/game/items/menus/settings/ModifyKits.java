package mx.towers.pato14.game.items.menus.settings;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ModifyKits extends BookMenuItem {
    public ModifyKits(TowersWorldInstance instance) {
        super(Utils.setName(new ItemStack(Material.IRON_SWORD), AmazingTowers.getColor(instance.getConfig(ConfigType.CONFIG).getString("settingsBook.modifyKits"))), ConfigType.KITS, instance, "Kits");
    }

    @Override
    protected boolean mapKeysAsList() {
        return true;
    }
}
