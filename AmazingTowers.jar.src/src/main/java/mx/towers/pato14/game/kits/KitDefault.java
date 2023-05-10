package mx.towers.pato14.game.kits;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class KitDefault {
    private static final AmazingTowers plugin = AmazingTowers.getPlugin();

    public static void KitDe(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemStack sword = new ItemStack(Material.WOOD_SWORD);
        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE);
        ItemStack food = new ItemStack(Material.BAKED_POTATO, 20);
        ItemStack blocks = new ItemStack(Material.COBBLESTONE, 32);
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, pickaxe);
        player.getInventory().setItem(8, food);
        player.getInventory().setItem(2, blocks);
        LeatherArmorMeta hmeta = (LeatherArmorMeta) helmet.getItemMeta();
        LeatherArmorMeta cmeta = (LeatherArmorMeta) chestplate.getItemMeta();
        LeatherArmorMeta lmeta = (LeatherArmorMeta) leggings.getItemMeta();
        LeatherArmorMeta bmeta = (LeatherArmorMeta) boots.getItemMeta();
        Color color = plugin.getGameInstance(player).getGame().getTeams().getTeamColorByPlayer(player).getColorEnum();
        if (color != null) {
            hmeta.setColor(color);
            cmeta.setColor(color);
            lmeta.setColor(color);
            bmeta.setColor(color);
        }
        helmet.setItemMeta(hmeta);
        chestplate.setItemMeta(cmeta);
        leggings.setItemMeta(lmeta);
        boots.setItemMeta(bmeta);
        player.getInventory().setArmorContents(new ItemStack[]{boots, leggings, chestplate, helmet});
    }
}


