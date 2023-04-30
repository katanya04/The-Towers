package mx.towers.pato14.utils.wand;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Wand {
    private String[] region;
    private ItemStack wand;

    public Wand() {
        this.region = new String[2];
        this.region[0] = "";
        this.region[1] = "";
        this.wand = new ItemStack(Material.IRON_AXE);
        ItemMeta wandMeta = this.wand.getItemMeta();
        wandMeta.setDisplayName("§aRegion Selector");
        this.wand.setItemMeta(wandMeta);
    }

    public void clearPos1() {
        if (!equalsPos1("")) {
            this.region[0] = "";
        }
    }

    public void clearPos2() {
        if (!equalsPos2("")) {
            this.region[1] = "";
        }
    }

    public void clearStrings() {
        if (!equalsPos1("") && !equalsPos2("")) {
            this.region[0] = "";
            this.region[1] = "";
        }
    }

    public boolean equalsPos1(String pos1) {
        return this.region[0].equals(pos1);
    }

    public boolean equalsPos2(String pos1) {
        return this.region[1].equals(pos1);
    }

    public void setPos1(String pos1) {
        this.region[0] = pos1;
    }

    public void setPos2(String pos2) {
        this.region[1] = pos2;
    }

    public String[] getRegion() {
        return this.region;
    }

    public String getPos1() {
        return this.region[0];
    }

    public String getPos2() {
        return this.region[1];
    }

    public ItemStack getItem() {
        return this.wand;
    }
}


