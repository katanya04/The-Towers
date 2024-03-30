package mx.towers.pato14.game.kits;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import me.katanya04.anotherguiplugin.menu.ChestMenu;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.items.ItemsEnum;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Kit {
    private final static ActionItem kit = new ActionItem(pl -> new ItemStack(Material.PAPER), null, ItemsEnum.KIT.name);
    private final static ChestMenu buyKitMenu = new ChestMenu("buyKitMenu", new ItemStack[9 * 3]);
    static {
        kit.setOnInteract(event -> {
            Player player = event.getPlayer();
            GameInstance game = AmazingTowers.getGameInstance(player);
            Kit selectedKit = Kits.getByIcon(event.getItem());
            if (selectedKit == null)
                return;
            Config messages = game.getConfig(ConfigType.MESSAGES);
            if (!game.getRules().get(Rule.KITS)) {
                player.sendMessage(Utils.getColor(messages.getString("kitsDisabled")));
                player.closeInventory();
            } else if (!AmazingTowers.capitalismExists() || selectedKit.getPrice() == 0 || game.getGame().getKits().playerHasKit(player.getName(), selectedKit)) {
                game.getGame().getPlayersSelectedKit().put(player, selectedKit);
                player.sendMessage(Utils.getColor(messages.getString("selectKit").replace("%kitName%", selectedKit.getName())));
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                player.closeInventory();
            } else if (SetupVault.getCoins(player) >= selectedKit.getPrice()) {
                ItemStack[] contents = new ItemStack[9 * 3];
                contents[4] = selectedKit.iconInMenu;
                contents[21] = ItemsEnum.ACCEPT_BUY.getItem(player);
                contents[23] = ItemsEnum.DENY_BUY.getItem(player);
                buyKitMenu.setContents(contents);
                buyKitMenu.openMenu(player);
            } else {
                player.sendMessage(Utils.getColor(messages.getString("notEnoughMoney")));
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
            }
        });
    }
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
        this.iconInMenu = iconInMenu;
    }

    public Kit(String name, ItemStack[] armor, ItemStack[] hotbar, ItemStack iconInMenu) {
        this(name, armor, hotbar, 0, true, iconInMenu);
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getIconInMenu() {
        return this.iconInMenu;
    }

    public void applyKitToPlayer(HumanEntity player) {
        TeamColor color = AmazingTowers.getGameInstance(player).getGame().getTeams()
                .getTeamColorByPlayer(player.getName());
        for (ItemStack itemStack : armor) {
            if (itemStack != null && Utils.isLeatherArmor(itemStack.getType()) && color != null) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color.getColorEnum());
                itemStack.setItemMeta(meta);
            }
        }
        player.getInventory().setArmorContents(armor);
        int i = 0;
        for (ItemStack itemStack : hotbar) {
            if (itemStack != null && color != null) {
                if (itemStack.getType().equals(Material.GLASS) || itemStack.getType().equals(Material.STAINED_GLASS))
                    hotbar[i] = new ItemStack(Material.STAINED_GLASS, hotbar[i].getAmount(), color.getWoolColor());
                else if (itemStack.getType().equals(Material.THIN_GLASS) || itemStack.getType().equals(Material.STAINED_GLASS_PANE))
                    hotbar[i] = new ItemStack(Material.STAINED_GLASS_PANE, hotbar[i].getAmount(), color.getWoolColor());
            }
            i++;
        }
        for (i = 0; i < hotbar.length; i++) {
            player.getInventory().setItem(i, hotbar[i]);
        }
    }

    public int getPrice() {
        return price;
    }

    public boolean isPermanent() {
        return permanent;
    }
}