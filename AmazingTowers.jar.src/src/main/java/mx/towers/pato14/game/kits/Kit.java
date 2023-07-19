package mx.towers.pato14.game.kits;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Kit {
    private final String name;
    private final ItemStack[] armor;
    private final ItemStack[] itemsHotbar;

    public Kit(String name) {
        this.name = name;
        this.armor = new ItemStack[4];
        this.itemsHotbar = new ItemStack[9];
    }

    public void setArmor(List<String> armor) {
        if (armor.size() < 5) {
            for (String st : armor) {
                int i = st.length();
            }
        }
    }

    public String getNameKit() {
        return this.name;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    public ItemStack[] getItemsHotbar() {
        return this.itemsHotbar;
    }
}


