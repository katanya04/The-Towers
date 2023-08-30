package mx.towers.pato14.game.items;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LobbyParkourPrize extends ItemStack {
    public LobbyParkourPrize(LobbyInstance lobbyInstance) {
        super(Utils.colorArmor(Utils.addGlint(Utils.setLore(Utils.setName(new ItemStack(Material.LEATHER_CHESTPLATE), AmazingTowers.getColor("&r" + lobbyInstance.getConfig(ConfigType.MESSAGES)
                .getString("lobbyParkourPrize.name"))), (Arrays.stream(AmazingTowers.getColor(lobbyInstance.getConfig(ConfigType.MESSAGES)
                .getString("lobbyParkourPrize.lore")).split("\n")).collect(Collectors.toList())))), Color.RED));
    }
}
