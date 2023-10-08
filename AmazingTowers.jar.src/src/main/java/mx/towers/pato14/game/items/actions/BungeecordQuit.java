package mx.towers.pato14.game.items.actions;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.LobbyInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BungeecordQuit extends ActionItem {
    public BungeecordQuit(LobbyInstance lobbyInstance) {
        super(Utils.setName(new ItemStack(Material.BED), AmazingTowers.getColor(lobbyInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.quit.name"))));
    }

    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        super.interact(player, instance);
        Utils.bungeecordTeleport((Player) player);
    }
}
