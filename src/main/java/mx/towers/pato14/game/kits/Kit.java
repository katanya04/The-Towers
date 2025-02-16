package mx.towers.pato14.game.kits;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import me.katanya04.anotherguiplugin.actionItems.InteractionType;
import me.katanya04.anotherguiplugin.menu.ChestMenu;
import me.katanya04.anotherguiplugin.menu.InventoryMenu;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.items.Items;
import mx.towers.pato14.utils.items.ItemsEnum;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Kit {
    private final static ActionItem<Player> kit = new ActionItem<>(pl -> new ItemStack(Material.PAPER), null, ItemsEnum.KIT.name);
    private final static ChestMenu changeHotbar = new ChestMenu("", new ItemStack[9], true, InventoryMenu.SaveOption.INDIVIDUAL, false);

    static {
        changeHotbar.setOnClickBehaviour(event -> {
            if (event.getSlotType() == InventoryType.SlotType.OUTSIDE || !event.getClickedInventory().equals(event.getInventory())
                    || event.getClick().isShiftClick() || event.getClick().isKeyboardClick())
                event.setCancelled(true);
        });
        changeHotbar.setOnDragBehaviour(event -> {
            if (event.getRawSlots().stream().anyMatch(o -> o > event.getInventory().getSize()))
                event.setCancelled(true);
        });
        changeHotbar.setRetrieveItemOnCursorOnClose(true);
    }

    private final static ChestMenu buyKitMenu = new ChestMenu("buyKitMenu", new ItemStack[9 * 3]);

    static {
        kit.setOnInteract(event -> {
            Player player = event.getPlayer();
            GameInstance game = AmazingTowers.getGameInstance(player);
            if (game == null || game.getGame() == null)
                return;
            Config messages = game.getConfig(ConfigType.MESSAGES);
            if (!game.getRules().get(Rule.KITS)) {
                player.sendMessage(Utils.getColor(messages.getString("kitsDisabled")));
                player.closeInventory();
                return;
            }
            Kit selectedKit = Kits.getByIcon(event.getItem());
            if (selectedKit == null)
                return;
            if (event.getInteractionType() == InteractionType.LEFT_CLICK) {
                if (selectedKit.playerHasKit(player, game)) {
                    game.getGame().getPlayersSelectedKit().put(player.getName(), selectedKit);
                    player.sendMessage(Utils.getColor(messages.getString("selectKit").replace("%kitName%", selectedKit.getName())));
                    player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    player.closeInventory();
                } else if (SetupVault.getCoins(player) >= selectedKit.getPrice()) {
                    ItemStack[] contents = new ItemStack[9 * 3];
                    contents[4] = selectedKit.iconInMenu;
                    contents[21] = Items.get(ItemsEnum.ACCEPT_BUY);
                    contents[23] = Items.get(ItemsEnum.DENY_BUY);
                    buyKitMenu.setContents(contents);
                    buyKitMenu.openMenu(player);
                } else {
                    player.sendMessage(Utils.getColor(messages.getString("notEnoughMoney")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
                }
            } else if (event.getInteractionType() == InteractionType.RIGHT_CLICK) {
                if (!selectedKit.playerHasKit(player, game)) {
                    player.sendMessage(Utils.getColor(messages.getString("tryingToEditNotOwnedKit")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
                } else {
                    changeHotbar.setParent(kit.getParent());
                    changeHotbar.setGUIName(selectedKit.getName());
                    changeHotbar.setContents(selectedKit.getHotbar(player));
                    changeHotbar.openMenu(player);
                }
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

    public void applyKitToPlayer(Player player) {
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
        ItemStack[] items = getHotbar(player);
        int i = 0;
        for (ItemStack itemStack : items) {
            if (itemStack != null && color != null) {
                if (itemStack.getType().equals(Material.GLASS) || itemStack.getType().equals(Material.STAINED_GLASS))
                    items[i] = new ItemStack(Material.STAINED_GLASS, items[i].getAmount(), color.getWoolColor());
                else if (itemStack.getType().equals(Material.THIN_GLASS) || itemStack.getType().equals(Material.STAINED_GLASS_PANE))
                    items[i] = new ItemStack(Material.STAINED_GLASS_PANE, items[i].getAmount(), color.getWoolColor());
            }
            i++;
        }
        for (i = 0; i < items.length; i++) {
            player.getInventory().setItem(i, items[i]);
        }
    }

    public ItemStack[] getHotbar(Player player) {
        @SuppressWarnings("deprecation")
        ItemStack[] hotbar = InventoryMenu.getSavedMenu(player, null, this.getName());
        if (hotbar == null) {
            hotbar = this.hotbar;
        }
        return hotbar;
    }

    @SuppressWarnings("deprecation")
    public void resetHotbar(Player player) {
        InventoryMenu.resetPlayerSave(player.getName(), null, this.getName());
    }

    public int getPrice() {
        return price;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public boolean playerHasKit(Player player, GameInstance game) {
        return !AmazingTowers.capitalismExists() || this.getPrice() == 0 || game.getGame().getKits().playerHasKit(player.getName(), this);
    }
}