package mx.towers.pato14.game.items.actions;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AcceptBuy extends ActionItem {
    private final Kit kit;
    public AcceptBuy(GameInstance gameInstance, Kit kit) {
        super(Utils.setName(new ItemStack(Material.WOOL, 1, (short) 5), AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.acceptBuy"))));
        this.kit = kit;
    }

    @Override
    public void interact(HumanEntity player, GameInstance gameInstance) {
        if (kit.isPermanent())
            gameInstance.getGame().getKits().addKitToPlayer(player.getName(), kit);
        else
            gameInstance.getGame().getKits().addTemporalBoughtKitToPlayer(player.getName(), kit);
        player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("buyKit").replace("%kitName%", kit.getName())));
        SetupVault.getVaultEconomy().withdrawPlayer((OfflinePlayer) player, kit.getPrice());
        player.openInventory(gameInstance.getGame().getLobbyItems().getSelectKit().getMenu());
        ((Player) player).playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2.0f);
    }
}
