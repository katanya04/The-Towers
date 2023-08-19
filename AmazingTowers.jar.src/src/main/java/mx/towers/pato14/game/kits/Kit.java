package mx.towers.pato14.game.kits;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.game.items.menus.BuyKitMenu;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

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
        super(iconInMenu);
        this.name = name.trim();
        this.armor = armor;
        this.hotbar = hotbar;
        this.price = 0;
        this.permanent = true;
    }

    public void interact(HumanEntity player, GameInstance gameInstance) {
        Config messages = gameInstance.getConfig(ConfigType.MESSAGES);
        if (!gameInstance.getRules().get(Rule.KITS)) {
            player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("kitsDisabled")));
            player.closeInventory();
            return;
        }
        if (!AmazingTowers.getPlugin().capitalismExists() || this.getPrice() == 0 || gameInstance.getGame().getKits().playerHasKit(player.getName(), this)) {
            gameInstance.getGame().getPlayersSelectedKit().put(player, this);
            player.sendMessage(AmazingTowers.getColor(messages.getString("selectKit")
                    .replace("%kitName%", this.getName())));
            ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
            player.closeInventory();
        } else if (gameInstance.getVault().getCoins((Player) player) >= this.getPrice()) {
            BuyKitMenu buyKitMenu = new BuyKitMenu(gameInstance, this, player);
            gameInstance.getGame().getLobbyItems().getChestMenus().add(buyKitMenu);
            buyKitMenu.interact(player, gameInstance);
        } else {
            player.sendMessage(AmazingTowers.getColor(messages.getString("notEnoughMoney")));
            ((Player) player).playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
        }
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getIconInMenu() {
        return this;
    }

    public void applyKitToPlayer(HumanEntity player) {
        for (int i = 0; i < hotbar.length; i++) {
            player.getInventory().setItem(i, hotbar[i]);
        }
        Color color = AmazingTowers.getPlugin().getGameInstance(player).getGame().getTeams()
                .getTeamColorByPlayer(player.getName()).getColorEnum();
        for (ItemStack itemStack : armor) {
            if (isLeatherArmor(itemStack.getType()) && color != null) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            }
        }
        player.getInventory().setArmorContents(armor);
    }

    private boolean isLeatherArmor(Material material) {
        return material.equals(Material.LEATHER_HELMET) || material.equals(Material.LEATHER_CHESTPLATE)
                || material.equals(Material.LEATHER_LEGGINGS) || material.equals(Material.LEATHER_BOOTS);
    }

    public int getPrice() {
        return price;
    }

    public boolean isPermanent() {
        return permanent;
    }
}


