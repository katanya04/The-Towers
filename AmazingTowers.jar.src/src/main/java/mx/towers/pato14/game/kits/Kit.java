package mx.towers.pato14.game.kits;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    private final String name;
    private final ItemStack[] armor;
    private final ItemStack[] hotbar;
    private final int price;
    private final boolean permanent;
    private final ItemStack iconInMenu;

    public Kit(String name, ItemStack[] armor, ItemStack[] hotbar, int price, boolean permanent, ItemStack iconInMenu) {
        this.name = name.trim();
        this.armor = armor;
        this.hotbar = hotbar;
        this.price = price;
        this.permanent = permanent;
        this.iconInMenu = setIcon(iconInMenu, true);
    }

    public Kit(String name, ItemStack[] armor, ItemStack[] hotbar, ItemStack iconInMenu) {
        this.name = name.trim();
        this.armor = armor;
        this.hotbar = hotbar;
        this.iconInMenu = setIcon(iconInMenu, false);

        this.price = 0;
        this.permanent = true;
    }

    private ItemStack setIcon(ItemStack item, boolean addLore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(AmazingTowers.getColor("§r&l" + this.name));
        if (addLore) {
            List<String> lore = new ArrayList<>();
            lore.add(this.price + " coins");
            lore.add(permanent ? "Usos ilimitados" : "Comprar 1 uso");
            meta.setLore(lore);
        }
            item.setItemMeta(meta);
        return item;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    public ItemStack[] getHotbar() {
        return this.hotbar;
    }

    public ItemStack getIconInMenu() {
        return iconInMenu;
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


