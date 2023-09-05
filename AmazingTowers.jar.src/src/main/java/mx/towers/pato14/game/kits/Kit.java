package mx.towers.pato14.game.kits;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.game.items.menus.BuyKitMenu;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;

public class Kit extends ActionItem {
    private final String name;
    private final ItemStack[] armor;
    private final ItemStack[] hotbar;
    private final int price;
    private final boolean permanent;

    public Kit(String name, ItemStack[] armor, ItemStack[] hotbar, int price, boolean permanent, ItemStack iconInMenu) {
        super(iconInMenu);
        this.name = name.trim();
        this.armor = armor;
        this.hotbar = hotbar;
        this.price = price;
        this.permanent = permanent;
    }

    public Kit(String name, ItemStack[] armor, ItemStack[] hotbar, ItemStack iconInMenu) {
        this(name, armor, hotbar, 0, true, iconInMenu);
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        Config messages = gameInstance.getConfig(ConfigType.MESSAGES);
        if (!gameInstance.getRules().get(Rule.KITS)) {
            player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("kitsDisabled")));
            player.closeInventory();
            return;
        }
        if (!AmazingTowers.capitalismExists() || this.getPrice() == 0 || gameInstance.getGame().getKits().playerHasKit(player.getName(), this)) {
            gameInstance.getGame().getPlayersSelectedKit().put(player, this);
            player.sendMessage(AmazingTowers.getColor(messages.getString("selectKit")
                    .replace("%kitName%", this.getName())));
            ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
            player.closeInventory();
        } else if (SetupVault.getCoins((Player) player) >= this.getPrice()) {
            BuyKitMenu buyKitMenu = new BuyKitMenu(gameInstance, this, player);
            gameInstance.getHotbarItems().getChestMenus().add(buyKitMenu);
            buyKitMenu.interact(player, gameInstance);
        } else {
            player.sendMessage(AmazingTowers.getColor(messages.getString("notEnoughMoney")));
            ((Player) player).playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
        }
    }

    public String getName() {
        return this.name;
    }

    public Kit getIconInMenu() {
        return this;
    }

    public void applyKitToPlayer(HumanEntity player) {
        for (int i = 0; i < hotbar.length; i++) {
            player.getInventory().setItem(i, hotbar[i]);
        }
        Color color = AmazingTowers.getGameInstance(player).getGame().getTeams()
                .getTeamColorByPlayer(player.getName()).getColorEnum();
        for (ItemStack itemStack : armor) {
            if (itemStack != null && Utils.isLeatherArmor(itemStack.getType()) && color != null) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            }
        }
        player.getInventory().setArmorContents(armor);
    }

    public int getPrice() {
        return price;
    }

    public boolean isPermanent() {
        return permanent;
    }
}


