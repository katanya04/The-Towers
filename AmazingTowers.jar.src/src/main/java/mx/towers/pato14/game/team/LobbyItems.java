package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class LobbyItems implements Listener {
    private final HashMap<Integer, ItemStack> hotbarItems;
    private final MenuItem selectTeam;
    private final MenuItem selectKit;
    private ItemStack quit;
    private final Game game;
    private final AmazingTowers plugin;

    public LobbyItems(Game game) {
        this.game = game;
        this.plugin = game.getGameInstance().getPlugin();
        ItemStack selectTeamIcon = setName(new ItemStack(Material.WOOL, 1, (short) 14), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.joinTeams.name")));
        this.selectTeam = new MenuItem(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.joinTeams.name")), 9, selectTeamIcon, game.getTeams().getLobbyItems());
        ItemStack selectKitIcon = setName(new ItemStack(Material.IRON_SWORD), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.kits.name")));
        this.selectKit = new MenuItem(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.kits.name")), 9, selectKitIcon, game.getKits().getIcons());
        this.hotbarItems = new HashMap<>();
        if (getPlugin().getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
            this.quit = setName(new ItemStack(Material.BED), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.quit.name")));
            this.hotbarItems.put(game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("lobbyItems.hotbarItems.quit.position"), this.quit);
        }
        this.hotbarItems.put(game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("lobbyItems.hotbarItems.joinTeams.position"), this.selectTeam);
        this.hotbarItems.put(game.getGameInstance().getConfig(ConfigType.CONFIG).getInt("lobbyItems.hotbarItems.kits.position"), this.selectKit);
    }

    public void giveHotbarItems(HumanEntity player) {
        for (Map.Entry<Integer, ItemStack> item : this.hotbarItems.entrySet()) {
            player.getInventory().setItem(item.getKey(), item.getValue());
        }
    }

    private static ItemStack setName(ItemStack item, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    public boolean canUseLobbyItem(HumanEntity player) {
        return player.getGameMode().equals(GameMode.ADVENTURE) && this.game.equals(plugin.getGameInstance(player).getGame()) &&
                plugin.getGameInstance(player).getGame() != null && plugin.getGameInstance(player) != null;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!canUseLobbyItem(player))
            return;
        if (game.getGameState().equals(GameState.FINISH))
            return;
        if (e.getItem() == null || !(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;
        for (ItemStack item : hotbarItems.values()) {
            if (!item.equals(e.getItem()))
                continue;
            if (item instanceof MenuItem && game.getGameState() != GameState.FINISH)
                player.openInventory(((MenuItem) item).getMenu());
            else if (item.equals(this.quit))
                Dar.bungeecordTeleport(player);
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR))
            return;
        HumanEntity player = e.getWhoClicked();
        if (!canUseLobbyItem(e.getWhoClicked()))
            return;
        e.setCancelled(true);
        Config messages = this.game.getGameInstance().getConfig(ConfigType.MESSAGES);
        if (this.selectTeam.getMenu().equals(e.getClickedInventory()) && game.getGameState() != GameState.FINISH) {
            Team currentTeam = game.getTeams().getTeamByPlayer(player); //Equipo actual
            Team teamToJoin = getTeams().getTeamFromLobbyItem(clickedItem);
            if (teamToJoin == null) {
                if (TeamColor.isSpectatorItem(clickedItem, game.getGameInstance())) {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(AmazingTowers.getColor(messages.getString("enterSpectatorMode").replace("%newLine%", "\n")));
                    if (currentTeam != null) {
                        currentTeam.removePlayer(player);
                    }
                    player.closeInventory();
                }
            } else if (!teamToJoin.containsPlayer(player.getName())) { //Si no está ya en ese equipo
                if (!game.getGameInstance().getRules().get(Rule.BALANCED_TEAMS)
                        || teamToJoin.getSizePlayers() == game.getTeams().getLowestTeamPlayers()) {
                    if (currentTeam != null)
                        currentTeam.removePlayer(player);
                    teamToJoin.addPlayer(player);
                    if (game.getGameState().equals(GameState.GAME))
                        Dar.darItemsJoinTeam((Player) player);
                    player.sendMessage(AmazingTowers.getColor(messages.getString("selectTeam")
                            .replace("{Color}", teamToJoin.getTeamColor().getColor())
                            .replace("{Team}", teamToJoin.getTeamColor().getName(game.getGameInstance()))));
                    ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    player.closeInventory();
                } else {
                    player.sendMessage(AmazingTowers.getColor(messages.getString("unbalancedTeam")));
                    ((Player) player).playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
                }
            } else {
                player.sendMessage(AmazingTowers.getColor(messages.getString("alreadyJoinedTeam")
                        .replace("{Color}", teamToJoin.getTeamColor().getColor())
                        .replace("{Team}", teamToJoin.getTeamColor().getName(game.getGameInstance()))));
            }

        } else if (this.selectKit.getMenu().equals(e.getClickedInventory())) {
            Kit selectedKit = game.getKits().get(clickedItem);
            if (selectedKit == null)
                return;
            if (!plugin.capitalismExists() || selectedKit.getPrice() == 0 || game.getKits().playerHasKit(player.getName(), selectedKit)) {
                game.getPlayersSelectedKit().put(player, selectedKit);
                player.sendMessage(AmazingTowers.getColor(messages.getString("selectKit")
                        .replace("%kitName%", selectedKit.getName())));
                ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                player.closeInventory();
            } else if (game.getGameInstance().getVault().getCoins((Player) player) >= selectedKit.getPrice()) {
                player.openInventory(openMenuBuyKit(selectedKit, AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.buyKitMenuName"))));
            } else {
                player.sendMessage(AmazingTowers.getColor(messages.getString("notEnoughMoney")));
                ((Player) player).playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
            }
        } else if (e.getClickedInventory().getName().equals(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.buyKitMenuName")))) {
            if (clickedItem.getItemMeta().getDisplayName().equals(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.acceptBuy")))) {
                Kit kit = game.getKits().get(e.getClickedInventory().getItem(4));
                if (kit.isPermanent())
                    game.getKits().addKitToPlayer(player.getName(), kit);
                else
                    game.getKits().addTemporalBoughtKitToPlayer(player.getName(), kit);
                player.sendMessage(AmazingTowers.getColor(messages.getString("buyKit").replace("%kitName%", kit.getName())));
                SetupVault.getVaultEconomy().withdrawPlayer((OfflinePlayer) player, kit.getPrice());
                player.openInventory(selectKit.getMenu());
                ((Player) player).playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2.0f);
            } else if (clickedItem.getItemMeta().getDisplayName().equals(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.denyBuy")))) {
                player.openInventory(selectKit.getMenu());
            }
        }
    }

    private Inventory openMenuBuyKit(Kit kit, String name) {
        Inventory menu = Bukkit.createInventory(null, 27, name);
        menu.setItem(4, kit.getIconInMenu());
        menu.setItem(21, setName(new ItemStack(Material.WOOL, 1, (short) 5), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.acceptBuy"))));
        menu.setItem(23, setName(new ItemStack(Material.WOOL, 1, (short) 14), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.denyBuy"))));
        return menu;
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (canUseLobbyItem(e.getWhoClicked()))
            e.setCancelled(true);
    }

    public ItemStack getItemQuit() {
        return this.quit;
    }

    private GameTeams getTeams() {
        return this.game.getTeams();
    }

    private AmazingTowers getPlugin() {
        return this.plugin;
    }

    public MenuItem getSelectTeam() {
        return selectTeam;
    }

    public MenuItem getSelectKit() {
        return selectKit;
    }

    public void updateTeamsMenu() {
        this.selectTeam.setContents(getTeams().getLobbyItems());
    }
}