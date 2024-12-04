package mx.towers.pato14.game.events.protect;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import net.minecraft.server.v1_8_R3.Block;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class TntListener implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onTntExplode(EntityExplodeEvent e) throws NoSuchFieldException {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getEntity());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (!gameInstance.getRules().get(Rule.TNT)) {
            e.setCancelled(true);
            return;
        }
        Class<Block> blockClass = Block.class;
        Field durabilityField = blockClass.getDeclaredField("durability");
        durabilityField.setAccessible(true);
        if (e.getEntityType().equals(EntityType.PRIMED_TNT) || e.getEntityType().equals(EntityType.MINECART_TNT)) {
            e.blockList().removeIf(block -> {
                try {
                    return (((float)durabilityField.get(Block.getById(block.getTypeId()))/7 + 0.5)
                            + (block.getLocation().distance(e.getLocation())) > 6.5);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        durabilityField.setAccessible(false);
    }
    @EventHandler
    public void onPlaceBlocks(BlockPlaceEvent e) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(e.getPlayer());
        if (gameInstance == null || gameInstance.getGame() == null)
            return;
        if (e.getBlockPlaced().getType().equals(Material.TNT)) {
            if (!gameInstance.getRules().get(Rule.TNT)) {
                e.setCancelled(true);
                return;
            }
            Player p = e.getPlayer();
            e.setCancelled(true);
            p.getWorld().spawnEntity(e.getBlock().getLocation().add(0.5d,0d,0.5d), EntityType.PRIMED_TNT);
            if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                if (p.getInventory().getItemInHand().getAmount() == 1) {
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR));
                } else {
                    p.getInventory().getItemInHand().setAmount(p.getInventory().getItemInHand().getAmount() - 1);
                }
            }
        }
    }
}
